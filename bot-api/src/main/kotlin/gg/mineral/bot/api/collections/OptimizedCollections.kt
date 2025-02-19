package gg.mineral.bot.api.collections

interface OptimizedCollections {
    fun <T> newSet(): Set<T>

    fun <K, V> newMap(): Map<K, V>

    fun newIntSet(): IntSet
}
