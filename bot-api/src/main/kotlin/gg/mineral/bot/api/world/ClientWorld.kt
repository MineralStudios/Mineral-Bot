package gg.mineral.bot.api.world

import gg.mineral.bot.api.entity.ClientEntity
import gg.mineral.bot.api.world.block.Block

interface ClientWorld {
    /**
     * Gets all entities in the world.
     *
     * @return all entities in the world
     */
    val entities: Collection<ClientEntity>

    /**
     * Gets the entity with the specified ID.
     *
     * @param entityId the entity ID
     * @return the entity with the specified ID
     */
    fun getEntityByID(entityId: Int): ClientEntity?

    /**
     * Gets the block at the specified coordinates.
     *
     * @param x the x-coordinate
     * @param y the y-coordinate
     * @param z the z-coordinate
     * @return the block at the specified coordinates
     */
    fun getBlockAt(x: Int, y: Int, z: Int): Block

    /**
     * Gets the block at the specified coordinates.
     *
     * @param x the x-coordinate
     * @param y the y-coordinate
     * @param z the z-coordinate
     * @return the block at the specified coordinates
     */
    fun getBlockAt(x: Double, y: Double, z: Double): Block

    /**
     * Creates a deep copy of this world.
     *
     * @return a deep copy of this world
     */
    fun deepCopy(): ClientWorld
}
