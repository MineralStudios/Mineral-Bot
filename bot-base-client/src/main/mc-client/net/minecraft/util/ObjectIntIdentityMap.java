package net.minecraft.util;

import com.google.common.base.Predicates;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenCustomHashMap;
import org.eclipse.jdt.annotation.NonNull;

import java.util.Iterator;
import java.util.List;

public class ObjectIntIdentityMap<V> implements IObjectIntIterable {
    private final Object2IntMap<V> underlyingMap = new Object2IntOpenCustomHashMap<V>(
            512,
            new Hash.Strategy<V>() {
                @Override
                public int hashCode(V v) {
                    return System.identityHashCode(v);
                }

                @Override
                public boolean equals(V a, V b) {
                    return a == b; // Identity comparison
                }
            });

    public ObjectIntIdentityMap() {
        this.underlyingMap.defaultReturnValue(-1);
    }

    private List<V> valueList = Lists.newArrayList();

    public void put(V value, int integer) {
        this.underlyingMap.put(value, integer);

        while (this.valueList.size() <= integer)
            this.valueList.add(null);

        this.valueList.set(integer, value);
    }

    public int getInt(V value) {
        return this.underlyingMap.getInt(value);
    }

    public V getObject(int integer) {
        return integer >= 0 && integer < this.valueList.size() ? this.valueList.get(integer)
                : null;
    }

    public @NonNull Iterator<V> iterator() {
        return Iterators.filter(this.valueList.iterator(), Predicates.notNull());
    }

    public boolean contains(int integer) {
        return this.getObject(integer) != null;
    }
}
