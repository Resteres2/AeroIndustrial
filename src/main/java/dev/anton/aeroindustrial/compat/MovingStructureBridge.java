package dev.anton.aeroindustrial.compat;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.joml.Vector3d;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Locale;

public final class MovingStructureBridge {
    private static final Object SABLE_BINDINGS_LOCK = new Object();
    private static volatile boolean sableBindingsResolved;
    private static volatile SableBindings sableBindings;

    private MovingStructureBridge() {}

    public static int applyThruster(
            ServerLevel level,
            double x,
            double y,
            double z,
            double radius,
            double strength,
            Direction direction
    ) {
        if ((!IntegrationStatus.sableLoaded() && !IntegrationStatus.aeronauticsLoaded())
                || radius <= 0.0D || strength <= 0.0D) {
            return 0;
        }

        BlockPos position = BlockPos.containing(x, y, z);
        if (IntegrationStatus.sableLoaded()
                && applySableImpulse(level, position, direction, strength)) {
            return 1;
        }

        // Outside a Sable sub-level, use the conservative compatibility fallback.
        // A single thruster pulse must not broadcast force to every nearby vehicle.
        return applyNearestEntityImpulse(level, x, y, z, radius, strength, direction);
    }

    private static int applyNearestEntityImpulse(
            ServerLevel level,
            double x,
            double y,
            double z,
            double radius,
            double strength,
            Direction direction
    ) {
        AABB bounds = new AABB(
                x - radius, y - radius, z - radius,
                x + radius, y + radius, z + radius
        );
        double radiusSquared = radius * radius;
        Entity nearest = null;
        double nearestDistanceSquared = radiusSquared;

        for (Entity entity : level.getEntities((Entity) null, bounds,
                MovingStructureBridge::isCompatibleMovingEntity)) {
            double dx = entity.getX() - x;
            double dy = entity.getY() - y;
            double dz = entity.getZ() - z;
            double distanceSquared = dx * dx + dy * dy + dz * dz;
            if (distanceSquared <= nearestDistanceSquared) {
                nearest = entity;
                nearestDistanceSquared = distanceSquared;
            }
        }

        if (nearest == null) {
            return 0;
        }

        double distance = Math.sqrt(nearestDistanceSquared);
        double scale = strength * Math.max(0.15D, 1.0D - distance / radius);
        nearest.setDeltaMovement(nearest.getDeltaMovement().add(
                direction.getStepX() * scale,
                direction.getStepY() * scale,
                direction.getStepZ() * scale
        ));
        nearest.hurtMarked = true;
        return 1;
    }

    private static boolean isCompatibleMovingEntity(Entity entity) {
        String className = entity.getClass().getName().toLowerCase(Locale.ROOT);
        return className.contains("contraption")
                || className.contains("airship")
                || className.contains("aeronaut");
    }

    private static boolean applySableImpulse(
            ServerLevel level,
            BlockPos position,
            Direction direction,
            double strength
    ) {
        SableBindings bindings = getSableBindings();
        if (bindings == null) {
            return false;
        }

        try {
            Object subLevel = bindings.getContaining.invoke(bindings.helper, level, position);
            if (subLevel == null || !bindings.serverSubLevelType.isInstance(subLevel)) {
                return false;
            }

            Object container = bindings.getContainer.invoke(null, level);
            if (container == null || !bindings.serverContainerType.isInstance(container)) {
                return false;
            }

            Object physicsSystem = bindings.physicsSystem.invoke(container);
            if (physicsSystem == null) {
                return false;
            }

            Object body = bindings.getPhysicsHandle.invoke(physicsSystem, subLevel);
            if (body == null) {
                return false;
            }

            // Sable expects a position in the sub-level plot and a local impulse.
            Vector3d impulsePosition = new Vector3d(
                    position.getX() + 0.5D,
                    position.getY() + 0.5D,
                    position.getZ() + 0.5D
            );
            Vector3d impulse = new Vector3d(
                    direction.getStepX() * strength,
                    direction.getStepY() * strength,
                    direction.getStepZ() * strength
            );
            bindings.applyImpulseAtPoint.invoke(body, impulsePosition, impulse);
            return true;
        } catch (ReflectiveOperationException | RuntimeException | LinkageError ignored) {
            // Optional integration: retain the entity fallback if the installed
            // Sable version does not expose the expected public API.
            return false;
        }
    }

    private static SableBindings getSableBindings() {
        if (sableBindingsResolved) {
            return sableBindings;
        }

        synchronized (SABLE_BINDINGS_LOCK) {
            if (sableBindingsResolved) {
                return sableBindings;
            }

            try {
                Class<?> sableClass = Class.forName("dev.ryanhcode.sable.Sable");
                Field helperField = sableClass.getField("HELPER");
                Object helper = helperField.get(null);
                if (helper == null) {
                    sableBindingsResolved = true;
                    return null;
                }

                // Resolve exact public signatures. Sable overloads getContaining
                // and getContainer, so selecting by method name + arity is unsafe.
                Method getContaining = helper.getClass().getMethod(
                        "getContaining", Level.class, Vec3i.class
                );
                Class<?> subLevelContainerType = Class.forName(
                        "dev.ryanhcode.sable.api.sublevel.SubLevelContainer"
                );
                Method getContainer = subLevelContainerType.getMethod(
                        "getContainer", ServerLevel.class
                );

                Class<?> serverContainerType = Class.forName(
                        "dev.ryanhcode.sable.api.sublevel.ServerSubLevelContainer"
                );
                Method physicsSystem = serverContainerType.getMethod("physicsSystem");

                Class<?> physicsSystemType = Class.forName(
                        "dev.ryanhcode.sable.sublevel.system.SubLevelPhysicsSystem"
                );
                Class<?> serverSubLevelType = Class.forName(
                        "dev.ryanhcode.sable.sublevel.ServerSubLevel"
                );
                Method getPhysicsHandle = physicsSystemType.getMethod(
                        "getPhysicsHandle", serverSubLevelType
                );

                Class<?> rigidBodyHandleType = Class.forName(
                        "dev.ryanhcode.sable.api.physics.handle.RigidBodyHandle"
                );
                Class<?> vector3dcType = Class.forName("org.joml.Vector3dc");
                Method applyImpulseAtPoint = rigidBodyHandleType.getMethod(
                        "applyImpulseAtPoint", vector3dcType, vector3dcType
                );

                sableBindings = new SableBindings(
                        helper,
                        getContaining,
                        getContainer,
                        serverContainerType,
                        physicsSystem,
                        serverSubLevelType,
                        getPhysicsHandle,
                        applyImpulseAtPoint
                );
            } catch (ReflectiveOperationException | LinkageError ignored) {
                // Cache an unavailable result instead of repeating failed lookups
                // every 10 ticks on servers with a different Sable API version.
                sableBindings = null;
            }

            sableBindingsResolved = true;
            return sableBindings;
        }
    }

    private record SableBindings(
            Object helper,
            Method getContaining,
            Method getContainer,
            Class<?> serverContainerType,
            Method physicsSystem,
            Class<?> serverSubLevelType,
            Method getPhysicsHandle,
            Method applyImpulseAtPoint
    ) {}
}
