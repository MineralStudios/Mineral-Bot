package gg.mineral.bot.api.entity;

import gg.mineral.bot.api.math.BoundingBox;
import gg.mineral.bot.api.math.Positionable;
import gg.mineral.bot.api.world.ClientWorld;
import org.eclipse.jdt.annotation.Nullable;

import java.util.Random;
import java.util.UUID;

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
    BoundingBox getCollidingBoundingBox();

    /**
     * Gets the entity's ID.
     *
     * @return the entity's ID
     */
    int getEntityId();

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

    /**
     * Gets whether the entity is on the ground.
     *
     * @return true if the entity is on the ground
     */
    boolean isOnGround();

    /**
     * Gets the entity's last X position.
     *
     * @return the entity's last X position
     */
    double getLastX();

    /**
     * Gets the entity's last Y position.
     *
     * @return the entity's last Y position
     */
    double getLastY();

    /**
     * Gets the entity's last Z position.
     *
     * @return the entity's last Z position
     */
    double getLastZ();

    /**
     * Gets the entity's x motion.
     *
     * @return the entity's x motion
     */
    double getMotionX();

    /**
     * Gets the entity's y motion.
     *
     * @return the entity's y motion
     */
    double getMotionY();

    /**
     * Gets the entity's z motion.
     *
     * @return the entity's z motion
     */
    double getMotionZ();

    /**
     * Gets the entity's world.
     *
     * @return the entity's world
     */
    @Nullable
    ClientWorld getWorld();

    /**
     * Gets the entity's random.
     *
     * @return the entity's random
     */
    Random getRandom();

    /**
     * Gets the entity's bounding box.
     *
     * @return the entity's bounding box
     */
    BoundingBox getBoundingBox();

    /**
     * Gets whether the entity is sprinting.
     *
     * @return true if the entity is sprinting
     */
    boolean isSprinting();
}
