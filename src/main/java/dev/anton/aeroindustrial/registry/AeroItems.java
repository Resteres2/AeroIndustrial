package dev.anton.aeroindustrial.registry;

import dev.anton.aeroindustrial.AeroIndustrial;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredItem;

public final class AeroItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(AeroIndustrial.MOD_ID);

    public static final DeferredItem<Item> AEROFLUX_CRYSTAL = ITEMS.registerSimpleItem("aeroflux_crystal");
    public static final DeferredItem<Item> PHASE_FOIL = ITEMS.registerSimpleItem("phase_foil");
    public static final DeferredItem<Item> VECTOR_COIL = ITEMS.registerSimpleItem("vector_coil");
    public static final DeferredItem<Item> INERTIAL_CORE = ITEMS.registerSimpleItem("inertial_core");
    public static final DeferredItem<Item> SKY_LATTICE = ITEMS.registerSimpleItem("sky_lattice");
    public static final DeferredItem<Item> RESONANCE_CELL = ITEMS.registerSimpleItem("resonance_cell");
    public static final DeferredItem<Item> PRESSURE_GEL = ITEMS.registerSimpleItem("pressure_gel");
    public static final DeferredItem<Item> FLUX_CONDUCTOR = ITEMS.registerSimpleItem("flux_conductor");

    private AeroItems() {}
}
