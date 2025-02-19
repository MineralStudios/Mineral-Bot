package gg.mineral.bot.base.client.collections

import gg.mineral.bot.api.collections.IntSet
import gg.mineral.bot.api.collections.OptimizedCollections
import it.unimi.dsi.fastutil.ints.IntOpenHashSet
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet

class OptimizedCollections : OptimizedCollections {
    override fun <T> newSet(): Set<T> {
        return ObjectOpenHashSet()
    }

    override fun <K, V> newMap(): Map<K, V> {
        return Object2ObjectOpenHashMap()
    }

    override fun newIntSet(): IntSet {
        return IntHashSet()
    }

    class IntHashSet : IntOpenHashSet(), IntSet
}
