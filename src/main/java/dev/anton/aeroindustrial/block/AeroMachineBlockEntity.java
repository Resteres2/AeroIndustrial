package dev.anton.aeroindustrial.block;

import dev.anton.aeroindustrial.compat.CreateKineticBridge;
import dev.anton.aeroindustrial.compat.IntegrationStatus;
import dev.anton.aeroindustrial.compat.GtceuEnergyBridge;
import dev.anton.aeroindustrial.compat.MovingStructureBridge;
import dev.anton.aeroindustrial.menu.AeroMachineMenu;
import dev.anton.aeroindustrial.registry.AeroBlockEntities;
import dev.anton.aeroindustrial.registry.AeroRecipes;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

public final class AeroMachineBlockEntity extends BaseContainerBlockEntity {
    public static final int INPUT_SLOT = 0;
    public static final int OUTPUT_SLOT = 1;

    private NonNullList<ItemStack> items = NonNullList.withSize(2, ItemStack.EMPTY);

    private int energy;
    private int progress;
    private int overload;
    private int tickCounter;
    private int lastKineticInput;
    private int lastGtInput;
    private Boolean processingMachineCache;

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

    public int getOverload() {
        return overload;
    }

    public int getMaxEnergy() {
        return 10000 + getTier() * 5000 + (getTier() == 11 ? 30000 : 0);
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
        return findRecipe(stack) != null;
    }

    public boolean isProcessingMachine() {
        if (level == null) return false;
        if (processingMachineCache == null || (!level.isClientSide && tickCounter % 200 == 0)) {
            processingMachineCache = level.getRecipeManager().getAllRecipesFor(AeroRecipes.AERO_MACHINE_TYPE.get())
                    .stream()
                    .anyMatch(holder -> holder.value().machineTier() == getTier());
        }
        return processingMachineCache;
    }

    public int getProcessTime() {
        var recipe = findRecipe(getItem(INPUT_SLOT));
        return recipe == null ? 0 : recipe.value().processingTime();
    }

    public int getProcessEnergy() {
        var recipe = findRecipe(getItem(INPUT_SLOT));
        return recipe == null ? 0 : recipe.value().energyCost();
    }

    private net.minecraft.world.item.crafting.RecipeHolder<dev.anton.aeroindustrial.recipe.AeroMachineRecipe> findRecipe(
            ItemStack stack) {
        if (level == null || stack.isEmpty()) return null;
        return level.getRecipeManager()
                .getRecipeFor(AeroRecipes.AERO_MACHINE_TYPE.get(),
                        new net.minecraft.world.item.crafting.SingleRecipeInput(stack),
                        level)
                .filter(holder -> holder.value().machineTier() == getTier())
                .orElse(null);
    }

    public void serverTick() {
        if (!(level instanceof ServerLevel serverLevel)) return;

        tickCounter++;

        // Only the Torque Resonator is the Create -> AeroFlux interface.
        if (getTier() == 2 && tickCounter % 10 == 0) {
            lastKineticInput = 0;
            lastGtInput = 0;
            if (IntegrationStatus.createLoaded()) {
                int harvested = CreateKineticBridge.harvest(serverLevel, worldPosition, 2);
                lastKineticInput = Math.min(harvested, 120);
                if (lastKineticInput > 0) {
                    receiveEnergy(lastKineticInput, false);
                }
            }
            if (IntegrationStatus.gtceuLoaded()) {
                lastGtInput = Math.min(GtceuEnergyBridge.harvest(serverLevel, worldPosition, 1), 120);
                if (lastGtInput > 0) {
                    receiveEnergy(lastGtInput, false);
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
                    0.03,
                    getBlockState().getValue(AeroMachineBlock.FACING)
            );
            if (affected > 0) {
                energy -= 25;
                setChanged();
            }
        }

        if (isProcessingMachine()) {
            process(serverLevel);
        }

        runSpecialRole(serverLevel);

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

    private void runSpecialRole(ServerLevel serverLevel) {
        if (tickCounter % 20 != 0) return;

        switch (getTier()) {
            case 1 -> {
                // Flux Lattice is a stabilizer: it slowly dissipates overload while charged.
                if (energy > 0) {
                    energy = Math.max(0, energy - 1);
                    overload = Math.max(0, overload - 3);
                    setChanged();
                }
            }
            case 6 -> {
                // Inertial Accumulator trades stored AeroFlux for controlled overload damping.
                if (energy >= 50 && overload >= 30) {
                    energy -= 50;
                    overload = Math.max(0, overload - 5);
                    setChanged();
                }
            }
            case 5 -> {
                // Sky Relay coordinates nearby AeroFlux machines.
                for (BlockPos pos : BlockPos.betweenClosed(
                        worldPosition.offset(-4, -2, -4),
                        worldPosition.offset(4, 2, 4))) {
                    if (pos.equals(worldPosition)) continue;
                    if (serverLevel.getBlockEntity(pos) instanceof AeroMachineBlockEntity other
                            && other.getTier() != 5) {
                        other.overload = Math.max(0, other.overload - 1);
                        other.setChanged();
                    }
                }
            }
            case 12 -> {
                // Mobile Dock is a local buffer: it pulls a small amount from an adjacent charged machine.
                for (Direction direction : Direction.values()) {
                    BlockPos pos = worldPosition.relative(direction);
                    if (serverLevel.getBlockEntity(pos) instanceof AeroMachineBlockEntity other
                            && other.energy > 50 && energy < getMaxEnergy() - 50) {
                        int moved = Math.min(50, Math.min(other.energy, getMaxEnergy() - energy));
                        other.energy -= moved;
                        energy += moved;
                        other.setChanged();
                        setChanged();
                        break;
                    }
                }
            }
            case 13 -> {
                // Pressure Lattice converts stored AeroFlux into local overload damping.
                if (energy >= 20) {
                    energy -= 20;
                    overload = Math.max(0, overload - 8);
                    setChanged();
                }
            }
            case 14 -> {
                // Aeronautical Conduit transfers a controlled packet to one adjacent machine.
                if (energy >= 100) {
                    for (Direction direction : Direction.values()) {
                        BlockPos pos = worldPosition.relative(direction);
                        if (serverLevel.getBlockEntity(pos) instanceof AeroMachineBlockEntity other
                                && other.energy < other.getMaxEnergy()) {
                            int moved = Math.min(100, other.getMaxEnergy() - other.energy);
                            energy -= moved;
                            other.energy += moved;
                            other.setChanged();
                            setChanged();
                            break;
                        }
                    }
                }
            }
            case 15 -> {
                // Vector Frame is a structural stabilizer rather than a processor.
                if (energy > 0) {
                    overload = Math.max(0, overload - 2);
                    setChanged();
                }
            }
            default -> {
            }
        }
    }

    private void process(ServerLevel serverLevel) {
        ItemStack input = getItem(INPUT_SLOT);
        ItemStack output = getItem(OUTPUT_SLOT);
        var holder = findRecipe(input);

        if (holder == null) {
            progress = 0;
            return;
        }

        var recipe = holder.value();
        ItemStack recipeResult = recipe.getResultItem(serverLevel.registryAccess());
        if (!output.isEmpty() && (!ItemStack.isSameItemSameComponents(output, recipeResult)
                || output.getCount() + recipeResult.getCount() > output.getMaxStackSize())) {
            return;
        }

        if (energy < recipe.energyCost()) {
            return;
        }

        progress++;
        if (progress < recipe.processingTime()) {
            return;
        }

        energy -= recipe.energyCost();
        input.shrink(1);

        if (output.isEmpty()) {
            setItem(OUTPUT_SLOT, recipe.assemble(
                    new net.minecraft.world.item.crafting.SingleRecipeInput(input),
                    serverLevel.registryAccess()));
        } else {
            output.grow(recipeResult.getCount());
            setChanged();
        }

        progress = 0;
        level.playSound(null, worldPosition, SoundEvents.BEACON_POWER_SELECT, SoundSource.BLOCKS, 0.45f, 1.5f);
        setChanged();
    }

    public Component statusLine() {
        Component status = Component.translatable(
                "status.aeroindustrial.machine",
                energy,
                getMaxEnergy(),
                overload
        );
        if (getTier() == 2) {
            status = status.copy().append(Component.translatable("status.aeroindustrial.create", lastKineticInput));
            status = status.copy().append(Component.translatable("status.aeroindustrial.gtceu", lastGtInput));
        }
        if (getTier() == 10) {
            status = status.copy().append(Component.translatable(
                    (IntegrationStatus.sableLoaded() || IntegrationStatus.aeronauticsLoaded())
                            ? "status.aeroindustrial.thruster.ready"
                            : "status.aeroindustrial.thruster.offline"
            ));
        }
        if (isProcessingMachine()) {
            status = status.copy().append(Component.translatable(
                    "status.aeroindustrial.processing",
                    progress,
                    getProcessTime()
            ));
        } else {
            status = status.copy().append(Component.translatable("status.aeroindustrial.idle"));
        }
        return status;
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
        lastGtInput = 0;
    }
}
