package gg.mineral.bot.api.entity.living.player;

import gg.mineral.bot.api.instance.ClientInstance;
import gg.mineral.bot.api.math.simulation.PlayerMotionSimulator;

public interface FakePlayer extends ClientPlayerMP {

    /**
     * Gets the client instance.
     *
     * @return the client instance
     */
    ClientInstance getClientInstance();

    /**
     * Gets the player's motion simulator.
     *
     * @return the player's motion simulator
     */
    PlayerMotionSimulator getMotionSimulator();
}
