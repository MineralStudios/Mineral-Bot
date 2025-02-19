package gg.mineral.bot.api.entity.living.player

import gg.mineral.bot.api.instance.ClientInstance

interface FakePlayer : ClientPlayerMP {
    /**
     * Gets the client instance.
     *
     * @return the client instance
     */
    val clientInstance: ClientInstance
}
