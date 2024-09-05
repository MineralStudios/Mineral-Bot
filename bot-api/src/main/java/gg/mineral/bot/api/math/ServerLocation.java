package gg.mineral.bot.api.math;

import gg.mineral.bot.api.world.ServerWorld;

public interface ServerLocation {
    /**
     * Gets the x-coordinate of the location.
     * 
     * @return the x-coordinate of the location
     */
    double getX();

    /**
     * Gets the y-coordinate of the location.
     * 
     * @return the y-coordinate of the location
     */
    double getY();

    /**
     * Gets the z-coordinate of the location.
     * 
     * @return the z-coordinate of the location
     */
    double getZ();

    /**
     * Gets the yaw of the location.
     * 
     * @return the yaw of the location
     */
    float getYaw();

    /**
     * Gets the pitch of the location.
     * 
     * @return the pitch of the location
     */
    float getPitch();

    /**
     * Gets the world of the location.
     * 
     * @return the world of the location
     */
    ServerWorld<?> getWorld();
}
