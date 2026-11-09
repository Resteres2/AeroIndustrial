package dev.anton.aeroindustrial.client.screen;

import dev.anton.aeroindustrial.block.AeroMachineBlockEntity;
import dev.anton.aeroindustrial.menu.AeroMachineMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

public final class AeroMachineScreen extends AbstractContainerScreen<AeroMachineMenu> {
    public AeroMachineScreen(AeroMachineMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
        this.inventoryLabelX = 8;
        this.inventoryLabelY = 72;
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        int left = this.leftPos;
        int top = this.topPos;

        graphics.fill(left, top, left + imageWidth, top + imageHeight, 0xFF171B20);
        graphics.fill(left + 4, top + 4, left + imageWidth - 4, top + 68, 0xFF242A31);
        graphics.fill(left + 7, top + 81, left + imageWidth - 7, top + imageHeight - 4, 0xFF20252B);

        drawSlotFrame(graphics, left + 43, top + 34);
        drawSlotFrame(graphics, left + 115, top + 34);

        graphics.drawString(this.font, Component.translatable("gui.aeroindustrial.input"), left + 32, top + 19, 0xFFD6DCE2, false);
        graphics.drawString(this.font, Component.translatable("gui.aeroindustrial.output"), left + 104, top + 19, 0xFFD6DCE2, false);

        if (this.menu.getMachineContainer() instanceof AeroMachineBlockEntity machine) {
            int tier = machine.getTier();
            graphics.drawString(this.font,
                    Component.translatable("gui.aeroindustrial.tier", tier),
                    left + 8, top + 58, 0xFF8FC7FF, false);
        }
    }

    private void drawSlotFrame(GuiGraphics graphics, int x, int y) {
        graphics.fill(x, y, x + 18, y + 18, 0xFF0B0E11);
        graphics.fill(x + 1, y + 1, x + 17, y + 17, 0xFF3A424B);
        graphics.fill(x + 2, y + 2, x + 16, y + 16, 0xFF171B20);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 0xFFE7ECF0, false);
        graphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 0xFFB9C0C8, false);
    }
}
