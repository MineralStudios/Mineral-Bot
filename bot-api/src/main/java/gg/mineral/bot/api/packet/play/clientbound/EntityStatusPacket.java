package gg.mineral.bot.api.packet.play.clientbound;

import gg.mineral.bot.api.entity.ClientEntity;
import gg.mineral.bot.api.packet.ClientboundPacket;
import gg.mineral.bot.api.world.ClientWorld;

public interface EntityStatusPacket extends ClientboundPacket {

    /**
     * Get the entity ID.
     *
     * @return the entity ID
     */
    int getEntityId();

    /**
     * Get the status.
     *
     * @return the status
     */
    byte getStatus();

    /**
     * Get the entity.
     *
     * @param world
     *            the world the entity is in
     * @return the entity
     */
    ClientEntity getEntity(ClientWorld world);

    /**
     * Set the entity ID.
     *
     * @param entityId
     *            the entity ID
     */
    void setEntityId(int entityId);

    /**
     * Set the status.
     *
     * @param status
     *            the status
     */
    void setStatus(byte status);

    /**
     * Set the entity.
     *
     * @param entity
     *            the entity
     */
    void setEntity(ClientEntity entity);
}
