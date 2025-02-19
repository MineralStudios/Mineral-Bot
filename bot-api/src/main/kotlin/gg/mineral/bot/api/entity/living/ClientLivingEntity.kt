package gg.mineral.bot.api.entity.living

import gg.mineral.bot.api.entity.ClientEntity

interface ClientLivingEntity : ClientEntity {
    /**
     * Gets the entity's head Y position.
     *
     * @return the entity's head Y position
     */
    val headY: Double

    /**
     * Gets the entity's active potion effect IDs.
     *
     * @return the entity's active potion effect IDs
     */
    val activePotionEffectIds: IntArray

    /**
     * Checks if the entity has an active potion effect.
     *
     * @param potionId the potion ID
     * @return `true` if the entity has an active potion effect, else `false`
     */
    fun isPotionActive(potionId: Int): Boolean

    /**
     * Gets the entity's health.
     *
     * @return the entity's health
     */
    val health: Float
}
