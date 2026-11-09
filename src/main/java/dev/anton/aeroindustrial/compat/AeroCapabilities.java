package dev.anton.aeroindustrial.compat;

import dev.anton.aeroindustrial.block.AeroMachineBlockEntity;
import dev.anton.aeroindustrial.registry.AeroBlockEntities;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.items.IItemHandler;

public final class AeroCapabilities {
    private AeroCapabilities() {}

    public static void register(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.EnergyStorage.BLOCK,
                AeroBlockEntities.AERO_MACHINE.get(),
                (be, side) -> new Storage(be)
        );
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                AeroBlockEntities.AERO_MACHINE.get(),
                (be, side) -> new InventoryHandler(be, side)
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

    private static final class InventoryHandler implements IItemHandler {
        private final AeroMachineBlockEntity be;
        private final net.minecraft.core.Direction side;

        private InventoryHandler(AeroMachineBlockEntity be, net.minecraft.core.Direction side) {
            this.be = be;
            this.side = side;
        }

        @Override
        public int getSlots() {
            return 2;
        }

        @Override
        public net.minecraft.world.item.ItemStack getStackInSlot(int slot) {
            return be.getItem(slot);
        }

        @Override
        public net.minecraft.world.item.ItemStack insertItem(int slot, net.minecraft.world.item.ItemStack stack, boolean simulate) {
            if (slot != AeroMachineBlockEntity.INPUT_SLOT || !be.canAcceptInput(stack) || stack.isEmpty()) {
                return stack;
            }

            int limit = Math.min(stack.getMaxStackSize(), getSlotLimit(slot));
            int accepted = Math.min(stack.getCount(), limit - be.getItem(slot).getCount());
            if (accepted <= 0) return stack;

            if (!simulate) {
                net.minecraft.world.item.ItemStack remainder = stack.copy();
                net.minecraft.world.item.ItemStack target = be.getItem(slot);
                if (target.isEmpty()) {
                    be.setItem(slot, stack.copyWithCount(accepted));
                } else {
                    target.grow(accepted);
                    be.setChanged();
                }
                return remainder.copyWithCount(stack.getCount() - accepted);
            }
            return stack.copyWithCount(stack.getCount() - accepted);
        }

        @Override
        public net.minecraft.world.item.ItemStack extractItem(int slot, int amount, boolean simulate) {
            if (amount <= 0 || slot != AeroMachineBlockEntity.OUTPUT_SLOT) return net.minecraft.world.item.ItemStack.EMPTY;
            net.minecraft.world.item.ItemStack existing = be.getItem(slot);
            if (existing.isEmpty()) return net.minecraft.world.item.ItemStack.EMPTY;

            net.minecraft.world.item.ItemStack extracted = existing.copyWithCount(Math.min(amount, existing.getCount()));
            if (!simulate) {
                be.removeItem(slot, extracted.getCount());
                be.setChanged();
            }
            return extracted;
        }

        @Override
        public int getSlotLimit(int slot) {
            return 64;
        }

        @Override
        public boolean isItemValid(int slot, net.minecraft.world.item.ItemStack stack) {
            return slot == AeroMachineBlockEntity.INPUT_SLOT && be.canAcceptInput(stack);
        }
    }

}
