package dev.anton.aeroindustrial.compat;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

public final class MovingStructureBridge {
    private MovingStructureBridge() {}

    public static int applyThruster(Level level, double x, double y, double z, double radius, double strength) {
        if (!IntegrationStatus.sableLoaded() && !IntegrationStatus.aeronauticsLoaded()) return 0;

        int affected = 0;
        AABB bounds = new AABB(x - radius, y - radius, z - radius,
                x + radius, y + radius, z + radius);

        for (Entity entity : level.getEntities((Entity) null, bounds, e -> {
            String name = e.getClass().getName().toLowerCase();
            return name.contains("contraption")
                    || name.contains("airship")
                    || name.contains("aeronaut");
        })) {
            double dx = entity.getX() - x;
            double dy = entity.getY() - y;
            double dz = entity.getZ() - z;
            double distance = Math.max(0.001, Math.sqrt(dx * dx + dy * dy + dz * dz));
            double scale = strength / distance;
            entity.setDeltaMovement(entity.getDeltaMovement().add(dx * scale, dy * scale, dz * scale));
            entity.hurtMarked = true;
            affected++;
        }
        return affected;
    }
}
