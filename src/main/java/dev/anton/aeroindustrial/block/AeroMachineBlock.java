package dev.anton.aeroindustrial.block;

import dev.anton.aeroindustrial.registry.AeroItems;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;\nimport net.minecraft.world.level.block.entity.BlockEntityTicker;\nimport net.minecraft.world.level.block.entity.BlockEntityType;\nimport dev.anton.aeroindustrial.registry.AeroBlockEntities;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;

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
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new AeroMachineBlockEntity(pos, state);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player,
                                               net.minecraft.world.phys.BlockHitResult hit) {
        if (!level.isClientSide) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof AeroMachineBlockEntity machine) {
                player.displayClientMessage(Component.literal(machine.statusLine())
                        .withStyle(ChatFormatting.AQUA), true);
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                         Player player, net.minecraft.world.InteractionHand hand,
                                         net.minecraft.world.phys.BlockHitResult hit) {
        if (stack.is(AeroItems.AEROFLUX_CRYSTAL.get())) {
            if (!level.isClientSide) {
                BlockEntity blockEntity = level.getBlockEntity(pos);
                if (blockEntity instanceof AeroMachineBlockEntity machine) {
                    int accepted = machine.receiveEnergy(2500, false);
                    if (accepted > 0) {
                        if (!player.isCreative()) stack.shrink(1);
                        player.displayClientMessage(Component.literal(
                                "AeroFlux +" + accepted + " | " + machine.getEnergy() + "/" + machine.getMaxEnergy())
                                .withStyle(ChatFormatting.GREEN), true);
                    } else {
                        player.displayClientMessage(Component.literal("AeroFlux reservoir is full")
                                .withStyle(ChatFormatting.RED), true);
                    }
                }
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hit);
    }
}
