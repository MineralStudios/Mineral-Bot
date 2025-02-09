package gg.mineral.bot.api.entity.living;

import gg.mineral.bot.api.entity.ClientEntity;

public interface ClientLivingEntity extends ClientEntity {
    /**
     * Gets the entity's head Y position.
     *
     * @return the entity's head Y position
     */
    double getHeadY();

    /**
     * Gets the entity's active potion effect IDs.
     *
     * @return the entity's active potion effect IDs
     */
    int[] getActivePotionEffectIds();

    /**
     * Checks if the entity has an active potion effect.
     *
     * @param potionId the potion ID
     * @return {@code true} if the entity has an active potion effect, else {@code false}
     */
    boolean isPotionActive(int potionId);

    /**
     * Gets the entity's health.
     *
     * @return the entity's health
     */
    float getHealth();
}
