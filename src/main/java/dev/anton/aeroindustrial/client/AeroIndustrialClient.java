package dev.anton.aeroindustrial.client;

import dev.anton.aeroindustrial.AeroIndustrial;
import dev.anton.aeroindustrial.client.screen.AeroMachineScreen;
import dev.anton.aeroindustrial.client.screen.AeroManualScreen;
import net.minecraft.client.Minecraft;
import dev.anton.aeroindustrial.registry.AeroMenus;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@EventBusSubscriber(modid = AeroIndustrial.MOD_ID, value = Dist.CLIENT)
public final class AeroIndustrialClient {
    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(AeroMenus.AERO_MACHINE.get(), AeroMachineScreen::new);
    }

    public static void openManual() {
        Minecraft.getInstance().setScreen(new AeroManualScreen());
    }

    private AeroIndustrialClient() {}
}
