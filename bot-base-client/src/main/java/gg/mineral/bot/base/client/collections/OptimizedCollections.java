package gg.mineral.bot.base.client.collections;

import java.util.Map;
import java.util.Set;

import gg.mineral.bot.api.collections.IntSet;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;

public class OptimizedCollections implements gg.mineral.bot.api.collections.OptimizedCollections {

    @Override
    public <T> Set<T> newSet() {
        return new ObjectOpenHashSet<>();
    }

    @Override
    public <K, V> Map<K, V> newMap() {
        return new Object2ObjectOpenHashMap<>();
    }

    @Override
    public IntSet newIntSet() {
        return new IntHashSet();
    }

    public class IntHashSet extends IntOpenHashSet implements IntSet {
    }

}
