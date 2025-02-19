package gg.mineral.bot.api.entity

import gg.mineral.bot.api.math.BoundingBox
import gg.mineral.bot.api.math.Positionable
import gg.mineral.bot.api.world.ClientWorld
import java.util.*

interface ClientEntity : Positionable {
    /**
     * Gets the entity's UUID.
     *
     * @return the entity's UUID
     */
    val uuid: UUID

    /**
     * Gets the entity's bounding box.
     *
     * @return the entity's bounding box
     */
    val collidingBoundingBox: BoundingBox?

    /**
     * Gets the entity's ID.
     *
     * @return the entity's ID
     */
    val entityId: Int

    /**
     * Gets the entity's yaw.
     *
     * @return the entity's yaw
     */
    val yaw: Float

    /**
     * Gets the entity's pitch.
     *
     * @return the entity's pitch
     */
    val pitch: Float

    /**
     * Gets whether the entity is on the ground.
     *
     * @return true if the entity is on the ground
     */
    val isOnGround: Boolean

    /**
     * Gets the entity's last X position.
     *
     * @return the entity's last X position
     */
    val lastX: Double

    /**
     * Gets the entity's last Y position.
     *
     * @return the entity's last Y position
     */
    val lastY: Double

    /**
     * Gets the entity's last Z position.
     *
     * @return the entity's last Z position
     */
    val lastZ: Double

    /**
     * Gets the entity's x motion.
     *
     * @return the entity's x motion
     */
    var motionX: Double

    /**
     * Gets the entity's y motion.
     *
     * @return the entity's y motion
     */
    var motionY: Double

    /**
     * Gets the entity's z motion.
     *
     * @return the entity's z motion
     */
    var motionZ: Double

    /**
     * Gets the entity's world.
     *
     * @return the entity's world
     */
    val world: ClientWorld

    /**
     * Gets the entity's random.
     *
     * @return the entity's random
     */
    val random: Random

    /**
     * Gets the entity's bounding box.
     *
     * @return the entity's bounding box
     */
    var boundingBox: BoundingBox

    /**
     * Gets whether the entity is sprinting.
     *
     * @return true if the entity is sprinting
     */
    val isSprinting: Boolean
}
