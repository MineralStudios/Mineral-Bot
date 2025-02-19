package gg.mineral.bot.api.math

import gg.mineral.bot.api.world.ServerWorld

interface ServerLocation {
    /**
     * Gets the x-coordinate of the location.
     *
     * @return the x-coordinate of the location
     */
    val x: Double

    /**
     * Gets the y-coordinate of the location.
     *
     * @return the y-coordinate of the location
     */
    val y: Double

    /**
     * Gets the z-coordinate of the location.
     *
     * @return the z-coordinate of the location
     */
    val z: Double

    /**
     * Gets the yaw of the location.
     *
     * @return the yaw of the location
     */
    val yaw: Float

    /**
     * Gets the pitch of the location.
     *
     * @return the pitch of the location
     */
    val pitch: Float

    /**
     * Gets the world of the location.
     *
     * @return the world of the location
     */
    val world: ServerWorld<*>
}
