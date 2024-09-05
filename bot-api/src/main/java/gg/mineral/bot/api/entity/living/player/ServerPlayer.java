package gg.mineral.bot.api.entity.living.player;

import gg.mineral.bot.api.world.ServerWorld;

public interface ServerPlayer {

    /**
     * Get the player's entity ID.
     *
     * @return the player's entity ID
     */
    public int getId();

    /**
     * Synchronizes the player's inventory.
     */
    public void syncInventory();

    /**
     * Sets the position of the player.
     *
     * @param x
     *            the x-coordinate of the position
     * @param y
     *            the y-coordinate of the position
     * @param z
     *            the z-coordinate of the position
     */
    public void setPosition(double x, double y, double z);

    /**
     * Sets the yaw and pitch of the player.
     *
     * @param yaw
     *            the yaw value
     * @param pitch
     *            the pitch value
     */
    public void setYawPitch(float yaw, float pitch);

    /**
     * Sends the supported channels to the player.
     */
    public void sendSupportedChannels();

    /**
     * Gets the index of the item in the player's hand.
     *
     * @return the index of the item in hand
     */
    public int getItemInHandIndex();

    /**
     * Spawns the player in the specified world.
     *
     * @param world
     *            the world to spawn the player in
     */
    public void spawnInWorld(ServerWorld<?> world);

    /**
     * Gets the name of the player.
     *
     * @return the name of the player
     */
    public String getName();

    /**
     * Called when the player joins the server.
     */
    public void onJoin();

    /**
     * Gets the ID of the player's game mode.
     *
     * @return the ID of the game mode
     */
    public int getGameModeId();

    /**
     * Gets the ID of the player's dimension.
     *
     * @return the ID of the dimension
     */
    public int getDimensionId();

    /**
     * Gets the ID of the player's difficulty.
     *
     * @return the ID of the difficulty
     */
    public int getDifficultyId();

    /**
     * Sets the resource pack for the player.
     *
     * @param resourcePack
     *            the URL of the resource pack
     * @param resourcePackHash
     *            the hash of the resource pack
     */
    public void setResourcePack(String resourcePack, String resourcePackHash);

    /**
     * Initializes the game mode for the player.
     */
    public void initializeGameMode();

    /**
     * Checks if the player is in hardcore mode.
     *
     * @return true if the player is in hardcore mode, false otherwise
     */
    public boolean isWorldHardcore();

    /**
     * Gets the maximum number of players allowed on the server.
     *
     * @return the maximum number of players
     */
    public int getMaxPlayers();

    /**
     * Gets the name of the world type.
     *
     * @return the name of the world type
     */
    public String getWorldTypeName();

    /**
     * Checks if the player has reduced debug information.
     *
     * @return true if the player has reduced debug information, false otherwise
     */
    public boolean isReducedDebugInfo();

    /**
     * Gets the name of the server mod.
     *
     * @return the name of the server mod
     */
    public String getServerModName();

    /**
     * Sends the scoreboard to the player.
     */
    public void sendScoreboard();

    /**
     * Resets the player sample update timer.
     */
    public void resetPlayerSampleUpdateTimer();

    /**
     * Checks if the player's difficulty is locked.
     *
     * @return true if the player's difficulty is locked, false otherwise
     */
    public boolean isDifficultyLocked();

    /**
     * Sends the player's location to the client.
     */
    public void sendLocationToClient();

    /**
     * Initializes the world for the player.
     */
    public void initWorld();

    /**
     * Initializes the resource pack for the player.
     */
    public void initResourcePack();

    /**
     * Calls the spawn events for the player.
     */
    public void callSpawnEvents();

    /**
     * Checks if the player is sprinting.
     *
     * @return true if the player is sprinting, false otherwise
     */
    public boolean isSprinting();

    /**
     * Gets the spawn coordinates of the world.
     *
     * @return an array containing the x, y, and z coordinates of the spawn
     */
    int[] getWorldSpawn();
}
