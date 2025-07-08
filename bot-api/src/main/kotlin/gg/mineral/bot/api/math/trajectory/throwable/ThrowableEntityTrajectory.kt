package gg.mineral.bot.api.math.trajectory.throwable

import gg.mineral.bot.api.math.trajectory.Trajectory
import gg.mineral.bot.api.util.MathUtil
import gg.mineral.bot.api.world.ClientWorld
import gg.mineral.bot.api.world.block.Block

abstract class ThrowableEntityTrajectory(
    override val world: ClientWorld,
    override var x: Double,
    override var y: Double,
    override var z: Double,
    yaw: Float,
    pitch: Float,
    override val collisionFunction: Trajectory.CollisionFunction
) :
    MathUtil, Trajectory {
    var airTimeTicks: Int = 0
    protected var motX: Float = 0f
    protected var motY: Float = 0f
    protected var motZ: Float = 0f

    init {
        val yawRadians = toRadians(yaw)
        val pitchRadians = toRadians(pitch)

        val sinYawRadians = sin(yawRadians)
        val cosYawRadians = cos(yawRadians)

        this.x -= (cosYawRadians * 0.16f).toDouble()
        this.y -= 0.10000000149011612
        this.z -= (sinYawRadians * 0.16f).toDouble()
        val multiplier = 0.4f

        val cosPitchRadians = cos(pitchRadians)
        this.motX = -sinYawRadians * cosPitchRadians * multiplier
        this.motZ = cosYawRadians * cosPitchRadians * multiplier
        val offsetPitchRadians = toRadians(pitch + this.offset())
        this.motY = -sin(offsetPitchRadians) * multiplier
        this.shoot(this.motX, this.motY, this.motZ, this.power())
    }

    private fun shoot(motX: Float, motY: Float, motZ: Float, power: Float) {
        var motX = motX
        var motY = motY
        var motZ = motZ
        val magnitude = sqrt(motX * motX + motY * motY + motZ * motZ)

        motX /= magnitude
        motY /= magnitude
        motZ /= magnitude
        motX *= power
        motY *= power
        motZ *= power
        this.motX = motX
        this.motY = motY
        this.motZ = motZ
    }

    /**
     * The pitch offset of the trajectory.
     *
     * @return the pitch offset
     */
    abstract fun offset(): Float

    /**
     * The power of the trajectory.
     *
     * @return the power
     */
    abstract fun power(): Float

    /**
     * The gravity of the trajectory.
     *
     * @return the gravity
     */
    abstract fun gravity(): Float


    override fun tick(): Trajectory.Result {
        val xTile = floor(x)
        val yTile = floor(y)
        val zTile = floor(z)

        val nextLocX = this.x + this.motX
        val nextLocY = this.y + this.motY
        val nextLocZ = this.z + this.motZ
        if (collisionFunction.test(this.x, this.y, this.z)
            || collisionFunction.test(nextLocX, nextLocY, nextLocZ)
        ) return Trajectory.Result.VALID

        val block = world.getBlockAt(xTile, yTile, zTile)

        if (block.id != Block.AIR) {
            val collisionBoundingBox = block.getCollisionBoundingBox(
                world,
                xTile,
                yTile, zTile
            )

            if (collisionBoundingBox != null && collisionBoundingBox.isVecInside(this.x, this.y, this.z)) {
                return Trajectory.Result.INVALID
            }
        }

        ++airTimeTicks

        this.x += motX.toDouble()
        this.y += motY.toDouble()
        this.z += motZ.toDouble()

        val drag = 0.99f

        this.motX *= drag
        this.motY *= drag
        this.motZ *= drag
        this.motY -= gravity()

        return Trajectory.Result.CONTINUE
    }
}
