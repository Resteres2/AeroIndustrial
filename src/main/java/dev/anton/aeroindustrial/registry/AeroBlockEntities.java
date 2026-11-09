package dev.anton.aeroindustrial.registry;

import dev.anton.aeroindustrial.AeroIndustrial;
import dev.anton.aeroindustrial.block.AeroMachineBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

public final class AeroBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, AeroIndustrial.MOD_ID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<AeroMachineBlockEntity>> AERO_MACHINE =
            BLOCK_ENTITIES.register("aero_machine", () -> BlockEntityType.Builder
                    .of(AeroMachineBlockEntity::new,
                            AeroBlocks.FLUX_LATTICE.get(),
                            AeroBlocks.TORQUE_RESONATOR.get(),
                            AeroBlocks.PHASE_REFINER.get(),
                            AeroBlocks.VECTOR_COMPRESSOR.get(),
                            AeroBlocks.SKY_RELAY.get(),
                            AeroBlocks.INERTIAL_ACCUMULATOR.get(),
                            AeroBlocks.AERO_FORGE.get(),
                            AeroBlocks.ATMOSPHERIC_CONDENSER.get(),
                            AeroBlocks.MOBILE_REFINERY.get(),
                            AeroBlocks.VECTOR_THRUSTER.get(),
                            AeroBlocks.AEROFLUX_CAPACITOR.get(),
                            AeroBlocks.MOBILE_DOCK.get(),
                            AeroBlocks.PRESSURE_LATTICE.get(),
                            AeroBlocks.AERONAUTICAL_CONDUIT.get(),
                            AeroBlocks.VECTOR_FRAME.get())
                    .build(null));

    private AeroBlockEntities() {}
}
