package gg.mineral.bot.api.math.simulation;

import gg.mineral.bot.api.math.Positionable;

public interface MotionSimulator extends Positionable {
    /**
     * Simulates the motion of the entity for the given amount of milliseconds.
     *
     * @param millis the amount of milliseconds to simulate motion for
     */
    void execute(long millis);
}
