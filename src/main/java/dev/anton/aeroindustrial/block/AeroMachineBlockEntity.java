package dev.anton.aeroindustrial.block;

import dev.anton.aeroindustrial.api.IAeroFluxStorage;
import dev.anton.aeroindustrial.compat.AeroCapabilities;
import dev.anton.aeroindustrial.compat.CreateKineticBridge;
import dev.anton.aeroindustrial.compat.IntegrationStatus;
import dev.anton.aeroindustrial.compat.GtceuEnergyBridge;
import dev.anton.aeroindustrial.config.AeroConfig;
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

public final class AeroMachineBlockEntity extends BaseContainerBlockEntity implements IAeroFluxStorage {
    public static final int INPUT_SLOT = 0;
    public static final int OUTPUT_SLOT = 1;

    private NonNullList<ItemStack> items = NonNullList.withSize(2, ItemStack.EMPTY);

    private int energy;
    private int progress;
    private int overload;
    private int tickCounter;
    private int lastKineticInput;
    private int lastGtInput;
    private int lastBufferTransfer;
    private int lastDockTransfer;
    private int lastConduitTransfer;
    private Boolean processingMachineCache;
    private java.util.List<net.minecraft.world.item.crafting.RecipeHolder<dev.anton.aeroindustrial.recipe.AeroMachineRecipe>> tierRecipesCache;
    private int tierRecipesCacheTick = -1;

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

    @Override
    public int receiveAeroFlux(int amount, boolean simulate) {
        return receiveEnergy(amount, simulate);
    }

    @Override
    public int extractAeroFlux(int amount, boolean simulate) {
        return extractEnergy(amount, simulate);
    }

    @Override
    public int getAeroFluxStored() {
        return getEnergy();
    }

    @Override
    public int getMaxAeroFluxStored() {
        return getMaxEnergy();
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
        if (processingMachineCache == null || (!level.isClientSide && tickCounter % 20 == 0)) {
            processingMachineCache = !getTierRecipes().isEmpty();
        }
        return processingMachineCache;
    }

    private java.util.List<net.minecraft.world.item.crafting.RecipeHolder<dev.anton.aeroindustrial.recipe.AeroMachineRecipe>> getTierRecipes() {
        if (level == null) return java.util.List.of();
        if (tierRecipesCache == null || tierRecipesCacheTick < 0 || tickCounter - tierRecipesCacheTick >= 20) {
            tierRecipesCache = level.getRecipeManager()
                    .getAllRecipesFor(AeroRecipes.AERO_MACHINE_TYPE.get())
                    .stream()
                    .filter(holder -> holder.value().machineTier() == getTier())
                    .toList();
            tierRecipesCacheTick = tickCounter;
        }
        return tierRecipesCache;
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

        // RecipeManager lookups are comparatively expensive and this block entity
        // can perform them every server tick while processing. Keep a short-lived
        // per-tier cache so large machine arrays do not repeatedly scan every recipe.
        var input = new net.minecraft.world.item.crafting.SingleRecipeInput(stack);
        return getTierRecipes().stream()
                .filter(holder -> holder.value().matches(input, level))
                .findFirst()
                .orElse(null);
    }

    public void serverTick() {
        if (!(level instanceof ServerLevel serverLevel)) return;

        tickCounter++;
        lastKineticInput = 0;
        lastGtInput = 0;
        lastBufferTransfer = 0;
        lastDockTransfer = 0;
        lastConduitTransfer = 0;

        // A redstone signal is a universal maintenance/stop command.
        if (serverLevel.hasNeighborSignal(worldPosition)) {
            progress = 0;
            return;
        }

        // Only the Torque Resonator is the Create -> AeroFlux interface.
        if (getTier() == 2 && tickCounter % 10 == 0) {
            if (IntegrationStatus.createLoaded()) {
                int harvested = CreateKineticBridge.harvest(serverLevel, worldPosition, 2, AeroConfig.CONFIG.createInputPerPulse.get());
                lastKineticInput = harvested;
                if (lastKineticInput > 0) {
                    receiveEnergy(lastKineticInput, false);
                }
            }
            if (IntegrationStatus.gtceuLoaded()) {
                lastGtInput = GtceuEnergyBridge.harvest(serverLevel, worldPosition, 1, AeroConfig.CONFIG.gtceuInputPerPulse.get());
                if (lastGtInput > 0) {
                    receiveEnergy(lastGtInput, false);
                }
            }
        }

        // Vector Thruster is the AeroFlux -> moving-structure interface.
        if (getTier() == 10 && tickCounter % 10 == 0 && energy >= AeroConfig.CONFIG.thrusterEnergyCost.get()) {
            int affected = MovingStructureBridge.applyThruster(
                    serverLevel,
                    worldPosition.getX() + 0.5,
                    worldPosition.getY() + 0.5,
                    worldPosition.getZ() + 0.5,
                    8.0,
                    AeroConfig.CONFIG.thrusterStrength.get(),
                    getBlockState().getValue(AeroMachineBlock.FACING)
            );
            if (affected > 0) {
                energy -= AeroConfig.CONFIG.thrusterEnergyCost.get();
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
        } else if (tickCounter % 20 == 0) {
            // Persist long-running machines without forcing an NBT write every tick.
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
                // Mobile Dock acts as a bidirectional berth: draw when low, unload when full.
                if (energy < getMaxEnergy() / 3) {
                    for (Direction direction : Direction.values()) {
                        BlockPos pos = worldPosition.relative(direction);
                        IAeroFluxStorage storage = serverLevel.getCapability(
                                AeroCapabilities.AEROFLUX, pos, direction.getOpposite());
                        if (storage == null || storage.getAeroFluxStored() <= 50) continue;
                        int moved = Math.min(100, Math.min(storage.getAeroFluxStored(), getMaxEnergy() - energy));
                        int extracted = storage.extractAeroFlux(moved, false);
                        if (extracted > 0) { energy += extracted; lastDockTransfer = extracted; setChanged(); break; }
                    }
                } else if (energy > getMaxEnergy() * 0.75) {
                    for (Direction direction : Direction.values()) {
                        BlockPos pos = worldPosition.relative(direction);
                        IAeroFluxStorage storage = serverLevel.getCapability(
                                AeroCapabilities.AEROFLUX, pos, direction.getOpposite());
                        if (storage == null || storage.getAeroFluxStored() >= storage.getMaxAeroFluxStored() * 0.75) continue;
                        int accepted = storage.receiveAeroFlux(Math.min(100, energy), false);
                        if (accepted > 0) { energy -= accepted; lastDockTransfer = accepted; setChanged(); break; }
                    }
                }
            }
            case 11 -> {
                // AeroFlux Capacitor is an active buffer: it automatically feeds adjacent
                // machines that have fallen below half charge, but never drains itself
                // below a reserve. This turns capacitors into the backbone of local grids.
                int reserve = Math.max(250, getMaxEnergy() / 4);
                if (energy > reserve + 100) {
                    for (Direction direction : Direction.values()) {
                        BlockPos pos = worldPosition.relative(direction);
                        if (!(serverLevel.getBlockEntity(pos) instanceof AeroMachineBlockEntity target)
                                || target == this
                                || target.getEnergy() >= target.getMaxEnergy() / 2) {
                            continue;
                        }

                        int packet = Math.min(250, energy - reserve);
                        int accepted = target.receiveAeroFlux(packet, true);
                        if (accepted > 0) {
                            target.receiveAeroFlux(accepted, false);
                            energy -= accepted;
                            lastBufferTransfer = accepted;
                            setChanged();
                            break;
                        }
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
                // Aeronautical Conduit routes packets toward the least-charged adjacent buffer.
                if (energy >= 100) {
                    IAeroFluxStorage best = null;
                    int bestStored = Integer.MAX_VALUE;
                    int bestCapacity = 1;
                    for (Direction direction : Direction.values()) {
                        BlockPos pos = worldPosition.relative(direction);
                        IAeroFluxStorage storage = serverLevel.getCapability(
                                AeroCapabilities.AEROFLUX, pos, direction.getOpposite());
                        if (storage == null || storage.getAeroFluxStored() >= storage.getMaxAeroFluxStored()) continue;
                        if ((long) storage.getAeroFluxStored() * bestCapacity
                                < (long) bestStored * storage.getMaxAeroFluxStored()) {
                            best = storage; bestStored = storage.getAeroFluxStored();
                            bestCapacity = storage.getMaxAeroFluxStored();
                        }
                    }
                    if (best != null) {
                        int accepted = best.receiveAeroFlux(100, false);
                        if (accepted > 0) { energy -= accepted; lastConduitTransfer = accepted; setChanged(); }
                    }
                }
            }
            case 15 -> {
                // Vector Frame is a local resonance anchor. It protects itself and
                // directly connected machines, making compact aeronautical assemblies
                // more stable than isolated machines.
                if (energy >= 10) {
                    energy -= 10;
                    overload = Math.max(0, overload - 2);
                    for (Direction direction : Direction.values()) {
                        BlockPos pos = worldPosition.relative(direction);
                        if (serverLevel.getBlockEntity(pos) instanceof AeroMachineBlockEntity other
                                && other.getTier() != 15) {
                            other.overload = Math.max(0, other.overload - 1);
                            other.setChanged();
                        }
                    }
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
        if (recipeResult.isEmpty() || recipeResult.getCount() <= 0
                || recipeResult.getCount() > recipeResult.getMaxStackSize()) {
            // Invalid external data must never create an oversized or empty output stack.
            progress = 0;
            return;
        }
        if (!output.isEmpty() && (!ItemStack.isSameItemSameComponents(output, recipeResult)
                || output.getCount() + recipeResult.getCount() > output.getMaxStackSize())) {
            return;
        }

        if (energy < recipe.energyCost()) {
            return;
        }

        // AeroFlux has a resonance window: lightly loaded systems process faster,
        // while severe overload forces the machine to slow down and stabilize.
        int progressGain = overload <= 20 ? 2 : overload >= 80 ? 0 : 1;
        if (progressGain == 0) {
            if (tickCounter % 10 == 0) setChanged();
            return;
        }

        progress = Math.min(recipe.processingTime(), progress + progressGain);
        if (progress < recipe.processingTime()) {
            if (tickCounter % 10 == 0) {
                setChanged();
            }
            return;
        }

        // Assemble from the actual pre-consumption input. This keeps recipe semantics
        // correct if a future recipe implementation derives its result from the input.
        ItemStack recipeInput = input.copy();
        energy -= recipe.energyCost();
        input.shrink(1);

        if (output.isEmpty()) {
            setItem(OUTPUT_SLOT, recipe.assemble(
                    new net.minecraft.world.item.crafting.SingleRecipeInput(recipeInput),
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
        if (getTier() == 11 && lastBufferTransfer > 0) {
            status = status.copy().append(Component.translatable("status.aeroindustrial.capacitor", lastBufferTransfer));
        }
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
        if (getTier() == 14 && lastConduitTransfer > 0) {
            status = status.copy().append(Component.translatable("status.aeroindustrial.conduit", lastConduitTransfer));
        }
        if (getTier() == 12 && lastDockTransfer > 0) {
            status = status.copy().append(Component.translatable("status.aeroindustrial.dock", lastDockTransfer));
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
        lastBufferTransfer = 0;
        lastDockTransfer = 0;
        lastConduitTransfer = 0;
        processingMachineCache = null;
        tierRecipesCache = null;
        tierRecipesCacheTick = -1;
    }
}
