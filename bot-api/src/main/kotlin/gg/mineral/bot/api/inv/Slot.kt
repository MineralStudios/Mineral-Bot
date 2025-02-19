package gg.mineral.bot.api.inv

interface Slot {
    /**
     * Gets the x position of the slot on the screen.
     *
     * @return the x position of the slot on the screen
     */
    val xDisplayPosition: Int

    /**
     * Gets the y position of the slot on the screen.
     *
     * @return the y position of the slot on the screen
     */
    val yDisplayPosition: Int
}
