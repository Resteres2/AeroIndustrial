package dev.anton.aeroindustrial.compat;

import dev.anton.aeroindustrial.block.AeroMachineBlockEntity;
import dev.anton.aeroindustrial.registry.AeroBlockEntities;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.energy.IEnergyStorage;

public final class AeroCapabilities {
    private AeroCapabilities() {}

    public static void register(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.EnergyStorage.BLOCK,
                AeroBlockEntities.AERO_MACHINE.get(),
                (be, side) -> new Storage(be)
        );
    }

    private static final class Storage implements IEnergyStorage {
        private final AeroMachineBlockEntity be;

        private Storage(AeroMachineBlockEntity be) {
            this.be = be;
        }

        @Override
        public int receiveEnergy(int maxReceive, boolean simulate) {
            return be.receiveEnergy(maxReceive, simulate);
        }

        @Override
        public int extractEnergy(int maxExtract, boolean simulate) {
            return be.extractEnergy(maxExtract, simulate);
        }

        @Override
        public int getEnergyStored() {
            return be.getEnergy();
        }

        @Override
        public int getMaxEnergyStored() {
            return be.getMaxEnergy();
        }

        @Override
        public boolean canExtract() {
            return true;
        }

        @Override
        public boolean canReceive() {
            return true;
        }
    }
}
