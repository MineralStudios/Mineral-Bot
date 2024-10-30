package net.minecraft.util;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

public class RegistrySimple<K, V> implements IRegistry<K, V> {
    private static final Logger logger = LogManager.getLogger(RegistrySimple.class);

    /** Objects registered on this registry. */
    protected final Map<K, V> registryObjects = this.createUnderlyingMap();

    /**
     * Creates the Map we will use to map keys to their registered values.
     */
    protected Map<K, V> createUnderlyingMap() {
        return new Object2ObjectOpenHashMap<>();
    }

    @Override
    public V getObject(K key) {
        return this.registryObjects.get(key);
    }

    /**
     * Register an object on this registry.
     */
    @Override
    public void putObject(K key, V value) {
        if (this.registryObjects.containsKey(key))
            logger.debug("Adding duplicate key \'" + key + "\' to registry");

        this.registryObjects.put(key, value);
    }

    /**
     * Gets all the keys recognized by this registry.
     */
    public Set<K> getKeys() {
        return Collections.unmodifiableSet(this.registryObjects.keySet());
    }

    /**
     * Does this registry contain an entry for the given key?
     */
    public boolean containsKey(K key) {
        return this.registryObjects.containsKey(key);
    }
}
