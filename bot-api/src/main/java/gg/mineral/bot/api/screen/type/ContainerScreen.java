package gg.mineral.bot.api.screen.type;

import gg.mineral.bot.api.inv.Slot;
import gg.mineral.bot.api.screen.Screen;

public interface ContainerScreen extends Screen {
    /**
     * Returns the X coordinate of the given slot.
     *
     * @param slot
     *            the slot to get the X coordinate of
     * @return the X coordinate of the slot
     */
    int getSlotX(Slot slot);

    /**
     * Returns the Y-coordinate of the specified slot.
     *
     * @param slot
     *            the slot for which to retrieve the Y-coordinate
     * @return the Y-coordinate of the specified slot
     */
    int getSlotY(Slot slot);
}
