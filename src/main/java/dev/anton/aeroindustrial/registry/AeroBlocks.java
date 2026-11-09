package dev.anton.aeroindustrial.registry;

import dev.anton.aeroindustrial.AeroIndustrial;
import dev.anton.aeroindustrial.block.AeroMachineBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class AeroBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(AeroIndustrial.MOD_ID);

    public static final DeferredBlock<Block> FLUX_LATTICE = machine("flux_lattice", 1);
    public static final DeferredBlock<Block> TORQUE_RESONATOR = machine("torque_resonator", 2);
    public static final DeferredBlock<Block> PHASE_REFINER = machine("phase_refiner", 3);
    public static final DeferredBlock<Block> VECTOR_COMPRESSOR = machine("vector_compressor", 4);
    public static final DeferredBlock<Block> SKY_RELAY = machine("sky_relay", 5);
    public static final DeferredBlock<Block> INERTIAL_ACCUMULATOR = machine("inertial_accumulator", 6);
    public static final DeferredBlock<Block> AERO_FORGE = machine("aero_forge", 7);
    public static final DeferredBlock<Block> ATMOSPHERIC_CONDENSER = machine("atmospheric_condenser", 8);
    public static final DeferredBlock<Block> MOBILE_REFINERY = machine("mobile_refinery", 9);
    public static final DeferredBlock<Block> VECTOR_THRUSTER = machine("vector_thruster", 10);

    public static final DeferredBlock<Block> AEROFLUX_CAPACITOR = machine("aeroflux_capacitor", 11);
    public static final DeferredBlock<Block> MOBILE_DOCK = machine("mobile_dock", 12);
    public static final DeferredBlock<Block> PRESSURE_LATTICE = machine("pressure_lattice", 13);
    public static final DeferredBlock<Block> AERONAUTICAL_CONDUIT = machine("aeronautical_conduit", 14);
    public static final DeferredBlock<Block> VECTOR_FRAME = machine("vector_frame", 15);

    private static DeferredBlock<Block> machine(String id, int tier) {
        return BLOCKS.registerBlock(id,
                p -> new AeroMachineBlock(
                        p.strength(3.0f)
                                .requiresCorrectToolForDrops()
                                .lightLevel(state -> tier >= 11 ? 4 : tier >= 8 ? 2 : 0),
                        tier
                ),
                BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK));
    }

    private AeroBlocks() {}
}
