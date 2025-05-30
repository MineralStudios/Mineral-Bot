package net.minecraft.client.renderer.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import gg.mineral.bot.lwjgl.opengl.GL11;

public class RenderEntity extends Render {
    public RenderEntity(Minecraft mc) {
        super(mc);
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
        GL11.glPushMatrix();
        renderOffsetAABB(this.mc, p_76986_1_.boundingBox, p_76986_2_ - p_76986_1_.lastTickPosX,
                p_76986_4_ - p_76986_1_.lastTickPosY, p_76986_6_ - p_76986_1_.lastTickPosZ);
        GL11.glPopMatrix();
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless
     * you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(Entity p_110775_1_) {
        return null;
    }
}
