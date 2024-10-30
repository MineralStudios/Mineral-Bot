package gg.mineral.bot.api.instance;

public interface Session {

    /**
     * Gets the player's access token.
     * 
     * @return the player's access token
     */
    String getAccessToken();

    /**
     * Gets the player's session ID.
     * 
     * @return the player's session ID
     */
    String getSessionId();
}
