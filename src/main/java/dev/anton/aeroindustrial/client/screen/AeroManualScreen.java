package dev.anton.aeroindustrial.client.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.List;

public final class AeroManualScreen extends Screen {
    private final List<Component> lines = List.of(
            Component.translatable("manual.aeroindustrial.core"),
            Component.translatable("manual.aeroindustrial.energy"),
            Component.translatable("manual.aeroindustrial.overload"),
            Component.translatable("manual.aeroindustrial.machine"),
            Component.translatable("manual.aeroindustrial.integration"),
            Component.translatable("manual.aeroindustrial.controls")
    );

    public AeroManualScreen() {
        super(Component.translatable("screen.aeroindustrial.manual"));
    }

    @Override
    protected void init() {
        super.init();
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
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
        for (Component line : lines) {
            List<net.minecraft.util.FormattedCharSequence> wrapped =
                    this.font.split(line, width - 28);
            for (var part : wrapped) {
                graphics.drawString(this.font, part, left + 14, y, 0xFFBFD3E0, false);
                y += 13;
            }
            y += 6;
            if (y > top + height - 22) break;
        }

        graphics.drawCenteredString(
                this.font,
                Component.translatable("manual.aeroindustrial.close"),
                this.width / 2,
                top + height - 15,
                0xFF7EA7BF
        );

        super.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
