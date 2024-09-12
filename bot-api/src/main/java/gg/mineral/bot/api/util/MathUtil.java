package gg.mineral.bot.api.util;

import gg.mineral.bot.api.entity.ClientEntity;
import gg.mineral.bot.api.entity.living.ClientLivingEntity;

public interface MathUtil {
    public static final double M_PI_4 = Math.PI * 0.25;

    default float angleDifference(float angle1, float angle2) {
        float difference = angle2 - angle1;
        difference = (difference + 180) % 360;
        if (difference < 0)
            difference += 360;
        return difference - 180;
    }

    default float absAngleDifference(float angle1, float angle2) {
        return (float) abs(angleDifference(angle1, angle2));
    }

    default double fastArcTan(final double a) {
        if (a < -1 || a > 1)
            return Math.atan(a);
        return M_PI_4 * a - a * (abs(a) - 1)
                * (0.2447 + 0.0663 * abs(a));
    }

    default double toDegrees(double angle) {
        return angle * 180.0 / Math.PI;
    }

    default double toRadians(double angle) {
        return angle * Math.PI / 180.0;
    }

    default double sqrt(double x) {
        return Math.sqrt(x);
    }

    default float[] computeOptimalYawAndPitch(ClientLivingEntity player,
            ClientLivingEntity entity) {
        double y = entity.getHeadY() - player.getHeadY(), x = entity.getX() - player.getX();
        double z = entity.getZ() - player.getZ();

        float newPitch = y != 0 ? (float) -toDegrees(fastArcTan(y / sqrt(x * x + z * z))) : 0;
        float newYaw = (float) toDegrees(-fastArcTan(x / z));

        if (z < 0.0D && x < 0.0D)
            newYaw = (float) (90.0D + toDegrees(fastArcTan(z / x)));
        else if (z < 0.0D && x > 0.0D)
            newYaw = (float) (-90.0D + toDegrees(fastArcTan(z / x)));

        return new float[] { newPitch, newYaw };
    }

    default float computeOptimalYaw(ClientLivingEntity entity, ClientEntity target) {
        double x = target.getX() - entity.getX(), z = target.getZ() - entity.getZ();

        float newYaw = (float) toDegrees(-fastArcTan(x / z));

        if (z < 0.0D && x < 0.0D)
            newYaw = (float) (90.0D + toDegrees(fastArcTan(z / x)));
        else if (z < 0.0D && x > 0.0D)
            newYaw = (float) (-90.0D + toDegrees(fastArcTan(z / x)));

        return newYaw;
    }

    default float min(float a, float b) {
        return Math.min(a, b);
    }

    default float max(float a, float b) {
        return Math.max(a, b);
    }

    default double min(double a, double b) {
        return Math.min(a, b);
    }

    default double max(double a, double b) {
        return Math.max(a, b);
    }

    default long min(long a, long b) {
        return Math.min(a, b);
    }

    default long max(long a, long b) {
        return Math.max(a, b);
    }

    default double abs(double a) {
        return Math.abs(a);
    }

    default float signum(float a) {
        return Math.signum(a);
    }

    default double tan(double a) {
        return Math.tan(a);
    }

    default double cos(double a) {
        return Math.cos(a);
    }

    default double sin(double a) {
        return Math.sin(a);
    }

    default float sin(float a) {
        return (float) Math.sin(a);
    }

    default float cos(float a) {
        return (float) Math.cos(a);
    }

    default double[] vectorForRotation(float pitch, float yaw) {
        float f = cos(-yaw * 0.017453292F - (float) Math.PI);
        float f1 = sin(-yaw * 0.017453292F - (float) Math.PI);
        float f2 = -cos(-pitch * 0.017453292F);
        float f3 = sin(-pitch * 0.017453292F);
        return new double[] { (double) (f1 * f2), (double) f3, (double) (f * f2) };
    }
}
