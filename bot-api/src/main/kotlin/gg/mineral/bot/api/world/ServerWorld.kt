package gg.mineral.bot.api.world

interface ServerWorld<T> {
    /**
     * Gets the handle of the world.
     *
     * @return the handle of the world
     */
    val handle: T
}
