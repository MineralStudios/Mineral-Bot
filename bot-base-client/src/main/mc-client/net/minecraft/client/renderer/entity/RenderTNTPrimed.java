package net.minecraft.client.renderer.entity;

import gg.mineral.bot.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;

public class RenderTNTPrimed extends Render {
    private RenderBlocks blockRenderer;

    public RenderTNTPrimed(Minecraft mc) {
        super(mc);
        blockRenderer = new RenderBlocks(mc);
        this.shadowSize = 0.5F;
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
    public void doRender(EntityTNTPrimed p_76986_1_, double p_76986_2_, double p_76986_4_, double p_76986_6_,
                         float p_76986_8_, float p_76986_9_) {
        GL11.glPushMatrix();
        GL11.glTranslatef((float) p_76986_2_, (float) p_76986_4_, (float) p_76986_6_);
        float var10;

        if ((float) p_76986_1_.fuse - p_76986_9_ + 1.0F < 10.0F) {
            var10 = 1.0F - ((float) p_76986_1_.fuse - p_76986_9_ + 1.0F) / 10.0F;

            if (var10 < 0.0F) {
                var10 = 0.0F;
            }

            if (var10 > 1.0F) {
                var10 = 1.0F;
            }

            var10 *= var10;
            var10 *= var10;
            float var11 = 1.0F + var10 * 0.3F;
            GL11.glScalef(var11, var11, var11);
        }

        var10 = (1.0F - ((float) p_76986_1_.fuse - p_76986_9_ + 1.0F) / 100.0F) * 0.8F;
        this.bindEntityTexture(p_76986_1_);
        this.blockRenderer.renderBlockAsItem(Blocks.tnt, 0, p_76986_1_.getBrightness(p_76986_9_));

        if (p_76986_1_.fuse / 5 % 2 == 0) {
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_DST_ALPHA);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, var10);
            this.blockRenderer.renderBlockAsItem(Blocks.tnt, 0, 1.0F);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
        }

        GL11.glPopMatrix();
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless
     * you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(EntityTNTPrimed p_110775_1_) {
        return TextureMap.locationBlocksTexture;
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless
     * you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(Entity p_110775_1_) {
        return this.getEntityTexture((EntityTNTPrimed) p_110775_1_);
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
        this.doRender((EntityTNTPrimed) p_76986_1_, p_76986_2_, p_76986_4_, p_76986_6_, p_76986_8_, p_76986_9_);
    }
}
