package dev.anton.aeroindustrial.compat;

import dev.anton.aeroindustrial.AeroIndustrial;
import dev.anton.aeroindustrial.api.IAeroFluxStorage;
import dev.anton.aeroindustrial.block.AeroMachineBlockEntity;
import dev.anton.aeroindustrial.registry.AeroBlockEntities;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

public final class AeroCapabilities {
    public static final BlockCapability<IAeroFluxStorage, @Nullable Direction> AEROFLUX =
            BlockCapability.createSided(
                    ResourceLocation.fromNamespaceAndPath(AeroIndustrial.MOD_ID, "aeroflux"),
                    IAeroFluxStorage.class
            );

    private AeroCapabilities() {}

    public static void register(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                AEROFLUX,
                AeroBlockEntities.AERO_MACHINE.get(),
                (be, side) -> be
        );
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

        @Override public int receiveEnergy(int maxReceive, boolean simulate) {
            return be.receiveEnergy(maxReceive, simulate);
        }

        @Override public int extractEnergy(int maxExtract, boolean simulate) {
            return be.extractEnergy(maxExtract, simulate);
        }

        @Override public int getEnergyStored() {
            return be.getEnergy();
        }

        @Override public int getMaxEnergyStored() {
            return be.getMaxEnergy();
        }

        @Override public boolean canExtract() {
            return true;
        }

        @Override public boolean canReceive() {
            return true;
        }
    }

    private static final class InventoryHandler implements IItemHandler {
        private final AeroMachineBlockEntity be;
        private final Direction side;

        private InventoryHandler(AeroMachineBlockEntity be, Direction side) {
            this.be = be;
            this.side = side;
        }

        @Override
        public int getSlots() {
            return 2;
        }

        @Override
        public ItemStack getStackInSlot(int slot) {
            return be.getItem(slot);
        }

        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            if (side == Direction.DOWN
                    || slot != AeroMachineBlockEntity.INPUT_SLOT
                    || !be.canAcceptInput(stack)
                    || stack.isEmpty()) {
                return stack;
            }

            ItemStack existing = be.getItem(slot);
            if (!existing.isEmpty() && !ItemStack.isSameItemSameComponents(existing, stack)) {
                return stack;
            }

            int limit = Math.min(stack.getMaxStackSize(), getSlotLimit(slot));
            int accepted = Math.min(stack.getCount(), limit - existing.getCount());
            if (accepted <= 0) return stack;

            if (!simulate) {
                ItemStack target = be.getItem(slot);
                if (target.isEmpty()) {
                    be.setItem(slot, stack.copyWithCount(accepted));
                } else {
                    target.grow(accepted);
                    be.setChanged();
                }
            }

            return stack.copyWithCount(stack.getCount() - accepted);
        }

        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            if (side != Direction.DOWN || amount <= 0 || slot != AeroMachineBlockEntity.OUTPUT_SLOT) {
                return ItemStack.EMPTY;
            }

            ItemStack existing = be.getItem(slot);
            if (existing.isEmpty()) return ItemStack.EMPTY;

            ItemStack extracted = existing.copyWithCount(Math.min(amount, existing.getCount()));
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
        public boolean isItemValid(int slot, ItemStack stack) {
            return slot == AeroMachineBlockEntity.INPUT_SLOT && be.canAcceptInput(stack);
        }
    }
}
