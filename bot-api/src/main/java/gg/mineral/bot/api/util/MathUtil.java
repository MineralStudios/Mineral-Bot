package gg.mineral.bot.api.util;

import gg.mineral.bot.api.entity.living.ClientLivingEntity;
import gg.mineral.bot.api.entity.living.player.ClientPlayer;
import gg.mineral.bot.api.math.Positionable;
import lombok.val;

public interface MathUtil {
    public static final double M_PI_4 = Math.PI * 0.25;

    default float angleDifference(float angle1, float angle2) {
        var difference = angle2 - angle1;
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

    default double fastArcTan2(final double y, final double x) {
        if (x == 0.0) {
            if (y > 0.0)
                return Math.PI / 2;
            else if (y < 0.0)
                return -Math.PI / 2;
            else
                return 0.0; // Undefined, return 0 or handle as needed
        }

        double angle;
        if (Math.abs(x) > Math.abs(y)) {
            // Use y/x if x has a greater magnitude
            double a = y / x;
            angle = fastArcTan(a);

            // Adjust angle based on the quadrant
            if (x < 0.0) {
                if (y >= 0.0)
                    angle += Math.PI;
                else
                    angle -= Math.PI;
            }
        } else {
            // Use x/y if y has a greater magnitude
            double a = x / y;
            angle = fastArcTan(a);

            // Adjust angle based on the sign of y
            if (y > 0.0)
                angle = Math.PI / 2 - angle;
            else
                angle = -Math.PI / 2 - angle;
        }
        return angle;
    }

    default double toDegrees(double angle) {
        return angle * 180.0 / Math.PI;
    }

    default double toRadians(double angle) {
        return angle * Math.PI / 180.0;
    }

    default float toRadians(float angle) {
        return angle * (float) Math.PI / 180.0F;
    }

    default double sqrt(double x) {
        return Math.sqrt(x);
    }

    default float sqrt(float x) {
        return (float) Math.sqrt(x);
    }

    default float[] computeOptimalYawAndPitch(ClientPlayer player,
                                              ClientPlayer entity) {
        double x = entity.getX() - player.getX();
        double y = (entity.getY() + entity.getEyeHeight()) - (player.getY() + player.getEyeHeight()) - 1.9D;
        double z = entity.getZ() - player.getZ();

        val newPitch = y != 0 ? (float) -toDegrees(fastArcTan(y / sqrt(x * x + z * z))) : 0;
        float newYaw;
        if (z < 0.0D && x < 0.0D)
            newYaw = (float) (90.0D + toDegrees(fastArcTan(z / x)));
        else if (z < 0.0D && x > 0.0D)
            newYaw = (float) (-90.0D + toDegrees(fastArcTan(z / x)));
        else
            newYaw = (float) toDegrees(-fastArcTan(x / z));

        return new float[]{newPitch, newYaw};
    }

    default float computeOptimalYaw(ClientLivingEntity entity, Positionable target) {
        double x = target.getX() - entity.getX();
        double z = target.getZ() - entity.getZ();

        float newYaw;
        if (z < 0.0D && x < 0.0D)
            newYaw = (float) (90.0D + toDegrees(fastArcTan(z / x)));
        else if (z < 0.0D && x > 0.0D)
            newYaw = (float) (-90.0D + toDegrees(fastArcTan(z / x)));
        else
            newYaw = (float) toDegrees(-fastArcTan(x / z));

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

    default int floor(double a) {
        return (int) Math.floor(a);
    }

    default double hypot(double a, double b) {
        return Math.hypot(a, b);
    }

    default double[] vectorForRotation(float pitch, float yaw) {
        val f = cos(-yaw * 0.017453292F - (float) Math.PI);
        val f1 = sin(-yaw * 0.017453292F - (float) Math.PI);
        val f2 = -cos(-pitch * 0.017453292F);
        val f3 = sin(-pitch * 0.017453292F);
        return new double[]{(double) (f1 * f2), (double) f3, (double) (f * f2)};
    }

    /**
     * Combines two integers into a long.
     *
     * @param high The high 32 bits of the resulting long.
     * @param low  The low 32 bits of the resulting long.
     * @return A long containing the two input integers.
     */
    default long combineIntsToLong(int high, int low) {
        return (((long) high) << 32) | (low & 0xFFFFFFFFL);
    }

    /**
     * Extracts the high 32 bits from a long as an int.
     *
     * @param value The long value to extract from.
     * @return The high 32 bits of the input long as an int.
     */
    default int highInt(long value) {
        return (int) (value >> 32);
    }

    /**
     * Extracts the low 32 bits from a long as an int.
     *
     * @param value The long value to extract from.
     * @return The low 32 bits of the input long as an int.
     */
    default int lowInt(long value) {
        return (int) value;
    }

}
