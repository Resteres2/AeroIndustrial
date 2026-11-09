package dev.anton.aeroindustrial.registry;

import dev.anton.aeroindustrial.AeroIndustrial;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

public final class AeroCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(net.minecraft.core.registries.Registries.CREATIVE_MODE_TAB, AeroIndustrial.MOD_ID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> AEROFLUX =
            TABS.register("aeroflux", () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.aeroindustrial.aeroflux"))
                    .icon(() -> new ItemStack(AeroItems.AEROFLUX_CRYSTAL.get()))
                    .displayItems((params, output) -> {
                        output.accept(AeroItems.AEROFLUX_CRYSTAL.get());
                        output.accept(AeroItems.PHASE_FOIL.get());
                        output.accept(AeroItems.VECTOR_COIL.get());
                        output.accept(AeroItems.INERTIAL_CORE.get());
                        output.accept(AeroItems.SKY_LATTICE.get());
                        output.accept(AeroItems.RESONANCE_CELL.get());
                        output.accept(AeroItems.PRESSURE_GEL.get());
                        output.accept(AeroItems.FLUX_CONDUCTOR.get());

                        output.accept(AeroBlocks.FLUX_LATTICE.get());
                        output.accept(AeroBlocks.TORQUE_RESONATOR.get());
                        output.accept(AeroBlocks.PHASE_REFINER.get());
                        output.accept(AeroBlocks.VECTOR_COMPRESSOR.get());
                        output.accept(AeroBlocks.SKY_RELAY.get());
                        output.accept(AeroBlocks.INERTIAL_ACCUMULATOR.get());
                        output.accept(AeroBlocks.AERO_FORGE.get());
                        output.accept(AeroBlocks.ATMOSPHERIC_CONDENSER.get());
                        output.accept(AeroBlocks.MOBILE_REFINERY.get());
                        output.accept(AeroBlocks.VECTOR_THRUSTER.get());
                        output.accept(AeroBlocks.AEROFLUX_CAPACITOR.get());
                        output.accept(AeroBlocks.MOBILE_DOCK.get());
                        output.accept(AeroBlocks.PRESSURE_LATTICE.get());
                        output.accept(AeroBlocks.AERONAUTICAL_CONDUIT.get());
                        output.accept(AeroBlocks.VECTOR_FRAME.get());
                    })
                    .build());

    private AeroCreativeTabs() {}
}
