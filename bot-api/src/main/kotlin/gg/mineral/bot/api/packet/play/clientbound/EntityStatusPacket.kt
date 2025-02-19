package gg.mineral.bot.api.packet.play.clientbound

import gg.mineral.bot.api.entity.ClientEntity
import gg.mineral.bot.api.packet.ClientboundPacket
import gg.mineral.bot.api.world.ClientWorld

interface EntityStatusPacket : ClientboundPacket {
    /**
     * Get the entity ID.
     *
     * @return the entity ID
     */
    /**
     * Set the entity ID.
     *
     * @param entityId
     * the entity ID
     */
    var entityId: Int

    /**
     * Get the status.
     *
     * @return the status
     */
    /**
     * Set the status.
     *
     * @param status
     * the status
     */
    var status: Byte

    /**
     * Get the entity.
     *
     * @param world
     * the world the entity is in
     * @return the entity
     */
    fun getEntity(world: ClientWorld): ClientEntity?

    /**
     * Set the entity.
     *
     * @param entity
     * the entity
     */
    fun setEntity(entity: ClientEntity)
}
