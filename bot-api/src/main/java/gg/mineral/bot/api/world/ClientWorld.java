package gg.mineral.bot.api.world;

import java.util.Collection;

import gg.mineral.bot.api.entity.ClientEntity;

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
     * @param entityId
     *            the entity ID
     * @return the entity with the specified ID
     */
    ClientEntity getEntityByID(int entityId);
}
