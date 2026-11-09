package dev.anton.aeroindustrial.compat;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;

public final class GtceuEnergyBridge {
    private GtceuEnergyBridge() {}

    public static int harvest(ServerLevel level, BlockPos center, int radius, int maxInput) {
        if (!IntegrationStatus.gtceuLoaded()) return 0;

        int total = 0;
        for (BlockPos pos : BlockPos.betweenClosed(
                center.offset(-radius, -radius, -radius),
                center.offset(radius, radius, radius))) {
            if (pos.equals(center)) continue;
            BlockEntity be = level.getBlockEntity(pos);
            if (be == null) continue;

            String name = be.getClass().getName().toLowerCase(java.util.Locale.ROOT);
            if (!name.contains("gtceu") && !name.contains("gregtech")) continue;

            for (Direction side : Direction.values()) {
                IEnergyStorage storage = level.getCapability(
                        Capabilities.EnergyStorage.BLOCK,
                        pos,
                        be.getBlockState(),
                        be,
                        side
                );
                if (storage == null || !storage.canExtract()) continue;

                int extracted = storage.extractEnergy(Math.min(40, maxInput - total), false);
                if (extracted > 0) {
                    total += extracted;
                    if (total >= maxInput) return total;
                }
            }
        }
        return total;
    }
}
