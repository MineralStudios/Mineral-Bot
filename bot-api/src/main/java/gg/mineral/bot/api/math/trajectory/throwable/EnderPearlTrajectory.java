package gg.mineral.bot.api.math.trajectory.throwable;

import gg.mineral.bot.api.world.ClientWorld;

public class EnderPearlTrajectory extends ThrowableEntityTrajectory {

    public EnderPearlTrajectory(ClientWorld world, double x, double y, double z, float yaw, float pitch, CollisionFunction collisionFunction) {
        super(world, x, y, z, yaw, pitch, collisionFunction);
    }

    @Override
    public float offset() {
        return 0.0F;
    }

    @Override
    public float power() {
        return 1.5F;
    }

    @Override
    public float gravity() {
        return 0.03F;
    }
}
