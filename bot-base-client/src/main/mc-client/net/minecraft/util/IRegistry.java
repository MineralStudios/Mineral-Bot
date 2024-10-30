package net.minecraft.util;

public interface IRegistry<K, V> {
    V getObject(K key);

    /**
     * Register an object on this registry.
     */
    void putObject(K key, V value);
}
