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

    public static final DeferredItem<Item> FLUX_LATTICE = ITEMS.registerSimpleBlockItem("flux_lattice", AeroBlocks.FLUX_LATTICE);
    public static final DeferredItem<Item> TORQUE_RESONATOR = ITEMS.registerSimpleBlockItem("torque_resonator", AeroBlocks.TORQUE_RESONATOR);
    public static final DeferredItem<Item> PHASE_REFINER = ITEMS.registerSimpleBlockItem("phase_refiner", AeroBlocks.PHASE_REFINER);
    public static final DeferredItem<Item> VECTOR_COMPRESSOR = ITEMS.registerSimpleBlockItem("vector_compressor", AeroBlocks.VECTOR_COMPRESSOR);
    public static final DeferredItem<Item> SKY_RELAY = ITEMS.registerSimpleBlockItem("sky_relay", AeroBlocks.SKY_RELAY);
    public static final DeferredItem<Item> INERTIAL_ACCUMULATOR = ITEMS.registerSimpleBlockItem("inertial_accumulator", AeroBlocks.INERTIAL_ACCUMULATOR);
    public static final DeferredItem<Item> AERO_FORGE = ITEMS.registerSimpleBlockItem("aero_forge", AeroBlocks.AERO_FORGE);
    public static final DeferredItem<Item> ATMOSPHERIC_CONDENSER = ITEMS.registerSimpleBlockItem("atmospheric_condenser", AeroBlocks.ATMOSPHERIC_CONDENSER);
    public static final DeferredItem<Item> MOBILE_REFINERY = ITEMS.registerSimpleBlockItem("mobile_refinery", AeroBlocks.MOBILE_REFINERY);
    public static final DeferredItem<Item> VECTOR_THRUSTER = ITEMS.registerSimpleBlockItem("vector_thruster", AeroBlocks.VECTOR_THRUSTER);
    public static final DeferredItem<Item> AEROFLUX_CAPACITOR = ITEMS.registerSimpleBlockItem("aeroflux_capacitor", AeroBlocks.AEROFLUX_CAPACITOR);
    public static final DeferredItem<Item> MOBILE_DOCK = ITEMS.registerSimpleBlockItem("mobile_dock", AeroBlocks.MOBILE_DOCK);
    public static final DeferredItem<Item> PRESSURE_LATTICE = ITEMS.registerSimpleBlockItem("pressure_lattice", AeroBlocks.PRESSURE_LATTICE);
    public static final DeferredItem<Item> AERONAUTICAL_CONDUIT = ITEMS.registerSimpleBlockItem("aeronautical_conduit", AeroBlocks.AERONAUTICAL_CONDUIT);
    public static final DeferredItem<Item> VECTOR_FRAME = ITEMS.registerSimpleBlockItem("vector_frame", AeroBlocks.VECTOR_FRAME);

    private AeroItems() {}
}
