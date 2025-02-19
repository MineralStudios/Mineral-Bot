package gg.mineral.bot.api.math.simulation

import gg.mineral.bot.api.math.Positionable

interface MotionSimulator : Positionable {
    /**
     * Simulates the motion of the entity for the given amount of milliseconds.
     *
     * @param millis the amount of milliseconds to simulate motion for
     */
    fun execute(millis: Long)
}
