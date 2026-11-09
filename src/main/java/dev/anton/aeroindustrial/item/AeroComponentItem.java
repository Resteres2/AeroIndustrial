package dev.anton.aeroindustrial.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public final class AeroComponentItem extends Item {
    private final String id;

    public AeroComponentItem(Properties properties, String id) {
        super(properties);
        this.id = id;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        tooltip.add(Component.translatable("tooltip.aeroindustrial.item." + id)
                .withStyle(ChatFormatting.GRAY));
    }
}
