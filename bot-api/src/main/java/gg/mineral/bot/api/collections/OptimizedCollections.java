package gg.mineral.bot.api.collections;

import java.util.Map;
import java.util.Set;

public interface OptimizedCollections {
    <T> Set<T> newSet();

    <K, V> Map<K, V> newMap();

    IntSet newIntSet();
}
