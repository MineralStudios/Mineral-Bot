package gg.mineral.bot.api.math.simulation;

import gg.mineral.bot.api.controls.Keyboard;
import gg.mineral.bot.api.controls.Mouse;
import gg.mineral.bot.api.math.BoundingBox;

public interface PlayerMotionSimulator extends MotionSimulator {

    /**
     * Gets the keyboard.
     *
     * @return the keyboard
     */
    Keyboard getKeyboard();

    /**
     * Gets the mouse.
     *
     * @return the mouse
     */
    Mouse getMouse();

    /**
     * Gets the pitch.
     *
     * @return the pitch
     */
    float getPitch();

    /**
     * Gets the yaw.
     *
     * @return the yaw
     */
    float getYaw();

    /**
     * Gets the bounding box.
     *
     * @return the bounding box
     */
    BoundingBox getBoundingBox();

    /**
     * Resets the player's motion simulator.
     */
    void reset();
}
