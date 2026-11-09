package dev.anton.aeroindustrial.block;

import dev.anton.aeroindustrial.compat.CreateKineticBridge;
import dev.anton.aeroindustrial.compat.IntegrationStatus;
import dev.anton.aeroindustrial.compat.MovingStructureBridge;
import dev.anton.aeroindustrial.menu.AeroMachineMenu;
import dev.anton.aeroindustrial.registry.AeroBlockEntities;
import dev.anton.aeroindustrial.registry.AeroItems;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

public final class AeroMachineBlockEntity extends BaseContainerBlockEntity {
    public static final int INPUT_SLOT = 0;
    public static final int OUTPUT_SLOT = 1;

    private NonNullList<ItemStack> items = NonNullList.withSize(2, ItemStack.EMPTY);

    private int energy;
    private int progress;
    private int overload;
    private int tickCounter;
    private int lastKineticInput;

    public AeroMachineBlockEntity(BlockPos pos, BlockState state) {
        super(AeroBlockEntities.AERO_MACHINE.get(), pos, state);
    }

    public int getTier() {
        return ((AeroMachineBlock) getBlockState().getBlock()).tier();
    }

    public int getEnergy() {
        return energy;
    }

    public int getProgress() {
        return progress;
    }

    public int getMaxEnergy() {
        return 10000 + getTier() * 5000;
    }

    public int receiveEnergy(int amount, boolean simulate) {
        if (amount <= 0) return 0;
        int accepted = Math.min(amount, getMaxEnergy() - energy);
        if (!simulate && accepted > 0) {
            energy += accepted;
            setChanged();
        }
        return accepted;
    }

    public int extractEnergy(int amount, boolean simulate) {
        if (amount <= 0) return 0;
        int extracted = Math.min(amount, energy);
        if (!simulate && extracted > 0) {
            energy -= extracted;
            setChanged();
        }
        return extracted;
    }

    public boolean canAcceptInput(ItemStack stack) {
        ProcessingSpec spec = processingSpec();
        return spec != null && stack.is(spec.input());
    }

    public boolean isProcessingMachine() {
        return processingSpec() != null;
    }

    public int getProcessTime() {
        ProcessingSpec spec = processingSpec();
        return spec == null ? 0 : spec.time();
    }

    public int getProcessEnergy() {
        ProcessingSpec spec = processingSpec();
        return spec == null ? 0 : spec.energy();
    }

    public void serverTick() {
        if (!(level instanceof ServerLevel serverLevel)) return;

        tickCounter++;

        // Only the Torque Resonator is the Create -> AeroFlux interface.
        if (getTier() == 2 && tickCounter % 10 == 0) {
            lastKineticInput = 0;
            if (IntegrationStatus.createLoaded()) {
                int harvested = CreateKineticBridge.harvest(serverLevel, worldPosition, 2);
                lastKineticInput = Math.min(harvested, 120);
                if (lastKineticInput > 0) {
                    receiveEnergy(lastKineticInput, false);
                }
            }
        }

        // Vector Thruster is the AeroFlux -> moving-structure interface.
        if (getTier() == 10 && tickCounter % 10 == 0 && energy >= 25) {
            int affected = MovingStructureBridge.applyThruster(
                    serverLevel,
                    worldPosition.getX() + 0.5,
                    worldPosition.getY() + 0.5,
                    worldPosition.getZ() + 0.5,
                    8.0,
                    0.03
            );
            if (affected > 0) {
                energy -= 25;
                setChanged();
            }
        }

        ProcessingSpec spec = processingSpec();
        if (spec != null) {
            process(spec);
        } else {
            runResonanceCore();
        }

        if (energy > getMaxEnergy() * 0.9) {
            overload = Math.min(100, overload + 1);
        } else {
            overload = Math.max(0, overload - 1);
        }

        if (overload >= 100) {
            energy = Math.max(0, energy - Math.max(1, getMaxEnergy() / 20));
            overload = 40;
            setChanged();
        }
    }

    private void runResonanceCore() {
        if (energy >= getTier() * 20) {
            energy -= getTier() * 2;
            progress++;
            if (progress >= 20) {
                progress = 0;
                overload = Math.max(0, overload - 1);
            }
            setChanged();
        } else {
            progress = 0;
        }
    }

    private void process(ProcessingSpec spec) {
        ItemStack input = getItem(INPUT_SLOT);
        ItemStack output = getItem(OUTPUT_SLOT);

        if (!canAcceptInput(input)) {
            progress = 0;
            return;
        }

        if (!output.isEmpty() && (!output.is(spec.output()) || output.getCount() >= output.getMaxStackSize())) {
            return;
        }

        if (energy < spec.energy()) {
            return;
        }

        progress++;
        if (progress < spec.time()) {
            return;
        }

        energy -= spec.energy();
        input.shrink(1);

        if (output.isEmpty()) {
            setItem(OUTPUT_SLOT, new ItemStack(spec.output()));
        } else {
            output.grow(1);
            setChanged();
        }

        progress = 0;
        setChanged();
    }

    private ProcessingSpec processingSpec() {
        return switch (getTier()) {
            case 3 -> new ProcessingSpec(AeroItems.PHASE_FOIL.get(), AeroItems.RESONANCE_CELL.get(), 80, 3000);
            case 4 -> new ProcessingSpec(Items.COPPER_INGOT, AeroItems.VECTOR_COIL.get(), 100, 4000);
            case 7 -> new ProcessingSpec(Items.REDSTONE, AeroItems.FLUX_CONDUCTOR.get(), 120, 5000);
            case 8 -> new ProcessingSpec(Items.SNOWBALL, AeroItems.PRESSURE_GEL.get(), 90, 4500);
            case 9 -> new ProcessingSpec(Items.GOLD_INGOT, AeroItems.PHASE_FOIL.get(), 140, 6000);
            default -> null;
        };
    }

    public String statusLine() {
        String createStatus = getTier() == 2
                ? " | Create input " + lastKineticInput + " AF/10t"
                : "";
        String motionStatus = getTier() == 10
                ? " | Thruster coupling " + (IntegrationStatus.sableLoaded() || IntegrationStatus.aeronauticsLoaded() ? "READY" : "OFF")
                : "";
        String processStatus = isProcessingMachine()
                ? " | processing " + progress + "/" + getProcessTime()
                : "";
        return "AeroFlux " + energy + "/" + getMaxEnergy()
                + (isProcessingMachine() ? "" : " | resonance " + progress + "/20")
                + " | overload " + overload + "%"
                + createStatus
                + motionStatus
                + processStatus;
    }

    @Override
    public int getContainerSize() {
        return items.size();
    }

    @Override
    protected NonNullList<ItemStack> getItems() {
        return items;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> items) {
        this.items = items;
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable(getBlockState().getBlock().getDescriptionId());
    }

    @Override
    protected AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
        return new AeroMachineMenu(containerId, inventory, this);
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        return slot == INPUT_SLOT && canAcceptInput(stack);
    }

    @Override
    public boolean canTakeItem(Container target, int slot, ItemStack stack) {
        return slot == INPUT_SLOT || slot == OUTPUT_SLOT;
    }

    @Override
    public boolean stillValid(Player player) {
        return Container.stillValidBlockEntity(this, player);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, net.minecraft.core.HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("AeroFlux", energy);
        tag.putInt("Progress", progress);
        tag.putInt("Overload", overload);
        tag.putInt("TickCounter", tickCounter);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, net.minecraft.core.HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        energy = tag.getInt("AeroFlux");
        progress = tag.getInt("Progress");
        overload = tag.getInt("Overload");
        tickCounter = tag.getInt("TickCounter");
        lastKineticInput = 0;
    }

    private record ProcessingSpec(Item input, Item output, int time, int energy) {}
}
