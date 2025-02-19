package gg.mineral.bot.api.math.trajectory.throwable

import gg.mineral.bot.api.math.trajectory.Trajectory
import gg.mineral.bot.api.world.ClientWorld

class EnderPearlTrajectory(
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
        return 0.0f
    }

    override fun power(): Float {
        return 1.5f
    }

    override fun gravity(): Float {
        return 0.03f
    }
}
