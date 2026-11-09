package dev.anton.aeroindustrial.compat;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.joml.Vector3d;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Locale;

public final class MovingStructureBridge {
    private static volatile SableAccess sableAccess;
    private static volatile boolean sableLookupComplete;

    private MovingStructureBridge() {}

    public static int applyThruster(
            Level level,
            double x,
            double y,
            double z,
            double radius,
            double strength,
            Direction direction
    ) {
        if ((!IntegrationStatus.sableLoaded() && !IntegrationStatus.aeronauticsLoaded())
                || radius <= 0.0D || strength <= 0.0D) return 0;

        if (level instanceof ServerLevel serverLevel
                && (IntegrationStatus.sableLoaded() || IntegrationStatus.aeronauticsLoaded())) {
            if (applySableImpulse(serverLevel, BlockPos.containing(x, y, z), direction, strength)) {
                return 1;
            }
        }

        int affected = 0;
        AABB bounds = new AABB(x - radius, y - radius, z - radius,
                x + radius, y + radius, z + radius);

        for (Entity entity : level.getEntities((Entity) null, bounds, e -> {
            String name = e.getClass().getName().toLowerCase(Locale.ROOT);
            return name.contains("contraption")
                    || name.contains("airship")
                    || name.contains("aeronaut");
        })) {
            double dx = entity.getX() - x;
            double dy = entity.getY() - y;
            double dz = entity.getZ() - z;
            double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
            double scale = strength * Math.max(0.15, 1.0 - distance / radius);

            entity.setDeltaMovement(entity.getDeltaMovement().add(
                    direction.getStepX() * scale,
                    direction.getStepY() * scale,
                    direction.getStepZ() * scale
            ));
            entity.hurtMarked = true;
            affected++;
        }
        return affected;
    }

    private static boolean applySableImpulse(
            ServerLevel level,
            BlockPos position,
            Direction direction,
            double strength
    ) {
        try {
            Class<?> sableClass = Class.forName("dev.ryanhcode.sable.Sable");
            Field helperField = sableClass.getField("HELPER");
            Object helper = helperField.get(null);

            Method getContaining = findMethod(helper.getClass(), "getContaining", 2);
            if (getContaining == null) return false;

            Object subLevel = getContaining.invoke(helper, level, position);
            if (subLevel == null) return false;

            Class<?> containerClass = Class.forName("dev.ryanhcode.sable.api.sublevel.SubLevelContainer");
            Method getContainer = findMethod(containerClass, "getContainer", 1);
            if (getContainer == null) return false;

            Object container = getContainer.invoke(null, level);
            if (container == null) return false;

            Method physicsSystemMethod = findMethod(container.getClass(), "physicsSystem", 0);
            if (physicsSystemMethod == null) return false;
            Object physicsSystem = physicsSystemMethod.invoke(container);

            Method getHandle = findMethod(physicsSystem.getClass(), "getPhysicsHandle", 1);
            if (getHandle == null) return false;
            Object body = getHandle.invoke(physicsSystem, subLevel);
            if (body == null) return false;

            Method getPipeline = findMethod(physicsSystem.getClass(), "getPipeline", 0);
            if (getPipeline == null) return false;
            Object pipeline = getPipeline.invoke(physicsSystem);

            Vector3d worldPosition = new Vector3d(
                    position.getX() + 0.5,
                    position.getY() + 0.5,
                    position.getZ() + 0.5
            );
            Vector3d force = new Vector3d(
                    direction.getStepX() * strength,
                    direction.getStepY() * strength,
                    direction.getStepZ() * strength
            );

            for (Method method : pipeline.getClass().getMethods()) {
                if (!method.getName().equals("applyImpulse") || method.getParameterCount() != 3) continue;
                if (!method.getParameterTypes()[0].isAssignableFrom(body.getClass())) continue;
                if (!method.getParameterTypes()[1].isAssignableFrom(Vector3d.class)) continue;
                if (!method.getParameterTypes()[2].isAssignableFrom(Vector3d.class)) continue;
                method.invoke(pipeline, body, worldPosition, force);
                return true;
            }
        } catch (ReflectiveOperationException | RuntimeException ignored) {
            // Compatibility layer: fall back to the conservative entity impulse.
        }
        return false;
    }

    private static Method findMethod(Class<?> type, String name, int parameterCount) {
        for (Method method : type.getMethods()) {
            if (method.getName().equals(name) && method.getParameterCount() == parameterCount) {
                return method;
            }
        }
        return null;
    }
}
