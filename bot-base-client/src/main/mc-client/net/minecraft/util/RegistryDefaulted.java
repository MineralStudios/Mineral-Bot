package net.minecraft.util;

import lombok.RequiredArgsConstructor;
import lombok.val;

@RequiredArgsConstructor
public class RegistryDefaulted<K, V> extends RegistrySimple<K, V> {
    /**
     * Default object for this registry, returned when an object is not found.
     */
    private final V defaultObject;

    @Override
    public V getObject(K key) {
        val value = super.getObject(key);
        return value == null ? this.defaultObject : value;
    }
}
