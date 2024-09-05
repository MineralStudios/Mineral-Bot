package gg.mineral.bot.api.inv;

public interface InventoryContainer {

    /**
     * Retrieves the slot at the specified index.
     *
     * @param slot
     *            the index of the slot to retrieve
     * @return the slot at the specified index
     */
    Slot getSlot(int slot);
}
