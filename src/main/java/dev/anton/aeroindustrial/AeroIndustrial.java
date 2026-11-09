package dev.anton.aeroindustrial;

import dev.anton.aeroindustrial.compat.AeroCapabilities;
import dev.anton.aeroindustrial.registry.AeroBlockEntities;
import dev.anton.aeroindustrial.registry.AeroBlocks;
import dev.anton.aeroindustrial.registry.AeroCreativeTabs;
import dev.anton.aeroindustrial.registry.AeroItems;
import dev.anton.aeroindustrial.registry.AeroMenus;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(AeroIndustrial.MOD_ID)
public final class AeroIndustrial {
    public static final String MOD_ID = "aeroindustrial";

    public AeroIndustrial(IEventBus modBus) {
        AeroBlocks.BLOCKS.register(modBus);
        AeroItems.ITEMS.register(modBus);
        AeroCreativeTabs.TABS.register(modBus);
        AeroBlockEntities.BLOCK_ENTITIES.register(modBus);
        AeroMenus.MENUS.register(modBus);
        modBus.addListener(AeroCapabilities::register);
    }
}
