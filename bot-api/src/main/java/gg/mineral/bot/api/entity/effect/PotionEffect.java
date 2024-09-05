package gg.mineral.bot.api.entity.effect;

public interface PotionEffect {
    /**
     * Returns the ID of the potion effect.
     *
     * @return the ID of the potion effect
     */
    int getPotionID();

    /**
     * Returns the type of this potion effect.
     *
     * @return the type of this potion effect
     */
    default PotionEffectType getType() {
        return PotionEffectType.getById(getPotionID());
    }

    /**
     * Returns the amplifier level of this potion effect.
     *
     * @return the amplifier level of this potion effect
     */
    int getAmplifier();

    /**
     * Returns the duration of this potion effect.
     *
     * @return the duration of this potion effect
     */
    int getDuration();
}
