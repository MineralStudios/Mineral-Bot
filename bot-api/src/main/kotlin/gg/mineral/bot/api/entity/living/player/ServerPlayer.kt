package gg.mineral.bot.api.entity.living.player

import gg.mineral.bot.api.world.ServerWorld

interface ServerPlayer {
    /**
     * Get the player's entity ID.
     *
     * @return the player's entity ID
     */
    fun getId(): Int

    /**
     * Sets the position of the player.
     *
     * @param x the x-coordinate of the position
     * @param y the y-coordinate of the position
     * @param z the z-coordinate of the position
     */
    fun setPosition(x: Double, y: Double, z: Double)

    /**
     * Sets the yaw and pitch of the player.
     *
     * @param yaw   the yaw value
     * @param pitch the pitch value
     */
    fun setYawPitch(yaw: Float, pitch: Float)

    /**
     * Sends the supported channels to the player.
     */
    fun sendSupportedChannels()

    /**
     * Gets the index of the item in the player's hand.
     *
     * @return the index of the item in hand
     */
    val itemInHandIndex: Int

    /**
     * Spawns the player in the specified world.
     *
     * @param world the world to spawn the player in
     */
    fun spawnInWorld(world: ServerWorld<*>)

    /**
     * Gets the name of the player.
     *
     * @return the name of the player
     */
    fun getName(): String

    /**
     * Called when the player joins the server.
     */
    fun onJoin()

    /**
     * Gets the ID of the player's game mode.
     *
     * @return the ID of the game mode
     */
    val gameModeId: Int

    /**
     * Gets the ID of the player's dimension.
     *
     * @return the ID of the dimension
     */
    val dimensionId: Int

    /**
     * Gets the ID of the player's difficulty.
     *
     * @return the ID of the difficulty
     */
    val difficultyId: Int

    /**
     * Sets the resource pack for the player.
     *
     * @param resourcePack     the URL of the resource pack
     * @param resourcePackHash the hash of the resource pack
     */
    fun setResourcePack(resourcePack: String, resourcePackHash: String)

    /**
     * Initializes the game mode for the player.
     */
    fun initializeGameMode()

    /**
     * Checks if the player is in hardcore mode.
     *
     * @return true if the player is in hardcore mode, false otherwise
     */
    val isWorldHardcore: Boolean

    /**
     * Gets the maximum number of players allowed on the server.
     *
     * @return the maximum number of players
     */
    val maxPlayers: Int

    /**
     * Gets the name of the world type.
     *
     * @return the name of the world type
     */
    val worldTypeName: String

    /**
     * Checks if the player has reduced debug information.
     *
     * @return true if the player has reduced debug information, false otherwise
     */
    val isReducedDebugInfo: Boolean

    /**
     * Gets the name of the server mod.
     *
     * @return the name of the server mod
     */
    val serverModName: String

    /**
     * Sends the scoreboard to the player.
     */
    fun sendScoreboard()

    /**
     * Resets the player sample update timer.
     */
    fun resetPlayerSampleUpdateTimer()

    /**
     * Checks if the player's difficulty is locked.
     *
     * @return true if the player's difficulty is locked, false otherwise
     */
    val isDifficultyLocked: Boolean

    /**
     * Sends the player's location to the client.
     */
    fun sendLocationToClient()

    /**
     * Initializes the world for the player.
     */
    fun initWorld()

    /**
     * Initializes the resource pack for the player.
     */
    fun initResourcePack()

    /**
     * Calls the spawn events for the player.
     */
    fun callSpawnEvents()

    /**
     * Checks if the player is sprinting.
     *
     * @return true if the player is sprinting, false otherwise
     */
    val sprinting: Boolean

    /**
     * Gets the spawn coordinates of the world.
     *
     * @return an array containing the x, y, and z coordinates of the spawn
     */
    val worldSpawn: IntArray
}
