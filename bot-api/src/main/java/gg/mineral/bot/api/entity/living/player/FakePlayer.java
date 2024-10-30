package gg.mineral.bot.api.entity.living.player;

import java.util.Set;
import java.util.UUID;

import gg.mineral.bot.api.instance.ClientInstance;

public interface FakePlayer extends ClientPlayerMP {

    /**
     * Gets the player's friendly entity UUIDs.
     * 
     * @return the player's friendly entity UUIDs
     */
    Set<UUID> getFriendlyEntityUUIDs();

    /**
     * Gets the client instance.
     * 
     * @return the client instance
     */
    ClientInstance getClientInstance();
}
