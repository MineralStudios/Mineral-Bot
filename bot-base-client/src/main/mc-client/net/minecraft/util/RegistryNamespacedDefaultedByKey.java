package net.minecraft.util;

import lombok.RequiredArgsConstructor;
import lombok.val;

@RequiredArgsConstructor
public class RegistryNamespacedDefaultedByKey<V> extends RegistryNamespaced<V> {
    private final String defaultKey;
    private V defaultValue;

    /**
     * Adds a new object to this registry, keyed by both the given integer ID and
     * the given string.
     */
    @Override
    public void addObject(int id, String key, V value) {
        if (this.defaultKey.equals(key))
            this.defaultValue = value;

        super.addObject(id, key, value);
    }

    @Override
    public V getObject(String p_82594_1_) {
        val value = super.getObject(p_82594_1_);
        return value == null ? this.defaultValue : value;
    }

    /**
     * Gets the object identified by the given ID.
     */
    public V getObjectForID(int p_148754_1_) {
        V value = super.getObjectForID(p_148754_1_);
        return value == null ? this.defaultValue : value;
    }
}
