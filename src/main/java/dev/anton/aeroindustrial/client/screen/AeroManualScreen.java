package dev.anton.aeroindustrial.client.screen;

import dev.anton.aeroindustrial.menu.AeroManualMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;

import java.util.List;

public final class AeroManualScreen extends AbstractContainerScreen<AeroManualMenu> {
    private int page;

    public AeroManualScreen(AeroManualMenu menu, net.minecraft.world.entity.player.Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = 236;
        this.imageHeight = 172;
    }

    private List<Component> pageLines() {
        return switch (page) {
            case 1 -> List.of(
                    Component.translatable("manual.aeroindustrial.tiers.1"),
                    Component.translatable("manual.aeroindustrial.tiers.2"),
                    Component.translatable("manual.aeroindustrial.tiers.3"),
                    Component.translatable("manual.aeroindustrial.tiers.4"),
                    Component.translatable("manual.aeroindustrial.tiers.5")
            );
            case 2 -> List.of(
                    Component.translatable("manual.aeroindustrial.tiers.6"),
                    Component.translatable("manual.aeroindustrial.tiers.7"),
                    Component.translatable("manual.aeroindustrial.tiers.8"),
                    Component.translatable("manual.aeroindustrial.tiers.9"),
                    Component.translatable("manual.aeroindustrial.tiers.10")
            );
            case 3 -> List.of(
                    Component.translatable("manual.aeroindustrial.tiers.11"),
                    Component.translatable("manual.aeroindustrial.tiers.12"),
                    Component.translatable("manual.aeroindustrial.tiers.13"),
                    Component.translatable("manual.aeroindustrial.tiers.14"),
                    Component.translatable("manual.aeroindustrial.tiers.15")
            );
            default -> List.of(
                    Component.translatable("manual.aeroindustrial.core"),
                    Component.translatable("manual.aeroindustrial.energy"),
                    Component.translatable("manual.aeroindustrial.overload"),
                    Component.translatable("manual.aeroindustrial.machine"),
                    Component.translatable("manual.aeroindustrial.integration")
            );
        };
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        int width = 236;
        int height = 172;
        int left = (this.width - width) / 2;
        int top = (this.height - height) / 2;

        graphics.fill(0, 0, this.width, this.height, 0x99000000);
        graphics.fill(left, top, left + width, top + height, 0xFF11161C);
        graphics.fill(left + 2, top + 2, left + width - 2, top + 28, 0xFF222A33);
        graphics.fill(left + 8, top + 34, left + width - 8, top + height - 8, 0xFF191F26);

        graphics.drawCenteredString(this.font, this.title, this.width / 2, top + 10, 0xFFEAF7FF);

        int y = top + 40;
        for (Component line : pageLines()) {
            List<net.minecraft.util.FormattedCharSequence> wrapped = this.font.split(line, width - 28);
            for (var part : wrapped) {
                if (y > top + height - 28) break;
                graphics.drawString(this.font, part, left + 14, y, 0xFFBFD3E0, false);
                y += 13;
            }
            y += 4;
            if (y > top + height - 28) break;
        }

        graphics.drawCenteredString(
                this.font,
                Component.translatable("manual.aeroindustrial.page", page + 1, 4),
                this.width / 2,
                top + height - 28,
                0xFF8FB4C8
        );
        graphics.drawCenteredString(
                this.font,
                Component.translatable("manual.aeroindustrial.controls"),
                this.width / 2,
                top + height - 15,
                0xFF7EA7BF
        );
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double deltaX, double deltaY) {
        if (deltaY > 0) {
            page = Math.max(0, page - 1);
        } else if (deltaY < 0) {
            page = Math.min(3, page + 1);
        }
        return true;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics, mouseX, mouseY, partialTick);
        super.render(graphics, mouseX, mouseY, partialTick);
        this.renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
