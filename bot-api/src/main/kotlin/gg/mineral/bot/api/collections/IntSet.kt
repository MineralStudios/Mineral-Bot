package gg.mineral.bot.api.collections

interface IntSet {
    /**
     * Adds the specified element to this set if it is not already present.
     *
     * @param k element to be added to this set
     * @return `true` if this set did not already contain the specified
     * element
     */
    fun add(k: Int): Boolean

    /**
     * Removes the specified element from this set if it is present.
     *
     * @param k element to be removed from this set, if present
     * @return `true` if this set contained the specified element
     */
    fun remove(k: Int): Boolean
}
