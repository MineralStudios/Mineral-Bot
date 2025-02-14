package gg.mineral.bot.api.math;

import lombok.val;
import org.eclipse.jdt.annotation.NonNull;

public interface Positionable {

    /**
     * Gets the X position.
     *
     * @return the X position
     */
    double getX();

    /**
     * Gets the Y position.
     *
     * @return the Y position
     */
    double getY();

    /**
     * Gets the Z position.
     *
     * @return the Z position
     */
    double getZ();

    default double distance3DTo(@NonNull Positionable entityIn) {
        return Math.sqrt(distance3DToSq(entityIn));
    }

    default double distance3DToSq(@NonNull Positionable entityIn) {
        val dX = this.getX() - entityIn.getX();
        val dY = this.getY() - entityIn.getY();
        val dZ = this.getZ() - entityIn.getZ();
        return dX * dX + dY * dY + dZ * dZ;
    }

    default double distance3DToSq(double x, double y, double z) {
        val dX = this.getX() - x;
        val dY = this.getY() - y;
        val dZ = this.getZ() - z;
        return dX * dX + dY * dY + dZ * dZ;
    }

    default double distance2DToSq(double x, double z) {
        val dX = this.getX() - x;
        val dZ = this.getZ() - z;
        return dX * dX + dZ * dZ;
    }

    default double distance2DTo(double x, double z) {
        return Math.sqrt(distance2DToSq(x, z));
    }
}
