package gg.mineral.bot.api.math;

import org.eclipse.jdt.annotation.NonNull;

public interface Positionable {

    double getX();

    double getY();

    double getZ();

    default double distance3DTo(@NonNull Positionable entityIn) {
        return Math.sqrt(distance3DToSq(entityIn));
    }

    default double distance3DToSq(@NonNull Positionable entityIn) {
        double dX = this.getX() - entityIn.getX(), dY = this.getY() - entityIn.getY(),
                dZ = this.getZ() - entityIn.getZ();
        return dX * dX + dY * dY + dZ * dZ;
    }

    default double distance3DToSq(double x, double y, double z) {
        double dX = this.getX() - x, dY = this.getY() - y, dZ = this.getZ() - z;
        return dX * dX + dY * dY + dZ * dZ;
    }

    default double distance2DToSq(double x, double z) {
        double dX = this.getX() - x, dZ = this.getZ() - z;
        return dX * dX + dZ * dZ;
    }

    default double distance2DTo(double x, double z) {
        return Math.sqrt(distance2DToSq(x, z));
    }
}
