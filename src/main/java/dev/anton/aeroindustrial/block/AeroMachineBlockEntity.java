package dev.anton.aeroindustrial.block;

import dev.anton.aeroindustrial.compat.CreateKineticBridge;
import dev.anton.aeroindustrial.compat.IntegrationStatus;
import dev.anton.aeroindustrial.compat.MovingStructureBridge;
import dev.anton.aeroindustrial.registry.AeroBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public final class AeroMachineBlockEntity extends BlockEntity {
    private int energy;
    private int progress;
    private int overload;
    private int tickCounter;

    public AeroMachineBlockEntity(BlockPos pos, BlockState state) {
        super(AeroBlockEntities.AERO_MACHINE.get(), pos, state);
    }

    public int getEnergy() {
        return energy;
    }

    public int getMaxEnergy() {
        return 10000 + ((AeroMachineBlock) getBlockState().getBlock()).tier() * 5000;
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

    public void serverTick() {
        if (!(level instanceof ServerLevel serverLevel)) return;

        AeroMachineBlock machine = (AeroMachineBlock) getBlockState().getBlock();
        int tier = machine.tier();
        tickCounter++;

        // Create is an optional integration. If present, nearby kinetic speed
        // can be converted into a small, intentionally lossy AeroFlux input.
        if (tickCounter % 10 == 0 && IntegrationStatus.createLoaded()) {
            int radius = tier >= 5 ? 3 : 2;
            int harvested = CreateKineticBridge.harvest(serverLevel, worldPosition, radius);
            if (harvested > 0) {
                receiveEnergy(Math.min(harvested, 120), false);
            }
        }

        // The Vector Thruster actively couples AeroFlux to moving structures.
        if (tier == 10 && tickCounter % 10 == 0 && energy >= 25) {
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

        // AeroFlux is intentionally separate from FE/EU. Supplied machines
        // resonate over time, while high storage creates an overload state.
        if (energy >= tier * 20) {
            energy -= tier * 2;
            progress++;
            if (progress >= 20) {
                progress = 0;
                overload = Math.max(0, overload - 1);
            }
            setChanged();
        } else {
            overload = Math.max(0, overload - 1);
            progress = 0;
        }

        if (energy > getMaxEnergy() * 0.9) {
            overload = Math.min(100, overload + 1);
        }

        if (overload >= 100) {
            energy = Math.max(0, energy - Math.max(1, getMaxEnergy() / 20));
            overload = 40;
            setChanged();
        }
    }

    public String statusLine() {
        return "AeroFlux " + energy + "/" + getMaxEnergy()
                + " | resonance " + progress + "/20"
                + " | overload " + overload + "%"
                + " | " + IntegrationStatus.summary();
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
    }
}
