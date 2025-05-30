package gg.mineral.bot.api.screen.type

import gg.mineral.bot.api.inv.Slot
import gg.mineral.bot.api.screen.Screen

interface ContainerScreen : Screen {
    /**
     * Returns the X coordinate of the given slot.
     *
     * @param slot the slot to get the X coordinate of
     * @return the X coordinate of the slot
     */
    fun getSlotX(slot: Slot): Int

    /**
     * Returns the Y-coordinate of the specified slot.
     *
     * @param slot the slot for which to retrieve the Y-coordinate
     * @return the Y-coordinate of the specified slot
     */
    fun getSlotY(slot: Slot): Int

    fun getSlotXScaled(slot: Slot, displayWidth: Int): Int {
        return getSlotX(slot) * displayWidth / this.width
    }

    fun getSlotYScaled(slot: Slot, displayHeight: Int): Int {
        return displayHeight - ((getSlotY(slot) + 1) * displayHeight / this.height)
    }

    /**
     * Returns the scale factor of the screen.
     *
     * @return the scale factor of the screen
     */
    val scaleFactor: Int
}
