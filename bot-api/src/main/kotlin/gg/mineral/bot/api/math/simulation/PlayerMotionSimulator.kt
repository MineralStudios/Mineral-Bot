package gg.mineral.bot.api.math.simulation

import gg.mineral.bot.api.controls.Keyboard
import gg.mineral.bot.api.controls.Mouse
import gg.mineral.bot.api.math.BoundingBox

interface PlayerMotionSimulator : MotionSimulator {
    /**
     * Gets the keyboard.
     *
     * @return the keyboard
     */
    val keyboard: Keyboard

    /**
     * Gets the mouse.
     *
     * @return the mouse
     */
    val mouse: Mouse

    /**
     * Gets the pitch.
     *
     * @return the pitch
     */
    val pitch: Float

    /**
     * Gets the yaw.
     *
     * @return the yaw
     */
    val yaw: Float

    /**
     * Gets the bounding box.
     *
     * @return the bounding box
     */
    val boundingBox: BoundingBox

    /**
     * Resets the player's motion simulator.
     */
    fun reset()
}
