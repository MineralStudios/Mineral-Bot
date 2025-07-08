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
    
    companion object {
        fun fromVelocity(
            world: ClientWorld,
            x: Double,
            y: Double,
            z: Double,
            motionX: Double,
            motionY: Double,
            motionZ: Double,
            collisionFunction: Trajectory.CollisionFunction
        ): SplashPotionTrajectory {
            // Create with dummy yaw/pitch
            val trajectory = SplashPotionTrajectory(world, x, y, z, 0f, 0f, collisionFunction)
            // Override the motion values
            trajectory.motX = motionX.toFloat()
            trajectory.motY = motionY.toFloat()
            trajectory.motZ = motionZ.toFloat()
            // Reset position since constructor modifies it
            trajectory.x = x
            trajectory.y = y
            trajectory.z = z
            return trajectory
        }
    }
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
