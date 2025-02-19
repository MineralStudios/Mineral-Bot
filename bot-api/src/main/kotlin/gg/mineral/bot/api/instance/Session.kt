package gg.mineral.bot.api.instance

interface Session {
    /**
     * Gets the player's access token.
     *
     * @return the player's access token
     */
    val accessToken: String

    /**
     * Gets the player's session ID.
     *
     * @return the player's session ID
     */
    val sessionId: String

    /**
     * Gets the player's username.
     *
     * @return the player's username
     */
    val username: String
}
