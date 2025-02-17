package gg.mineral.bot.api.entity.living.player;

import gg.mineral.bot.api.instance.ClientInstance;

public interface FakePlayer extends ClientPlayerMP {

    /**
     * Gets the client instance.
     *
     * @return the client instance
     */
    ClientInstance getClientInstance();
}
