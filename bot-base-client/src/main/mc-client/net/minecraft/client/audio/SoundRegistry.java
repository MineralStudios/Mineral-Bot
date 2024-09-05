package net.minecraft.client.audio;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import net.minecraft.util.RegistrySimple;

public class SoundRegistry extends RegistrySimple {
    private Object2ObjectOpenHashMap<Object, Object> field_148764_a;
    private static final String __OBFID = "CL_00001151";

    /**
     * Creates the Map we will use to map keys to their registered values.
     */
    protected Object2ObjectOpenHashMap<Object, Object> createUnderlyingMap() {
        this.field_148764_a = new Object2ObjectOpenHashMap<Object, Object>();
        return this.field_148764_a;
    }

    public void func_148762_a(SoundEventAccessorComposite p_148762_1_) {
        this.putObject(p_148762_1_.func_148729_c(), p_148762_1_);
    }

    public void func_148763_c() {
        this.field_148764_a.clear();
    }
}
