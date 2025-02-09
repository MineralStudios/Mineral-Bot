package gg.mineral.bot.api.screen.type;

import gg.mineral.bot.api.inv.Slot;
import gg.mineral.bot.api.screen.Screen;

public interface ContainerScreen extends Screen {
    /**
     * Returns the X coordinate of the given slot.
     *
     * @param slot the slot to get the X coordinate of
     * @return the X coordinate of the slot
     */
    int getSlotX(Slot slot);

    /**
     * Returns the Y-coordinate of the specified slot.
     *
     * @param slot the slot for which to retrieve the Y-coordinate
     * @return the Y-coordinate of the specified slot
     */
    int getSlotY(Slot slot);

    default int getSlotXScaled(Slot slot, int displayWidth) {
        return getSlotX(slot) * displayWidth / this.getWidth();
    }

    default int getSlotYScaled(Slot slot, int displayHeight) {
        return displayHeight - ((getSlotY(slot) + 1) * displayHeight / this.getHeight());
    }

    /**
     * Returns the scale factor of the screen.
     *
     * @return the scale factor of the screen
     */
    int getScaleFactor();
}
