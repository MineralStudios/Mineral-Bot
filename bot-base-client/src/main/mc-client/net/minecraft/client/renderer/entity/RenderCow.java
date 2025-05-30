package net.minecraft.client.renderer.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.util.ResourceLocation;

public class RenderCow extends RenderLiving {
    private static final ResourceLocation cowTextures = new ResourceLocation("textures/entity/cow/cow.png");

    public RenderCow(Minecraft mc, ModelBase p_i1253_1_, float p_i1253_2_) {
        super(mc, p_i1253_1_, p_i1253_2_);
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless
     * you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(EntityCow p_110775_1_) {
        return cowTextures;
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless
     * you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(Entity p_110775_1_) {
        return this.getEntityTexture((EntityCow) p_110775_1_);
    }
}
