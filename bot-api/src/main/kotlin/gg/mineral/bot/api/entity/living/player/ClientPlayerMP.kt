package gg.mineral.bot.api.entity.living.player

interface ClientPlayerMP : ClientPlayer {
    /**
     * Gets the player's last reported X position.
     *
     * @return the player's last reported X position
     */
    val lastReportedX: Double

    /**
     * Gets the player's last reported Y position.
     *
     * @return the player's last reported Y position
     */
    val lastReportedY: Double

    /**
     * Gets the player's last reported Z position.
     *
     * @return the player's last reported Z position
     */
    val lastReportedZ: Double
}
