package gg.mineral.bot.base.client.collections;

import gg.mineral.bot.api.collections.IntSet;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;

import java.util.Map;
import java.util.Set;

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

    public static class IntHashSet extends IntOpenHashSet implements IntSet {
    }

}
