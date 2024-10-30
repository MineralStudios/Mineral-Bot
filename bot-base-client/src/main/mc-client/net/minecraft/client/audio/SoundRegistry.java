package net.minecraft.client.audio;

import net.minecraft.util.RegistrySimple;
import net.minecraft.util.ResourceLocation;

public class SoundRegistry extends RegistrySimple<ResourceLocation, SoundEventAccessorComposite> {

    public void add(SoundEventAccessorComposite p_148762_1_) {
        this.putObject(p_148762_1_.func_148729_c(), p_148762_1_);
    }

    public void clear() {
        this.registryObjects.clear();
    }
}
