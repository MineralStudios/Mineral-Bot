package gg.mineral.bot.api.world;

import gg.mineral.bot.api.entity.ClientEntity;
import gg.mineral.bot.api.world.block.Block;

import java.util.Collection;

public interface ClientWorld {

    /**
     * Gets all entities in the world.
     *
     * @return all entities in the world
     */
    Collection<ClientEntity> getEntities();

    /**
     * Gets the entity with the specified ID.
     *
     * @param entityId the entity ID
     * @return the entity with the specified ID
     */
    ClientEntity getEntityByID(int entityId);

    /**
     * Gets the block at the specified coordinates.
     *
     * @param x the x-coordinate
     * @param y the y-coordinate
     * @param z the z-coordinate
     * @return the block at the specified coordinates
     */
    Block getBlockAt(int x, int y, int z);

    /**
     * Gets the block at the specified coordinates.
     *
     * @param x the x-coordinate
     * @param y the y-coordinate
     * @param z the z-coordinate
     * @return the block at the specified coordinates
     */
    Block getBlockAt(double x, double y, double z);
}
