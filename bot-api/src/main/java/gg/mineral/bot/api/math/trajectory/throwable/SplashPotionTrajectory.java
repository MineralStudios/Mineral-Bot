package gg.mineral.bot.api.math.trajectory.throwable;

import gg.mineral.bot.api.world.ClientWorld;

public class SplashPotionTrajectory extends ThrowableEntityTrajectory {

    public SplashPotionTrajectory(ClientWorld world, double x, double y, double z, float yaw, float pitch, CollisionFunction collisionFunction) {
        super(world, x, y, z, yaw, pitch, collisionFunction);
    }

    @Override
    public float offset() {
        return -20.0f;
    }

    @Override
    public float power() {
        return 0.5f;
    }

    @Override
    public float gravity() {
        return 0.05f;
    }
}
