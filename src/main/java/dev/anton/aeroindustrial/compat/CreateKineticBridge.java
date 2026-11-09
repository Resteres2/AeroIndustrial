package dev.anton.aeroindustrial.compat;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.lang.reflect.Method;

public final class CreateKineticBridge {
    private CreateKineticBridge() {}

    public static int harvest(Level level, BlockPos center, int radius, int maxInput) {
        if (!IntegrationStatus.createLoaded()) return 0;

        int total = 0;
        for (BlockPos pos : BlockPos.betweenClosed(center.offset(-radius, -radius, -radius),
                center.offset(radius, radius, radius))) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be == null) continue;

            String name = be.getClass().getName().toLowerCase();
            if (!name.contains("create.") && !name.contains("kinetic")) continue;

            Number speed = invokeNumber(be, "getSpeed");
            if (speed == null) speed = invokeNumber(be, "getTheoreticalSpeed");
            if (speed != null) {
                total += Math.min(Math.min(40, maxInput - total), Math.max(0, (int) Math.abs(speed.doubleValue())));
                if (total >= maxInput) return total;
            }
        }
        return total;
    }

    private static Number invokeNumber(Object target, String methodName) {
        try {
            Method method = target.getClass().getMethod(methodName);
            Object value = method.invoke(target);
            return value instanceof Number n ? n : null;
        } catch (ReflectiveOperationException | SecurityException ignored) {
            return null;
        }
    }
}
