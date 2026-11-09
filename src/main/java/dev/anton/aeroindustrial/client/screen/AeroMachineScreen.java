package dev.anton.aeroindustrial.client.screen;

import dev.anton.aeroindustrial.menu.AeroMachineMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public final class AeroMachineScreen extends AbstractContainerScreen<AeroMachineMenu> {
    public AeroMachineScreen(AeroMachineMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 176;
        this.inventoryLabelX = 8;
        this.inventoryLabelY = 86;
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
        int maxEnergy = Math.max(1, this.menu.getMaxEnergy());
        int energyWidth = Math.min(152, (int) (152.0 * this.menu.getEnergy() / maxEnergy));
        graphics.fill(left + 8, top + 48, left + 160, top + 54, 0xFF0B0E11);
        graphics.fill(left + 8, top + 48, left + 8 + energyWidth, top + 54, 0xFF4BC7FF);

        String energyText = this.menu.getEnergy() + " / " + maxEnergy + " AF";
        graphics.drawString(this.font, energyText, left + 8, top + 56, 0xFF8FC7FF, false);

        int overload = Math.max(0, Math.min(100, this.menu.getOverload()));
        int overloadWidth = (int) (152.0 * overload / 100.0);
        graphics.fill(left + 8, top + 65, left + 160, top + 70, 0xFF0B0E11);
        graphics.fill(left + 8, top + 65, left + 8 + overloadWidth, top + 70, 0xFFFF6B5C);
        graphics.drawString(this.font, Component.translatable("gui.aeroindustrial.overload", overload),
                left + 8, top + 72, 0xFFFFA49B, false);

        if (this.menu.getProcessTime() > 0) {
            int time = this.menu.getProcessTime();
            int progressWidth = Math.min(152, (int) (152.0 * this.menu.getProgress() / Math.max(1, time)));
            graphics.fill(left + 8, top + 79, left + 160, top + 84, 0xFF0B0E11);
            graphics.fill(left + 8, top + 79, left + 8 + progressWidth, top + 84, 0xFF7BFF8A);
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
