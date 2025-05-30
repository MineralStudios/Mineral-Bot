package net.minecraft.client.renderer.entity;

import gg.mineral.bot.lwjgl.opengl.GL11;
import gg.mineral.bot.lwjgl.opengl.GL12;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

public class RenderXPOrb extends Render {
    private static final ResourceLocation experienceOrbTextures = new ResourceLocation(
            "textures/entity/experience_orb.png");

    public RenderXPOrb(Minecraft mc) {
        super(mc);
        this.shadowSize = 0.15F;
        this.shadowOpaque = 0.75F;
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
    public void doRender(EntityXPOrb p_76986_1_, double p_76986_2_, double p_76986_4_, double p_76986_6_,
                         float p_76986_8_, float p_76986_9_) {
        GL11.glPushMatrix();
        GL11.glTranslatef((float) p_76986_2_, (float) p_76986_4_, (float) p_76986_6_);
        this.bindEntityTexture(p_76986_1_);
        int var10 = p_76986_1_.getTextureByXP();
        float var11 = (float) (var10 % 4 * 16 + 0) / 64.0F;
        float var12 = (float) (var10 % 4 * 16 + 16) / 64.0F;
        float var13 = (float) (var10 / 4 * 16 + 0) / 64.0F;
        float var14 = (float) (var10 / 4 * 16 + 16) / 64.0F;
        float var15 = 1.0F;
        float var16 = 0.5F;
        float var17 = 0.25F;
        int var18 = p_76986_1_.getBrightnessForRender(p_76986_9_);
        int var19 = var18 % 65536;
        int var20 = var18 / 65536;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float) var19 / 1.0F, (float) var20 / 1.0F);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        float var26 = 255.0F;
        float var27 = ((float) p_76986_1_.xpColor + p_76986_9_) / 2.0F;
        var20 = (int) ((MathHelper.sin(var27 + 0.0F) + 1.0F) * 0.5F * var26);
        int var21 = (int) var26;
        int var22 = (int) ((MathHelper.sin(var27 + 4.1887903F) + 1.0F) * 0.1F * var26);
        int var23 = var20 << 16 | var21 << 8 | var22;
        GL11.glRotatef(180.0F - this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(-this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
        float var24 = 0.3F;
        GL11.glScalef(var24, var24, var24);
        Tessellator var25 = this.mc.getTessellator();
        if (var25 == null)
            return;
        var25.startDrawingQuads();
        var25.setColorRGBA_I(var23, 128);
        var25.setNormal(0.0F, 1.0F, 0.0F);
        var25.addVertexWithUV((double) (0.0F - var16), (double) (0.0F - var17), 0.0D, (double) var11, (double) var14);
        var25.addVertexWithUV((double) (var15 - var16), (double) (0.0F - var17), 0.0D, (double) var12, (double) var14);
        var25.addVertexWithUV((double) (var15 - var16), (double) (1.0F - var17), 0.0D, (double) var12, (double) var13);
        var25.addVertexWithUV((double) (0.0F - var16), (double) (1.0F - var17), 0.0D, (double) var11, (double) var13);
        var25.draw();
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        GL11.glPopMatrix();
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless
     * you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(EntityXPOrb p_110775_1_) {
        return experienceOrbTextures;
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless
     * you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(Entity p_110775_1_) {
        return this.getEntityTexture((EntityXPOrb) p_110775_1_);
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
        this.doRender((EntityXPOrb) p_76986_1_, p_76986_2_, p_76986_4_, p_76986_6_, p_76986_8_, p_76986_9_);
    }
}
