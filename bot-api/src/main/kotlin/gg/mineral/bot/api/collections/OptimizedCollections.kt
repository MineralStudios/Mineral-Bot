package gg.mineral.bot.api.collections

interface OptimizedCollections {
    fun <T> newSet(): MutableSet<T>

    fun <K, V> newMap(): Map<K, V>

    fun newIntSet(): IntSet
}
