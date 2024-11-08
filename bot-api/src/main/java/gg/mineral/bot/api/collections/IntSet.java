package gg.mineral.bot.api.collections;

public interface IntSet {

    /**
     * Adds the specified element to this set if it is not already present.
     *
     * @param k element to be added to this set
     * @return {@code true} if this set did not already contain the specified
     *         element
     */
    boolean add(final int k);

    /**
     * Removes the specified element from this set if it is present.
     *
     * @param k element to be removed from this set, if present
     * @return {@code true} if this set contained the specified element
     */
    boolean remove(final int k);
}
