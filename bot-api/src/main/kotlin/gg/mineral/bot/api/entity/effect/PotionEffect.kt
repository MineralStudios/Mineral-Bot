package gg.mineral.bot.api.entity.effect

interface PotionEffect {
    /**
     * Returns the ID of the potion effect.
     *
     * @return the ID of the potion effect
     */
    val potionID: Int

    val type: PotionEffectType?
        /**
         * Returns the type of this potion effect.
         *
         * @return the type of this potion effect
         */
        get() = PotionEffectType.getById(potionID)

    /**
     * Returns the amplifier level of this potion effect.
     *
     * @return the amplifier level of this potion effect
     */
    val amplifier: Int

    /**
     * Returns the duration of this potion effect.
     *
     * @return the duration of this potion effect
     */
    val duration: Int
}
