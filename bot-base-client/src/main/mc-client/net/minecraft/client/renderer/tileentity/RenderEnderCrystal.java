package net.minecraft.client.renderer.tileentity;

import gg.mineral.bot.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelEnderCrystal;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

public class RenderEnderCrystal extends Render {
    private static final ResourceLocation enderCrystalTextures = new ResourceLocation(
            "textures/entity/endercrystal/endercrystal.png");
    private ModelBase field_76995_b;

    public RenderEnderCrystal(Minecraft mc) {
        super(mc);
        this.shadowSize = 0.5F;
        this.field_76995_b = new ModelEnderCrystal(mc, 0.0F, true);
    }

    /**
     * Actually renders the given argument. This is a synthetic bridge method,
     * always casting down its argument and then
     * handing it off to a worker function which does the actual work. In all
     * probabilty, the class Render is generic
     * (Render<T extends Entity) and this method has signature public void
     * doRender(T entity, double d, double d1,
     * double d2, float f, float f1). But JAD is pre 1.5 so doesn't do that.
     */
    public void doRender(EntityEnderCrystal p_76986_1_, double p_76986_2_, double p_76986_4_, double p_76986_6_,
                         float p_76986_8_, float p_76986_9_) {
        float var10 = (float) p_76986_1_.innerRotation + p_76986_9_;
        GL11.glPushMatrix();
        GL11.glTranslatef((float) p_76986_2_, (float) p_76986_4_, (float) p_76986_6_);
        this.bindTexture(enderCrystalTextures);
        float var11 = MathHelper.sin(var10 * 0.2F) / 2.0F + 0.5F;
        var11 += var11 * var11;
        this.field_76995_b.render(p_76986_1_, 0.0F, var10 * 3.0F, var11 * 0.2F, 0.0F, 0.0F, 0.0625F);
        GL11.glPopMatrix();
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless
     * you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(EntityEnderCrystal p_110775_1_) {
        return enderCrystalTextures;
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless
     * you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(Entity p_110775_1_) {
        return this.getEntityTexture((EntityEnderCrystal) p_110775_1_);
    }

    /**
     * Actually renders the given argument. This is a synthetic bridge method,
     * always casting down its argument and then
     * handing it off to a worker function which does the actual work. In all
     * probabilty, the class Render is generic
     * (Render<T extends Entity) and this method has signature public void
     * doRender(T entity, double d, double d1,
     * double d2, float f, float f1). But JAD is pre 1.5 so doesn't do that.
     */
    public void doRender(Entity p_76986_1_, double p_76986_2_, double p_76986_4_, double p_76986_6_, float p_76986_8_,
                         float p_76986_9_) {
        this.doRender((EntityEnderCrystal) p_76986_1_, p_76986_2_, p_76986_4_, p_76986_6_, p_76986_8_, p_76986_9_);
    }
}
