package gg.mineral.bot.api.entity;

import java.util.UUID;

import org.eclipse.jdt.annotation.Nullable;

import gg.mineral.bot.api.math.BoundingBox;
import gg.mineral.bot.api.math.Positionable;

public interface ClientEntity extends Positionable {
    /**
     * Gets the entity's UUID.
     * 
     * @return the entity's UUID
     */
    UUID getUuid();

    /**
     * Gets the entity's bounding box.
     * 
     * @return the entity's bounding box
     */
    @Nullable
    BoundingBox getBoundingBox();

    /**
     * Gets the entity's ID.
     * 
     * @return the entity's ID
     */
    int getEntityId();

    /**
     * Gets the entity's X position.
     * 
     * @return the entity's X position
     */
    double getX();

    /**
     * Gets the entity's Y position.
     * 
     * @return the entity's Y position
     */
    double getY();

    /**
     * Gets the entity's Z position.
     * 
     * @return the entity's Z position
     */
    double getZ();

    /**
     * Gets the entity's yaw.
     * 
     * @return the entity's yaw
     */
    float getYaw();

    /**
     * Gets the entity's pitch.
     * 
     * @return the entity's pitch
     */
    float getPitch();
}
