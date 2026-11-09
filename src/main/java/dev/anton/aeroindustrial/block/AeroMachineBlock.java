package dev.anton.aeroindustrial.block;

import dev.anton.aeroindustrial.registry.AeroBlockEntities;
import dev.anton.aeroindustrial.registry.AeroItems;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.Containers;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public final class AeroMachineBlock extends Block implements EntityBlock {
    private final int tier;

    public AeroMachineBlock(Properties properties, int tier) {
        super(properties);
        this.tier = tier;
    }

    public int tier() {
        return tier;
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock())) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof net.minecraft.world.Container container) {
                Containers.dropContents(level, pos, container);
            }
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new AeroMachineBlockEntity(pos, state);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide) return null;
        return type == AeroBlockEntities.AERO_MACHINE.get()
                ? (BlockEntityTicker<T>) (level1, pos, state1, be) -> ((AeroMachineBlockEntity) be).serverTick()
                : null;
    }

    @Override
    public void appendHoverText(ItemStack stack, net.minecraft.world.item.Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        tooltip.add(Component.translatable("tooltip.aeroindustrial.machine." + tier)
                .withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("tooltip.aeroindustrial.machine.tier", tier)
                .withStyle(ChatFormatting.DARK_AQUA));
        tooltip.add(Component.translatable(
                "tooltip.aeroindustrial.machine.capacity",
                10000 + tier * 5000 + (tier == 11 ? 30000 : 0)
        ).withStyle(ChatFormatting.DARK_GRAY));
        tooltip.add(Component.translatable("tooltip.aeroindustrial.machine.common")
                .withStyle(ChatFormatting.DARK_GRAY));
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player,
                                               BlockHitResult hit) {
        if (!level.isClientSide && level.getBlockEntity(pos) instanceof AeroMachineBlockEntity machine) {
            if (player.isShiftKeyDown()) {
                player.displayClientMessage(machine.statusLine().copy().withStyle(ChatFormatting.AQUA), true);
            } else if (player instanceof ServerPlayer serverPlayer) {
                serverPlayer.openMenu(machine, buffer -> buffer.writeBlockPos(pos));
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                               Player player, InteractionHand hand, BlockHitResult hit) {
        if (stack.is(AeroItems.AEROFLUX_CRYSTAL.get())) {
            if (!level.isClientSide) {
                BlockEntity blockEntity = level.getBlockEntity(pos);
                if (blockEntity instanceof AeroMachineBlockEntity machine) {
                    int accepted = machine.receiveEnergy(2500, false);
                    if (accepted > 0) {
                        level.playSound(null, pos, SoundEvents.BEACON_POWER_SELECT, SoundSource.BLOCKS, 0.6f, 1.2f);
                        if (!player.isCreative()) stack.shrink(1);
                        player.displayClientMessage(Component.translatable(
                                "message.aeroindustrial.charge", accepted, machine.getEnergy(), machine.getMaxEnergy())
                                .withStyle(ChatFormatting.GREEN), true);
                    } else {
                        player.displayClientMessage(Component.translatable("message.aeroindustrial.full")
                                .withStyle(ChatFormatting.RED), true);
                    }
                }
            }
            return level.isClientSide ? ItemInteractionResult.CONSUME : ItemInteractionResult.SUCCESS;
        }

        if (!level.isClientSide && level.getBlockEntity(pos) instanceof AeroMachineBlockEntity machine
                && player instanceof ServerPlayer serverPlayer) {
            serverPlayer.openMenu(machine, buffer -> buffer.writeBlockPos(pos));
        }

        return level.isClientSide ? ItemInteractionResult.SUCCESS : ItemInteractionResult.CONSUME;
    }
}
