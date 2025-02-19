package gg.mineral.bot.api.inv.potion

import gg.mineral.bot.api.entity.effect.PotionEffect

interface Potion {
    /**
     * Returns whether this potion is a splash potion.
     *
     * @return true if the potion is a splash potion, false otherwise.
     */
    val isSplash: Boolean

    /**
     * Returns the list of potion effects.
     *
     * @return the list of potion effects
     */
    val effects: List<PotionEffect>
}
