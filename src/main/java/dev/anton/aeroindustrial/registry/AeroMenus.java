package dev.anton.aeroindustrial.registry;

import dev.anton.aeroindustrial.AeroIndustrial;
import dev.anton.aeroindustrial.menu.AeroMachineMenu;
import dev.anton.aeroindustrial.menu.AeroManualMenu;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class AeroMenus {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(Registries.MENU, AeroIndustrial.MOD_ID);

    public static final DeferredHolder<MenuType<?>, MenuType<AeroMachineMenu>> AERO_MACHINE =
            MENUS.register("aero_machine", () -> IMenuTypeExtension.<AeroMachineMenu>create(AeroMachineMenu::new));

    public static final DeferredHolder<MenuType<?>, MenuType<AeroManualMenu>> AERO_MANUAL =
            MENUS.register("aero_manual", () -> new MenuType<>(AeroManualMenu::new, FeatureFlags.DEFAULT_FLAGS));

    private AeroMenus() {}
}
