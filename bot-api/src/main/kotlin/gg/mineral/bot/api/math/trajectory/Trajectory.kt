package gg.mineral.bot.api.math.trajectory

import gg.mineral.bot.api.math.Positionable
import gg.mineral.bot.api.world.ClientWorld

interface Trajectory

    : Positionable {
    enum class Result {
        CONTINUE,
        VALID,
        INVALID
    }

    /**
     * Ticks the trajectory.
     *
     * @return the result of the tick
     */
    fun tick(): Result

    /**
     * Computes the trajectory.
     *
     * @return the result of the computation
     */
    fun compute(maxEval: Int): Result {
        var result = Result.CONTINUE
        var iterations = 0
        while (iterations < maxEval
            && result == Result.CONTINUE
        ) {
            result = tick()
            iterations++
        }
        return result
    }

    /**
     * The x-coordinate of the trajectory.
     *
     * @return the x-coordinate
     */
    override val x: Double

    /**
     * The y-coordinate of the trajectory.
     *
     * @return the y-coordinate
     */
    override val y: Double

    /**
     * The z-coordinate of the trajectory.
     *
     * @return the z-coordinate
     */
    override val z: Double

    /**
     * The collision function of the trajectory.
     *
     * @return the collision function
     */
    val collisionFunction: CollisionFunction

    /**
     * The world of the trajectory.
     *
     * @return the world
     */
    val world: ClientWorld

    fun interface CollisionFunction {
        fun test(x: Double, y: Double, z: Double): Boolean
    }
}
