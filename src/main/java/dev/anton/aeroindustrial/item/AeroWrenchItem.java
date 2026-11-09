package dev.anton.aeroindustrial.item;

import dev.anton.aeroindustrial.block.AeroMachineBlock;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import net.minecraft.world.level.block.Rotation;

public final class AeroWrenchItem extends Item {
    public AeroWrenchItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        BlockState state = context.getLevel().getBlockState(context.getClickedPos());
        if (!(state.getBlock() instanceof AeroMachineBlock)) {
            return InteractionResult.PASS;
        }

        if (!context.getLevel().isClientSide) {
            Rotation rotation = context.getPlayer() != null && context.getPlayer().isShiftKeyDown()
                    ? Rotation.COUNTERCLOCKWISE_90
                    : Rotation.CLOCKWISE_90;
            context.getLevel().setBlock(
                    context.getClickedPos(),
                    state.rotate(rotation),
                    net.minecraft.world.level.block.Block.UPDATE_ALL
            );
            context.getLevel().playSound(
                    null,
                    context.getClickedPos(),
                    SoundEvents.METAL_HIT,
                    SoundSource.BLOCKS,
                    0.7f,
                    1.2f
            );
        }

        return InteractionResult.sidedSuccess(context.getLevel().isClientSide);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.aeroindustrial.wrench").withStyle(ChatFormatting.GRAY));
    }

}
