package dev.anton.aeroindustrial.item;

import dev.anton.aeroindustrial.client.AeroIndustrialClient;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.fml.Dist;
import net.neoforged.fml.DistExecutor;

public final class AeroManualItem extends Item {
    public AeroManualItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (level.isClientSide) {
            DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> AeroIndustrialClient::openManual);
        }
        return InteractionResultHolder.sidedSuccess(player.getItemInHand(hand), level.isClientSide);
    }
}
