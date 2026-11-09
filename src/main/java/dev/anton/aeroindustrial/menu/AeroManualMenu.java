package dev.anton.aeroindustrial.menu;

import dev.anton.aeroindustrial.registry.AeroMenus;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

public final class AeroManualMenu extends AbstractContainerMenu {
    public AeroManualMenu(int containerId, Inventory inventory) {
        super(AeroMenus.AERO_MANUAL.get(), containerId);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }
}
