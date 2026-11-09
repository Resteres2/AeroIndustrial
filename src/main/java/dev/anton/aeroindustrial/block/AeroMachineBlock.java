package dev.anton.aeroindustrial.block;

import dev.anton.aeroindustrial.registry.AeroItems;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;

public final class AeroMachineBlock extends Block {
    private final int tier;

    public AeroMachineBlock(Properties properties, int tier) {
        super(properties);
        this.tier = tier;
    }

    public int tier() {
        return tier;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, net.minecraft.world.phys.BlockHitResult hit) {
        if (!level.isClientSide) {
            boolean create = nearbyModBlock(level, pos, "create");
            boolean gtceu = nearbyModBlock(level, pos, "gtceu");
            boolean aero = nearbyModBlock(level, pos, "aeronautics");
            player.displayClientMessage(Component.literal(
                    "AeroFlux machine T" + tier + " | Create=" + create + " | GTCEu=" + gtceu + " | Aeronautics=" + aero)
                    .withStyle(ChatFormatting.AQUA), true);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                         Player player, net.minecraft.world.InteractionHand hand, net.minecraft.world.phys.BlockHitResult hit) {
        if (stack.is(AeroItems.AEROFLUX_CRYSTAL.get())) {
            if (!level.isClientSide) {
                stack.shrink(1);
                ItemStack result = new ItemStack(AeroItems.PHASE_FOIL.get());
                if (!player.getInventory().add(result)) player.drop(result, false);
                player.displayClientMessage(Component.literal("Phase conversion complete").withStyle(ChatFormatting.GREEN), true);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hit);
    }

    private static boolean nearbyModBlock(Level level, BlockPos center, String namespace) {
        for (BlockPos p : BlockPos.betweenClosed(center.offset(-2, -1, -2), center.offset(2, 1, 2))) {
            String id = level.getBlockState(p).getBlock().builtInRegistryHolder().key().location().getNamespace();
            if (namespace.equals(id)) return true;
        }
        return false;
    }
}
