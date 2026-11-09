package dev.anton.aeroindustrial.compat;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.lang.reflect.Method;
import java.util.Locale;
import java.util.Optional;

public final class CreateKineticBridge {
    private static final ClassValue<Optional<Method>> SPEED_METHODS = new ClassValue<>() {
        @Override
        protected Optional<Method> computeValue(Class<?> type) {
            return findPublicNoArgMethod(type, "getSpeed");
        }
    };

    private static final ClassValue<Optional<Method>> THEORETICAL_SPEED_METHODS = new ClassValue<>() {
        @Override
        protected Optional<Method> computeValue(Class<?> type) {
            return findPublicNoArgMethod(type, "getTheoreticalSpeed");
        }
    };

    private CreateKineticBridge() {}

    public static int harvest(Level level, BlockPos center, int radius, int maxInput) {
        if (!IntegrationStatus.createLoaded() || maxInput <= 0 || radius < 0) {
            return 0;
        }

        int total = 0;
        for (BlockPos pos : BlockPos.betweenClosed(
                center.offset(-radius, -radius, -radius),
                center.offset(radius, radius, radius)
        )) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity == null) {
                continue;
            }

            String className = blockEntity.getClass().getName().toLowerCase(Locale.ROOT);
            // Keep this bridge restricted to Create-owned block entities.
            if (!className.startsWith("com.simibubi.create.")) {
                continue;
            }

            Number speed = invokeNumber(blockEntity, SPEED_METHODS);
            if (speed == null) {
                speed = invokeNumber(blockEntity, THEORETICAL_SPEED_METHODS);
            }
            if (speed == null) {
                continue;
            }

            double absoluteSpeed = Math.abs(speed.doubleValue());
            if (!Double.isFinite(absoluteSpeed) || absoluteSpeed < 1.0D) {
                continue;
            }

            int sourceInput = (int) Math.min(40.0D, absoluteSpeed);
            total += Math.min(sourceInput, maxInput - total);
            if (total >= maxInput) {
                return total;
            }
        }
        return total;
    }

    private static Number invokeNumber(Object target, ClassValue<Optional<Method>> methodCache) {
        Method method = methodCache.get(target.getClass()).orElse(null);
        if (method == null) {
            return null;
        }

        try {
            Object value = method.invoke(target);
            return value instanceof Number number ? number : null;
        } catch (ReflectiveOperationException | RuntimeException ignored) {
            return null;
        }
    }

    private static Optional<Method> findPublicNoArgMethod(Class<?> type, String name) {
        try {
            return Optional.of(type.getMethod(name));
        } catch (NoSuchMethodException | SecurityException ignored) {
            return Optional.empty();
        }
    }
}
