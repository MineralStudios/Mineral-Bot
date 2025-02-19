package gg.mineral.bot.api.collections

interface OptimizedCollections {
    fun <T> newSet(): MutableSet<T>

    fun <K, V> newMap(): MutableMap<K, V>

    fun newIntSet(): IntSet
}
