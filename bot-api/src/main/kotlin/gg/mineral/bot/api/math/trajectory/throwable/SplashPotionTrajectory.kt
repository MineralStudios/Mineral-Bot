package gg.mineral.bot.api.math.trajectory.throwable

import gg.mineral.bot.api.math.trajectory.Trajectory
import gg.mineral.bot.api.world.ClientWorld

open class SplashPotionTrajectory(
    world: ClientWorld,
    x: Double,
    y: Double,
    z: Double,
    yaw: Float,
    pitch: Float,
    collisionFunction: Trajectory.CollisionFunction
) :
    ThrowableEntityTrajectory(world, x, y, z, yaw, pitch, collisionFunction) {
    override fun offset(): Float {
        return -20.0f
    }

    override fun power(): Float {
        return 0.5f
    }

    override fun gravity(): Float {
        return 0.05f
    }
}
