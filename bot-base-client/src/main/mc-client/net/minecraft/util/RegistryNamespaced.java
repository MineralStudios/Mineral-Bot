package net.minecraft.util;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.eclipse.jdt.annotation.NonNull;

import java.util.Iterator;
import java.util.Map;

public class RegistryNamespaced<V> extends RegistrySimple<String, V> implements IObjectIntIterable {
    /**
     * The backing store that maps Integers to objects.
     */
    protected final ObjectIntIdentityMap<V> underlyingIntegerMap = new ObjectIntIdentityMap<>();
    protected final BiMap<V, String> biMap;

    public RegistryNamespaced() {
        this.biMap = ((BiMap<String, V>) this.registryObjects).inverse();
    }

    /**
     * Adds a new object to this registry, keyed by both the given integer ID and
     * the given string.
     */
    public void addObject(int id, String key, V value) {
        this.underlyingIntegerMap.put(value, id);
        this.putObject(ensureNamespaced(key), value);
        assert getObject(key) != null;
    }

    /**
     * Creates the Map we will use to map keys to their registered values.
     */
    @Override
    protected Map<String, V> createUnderlyingMap() {
        return HashBiMap.create();
    }

    @Override
    public V getObject(String key) {
        return super.getObject(ensureNamespaced(key));
    }

    /**
     * Gets the name we use to identify the given object.
     */
    public String getNameForObject(Object p_148750_1_) {
        return this.biMap.get(p_148750_1_);
    }

    /**
     * Does this registry contain an entry for the given key?
     */
    @Override
    public boolean containsKey(String key) {
        return super.containsKey(ensureNamespaced(key));
    }

    /**
     * Gets the integer ID we use to identify the given object.
     */
    public int getIDForObject(V value) {
        return this.underlyingIntegerMap.getInt(value);
    }

    /**
     * Gets the object identified by the given ID.
     */
    public V getObjectForID(int p_148754_1_) {
        return this.underlyingIntegerMap.getObject(p_148754_1_);
    }

    public @NonNull Iterator<V> iterator() {
        return this.underlyingIntegerMap.iterator();
    }

    /**
     * Gets a value indicating whether this registry contains an object that can be
     * identified by the given integer
     * value
     */
    public boolean containsID(int id) {
        return this.underlyingIntegerMap.contains(id);
    }

    /**
     * Ensures that the given name is indicated by a colon-delimited namespace,
     * prepending "minecraft:" if it is not
     * already.
     */
    private static String ensureNamespaced(String p_148755_0_) {
        return p_148755_0_.indexOf(58) == -1 ? "minecraft:" + p_148755_0_ : p_148755_0_;
    }
}
