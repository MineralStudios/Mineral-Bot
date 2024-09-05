package gg.mineral.bot.api.world;

public interface ServerWorld<T> {
    /**
     * Gets the handle of the world.
     * 
     * @return the handle of the world
     */
    T getHandle();
}
