package net.minecraft.server.management;

import org.eclipse.jdt.annotation.NonNull;

import java.util.*;

public class LowerStringMap implements Map {
    private final Map internalMap = new LinkedHashMap();

    public int size() {
        return this.internalMap.size();
    }

    public boolean isEmpty() {
        return this.internalMap.isEmpty();
    }

    public boolean containsKey(Object p_containsKey_1_) {
        return this.internalMap.containsKey(p_containsKey_1_.toString().toLowerCase());
    }

    public boolean containsValue(Object p_containsValue_1_) {
        return this.internalMap.containsKey(p_containsValue_1_);
    }

    public Object get(Object p_get_1_) {
        return this.internalMap.get(p_get_1_.toString().toLowerCase());
    }

    public Object put(String p_put_1_, Object p_put_2_) {
        return this.internalMap.put(p_put_1_.toLowerCase(), p_put_2_);
    }

    public Object remove(Object p_remove_1_) {
        return this.internalMap.remove(p_remove_1_.toString().toLowerCase());
    }

    public void putAll(Map p_putAll_1_) {
        Iterator var2 = p_putAll_1_.entrySet().iterator();

        while (var2.hasNext()) {
            Entry var3 = (Entry) var2.next();
            this.put((String) var3.getKey(), var3.getValue());
        }
    }

    public void clear() {
        this.internalMap.clear();
    }

    public @NonNull Set keySet() {
        return this.internalMap.keySet();
    }

    public @NonNull Collection values() {
        return this.internalMap.values();
    }

    public @NonNull Set entrySet() {
        return this.internalMap.entrySet();
    }

    public Object put(Object p_put_1_, Object p_put_2_) {
        return this.put((String) p_put_1_, p_put_2_);
    }
}
