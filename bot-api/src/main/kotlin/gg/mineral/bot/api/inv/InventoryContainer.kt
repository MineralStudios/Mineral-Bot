package gg.mineral.bot.api.inv

interface InventoryContainer {
    /**
     * Retrieves the slot at the specified index.
     *
     * @param inventory
     * @param slot
     * the index of the slot to retrieve
     *
     * @return the slot at the specified index
     */
    fun getSlot(inventory: Inventory, slot: Int): Slot?
}
