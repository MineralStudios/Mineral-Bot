package net.minecraft.client.renderer.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import gg.mineral.bot.lwjgl.opengl.GL11;
import gg.mineral.bot.lwjgl.opengl.GL12;

public class RenderPainting extends Render {
    public RenderPainting(Minecraft mc) {
        super(mc);
    }

    private static final ResourceLocation field_110807_a = new ResourceLocation(
            "textures/painting/paintings_kristoffer_zetterstrand.png");

    /**
     * Actually renders the given argument. This is a synthetic bridge method,
     * always casting down its argument and then
     * handing it off to a worker function which does the actual work. In all
     * probabilty, the class Render is generic
     * (Render<T extends Entity) and this method has signature public void
     * doRender(T entity, double d, double d1,
     * double d2, float f, float f1). But JAD is pre 1.5 so doesn't do that.
     */
    public void doRender(EntityPainting p_76986_1_, double p_76986_2_, double p_76986_4_, double p_76986_6_,
                         float p_76986_8_, float p_76986_9_) {
        GL11.glPushMatrix();
        GL11.glTranslated(p_76986_2_, p_76986_4_, p_76986_6_);
        GL11.glRotatef(p_76986_8_, 0.0F, 1.0F, 0.0F);
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        this.bindEntityTexture(p_76986_1_);
        EntityPainting.EnumArt var10 = p_76986_1_.art;
        float var11 = 0.0625F;
        GL11.glScalef(var11, var11, var11);
        this.func_77010_a(p_76986_1_, var10.sizeX, var10.sizeY, var10.offsetX, var10.offsetY);
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        GL11.glPopMatrix();
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless
     * you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(EntityPainting p_110775_1_) {
        return field_110807_a;
    }

    private void func_77010_a(EntityPainting p_77010_1_, int p_77010_2_, int p_77010_3_, int p_77010_4_,
                              int p_77010_5_) {
        float var6 = (float) (-p_77010_2_) / 2.0F;
        float var7 = (float) (-p_77010_3_) / 2.0F;
        float var8 = 0.5F;
        float var9 = 0.75F;
        float var10 = 0.8125F;
        float var11 = 0.0F;
        float var12 = 0.0625F;
        float var13 = 0.75F;
        float var14 = 0.8125F;
        float var15 = 0.001953125F;
        float var16 = 0.001953125F;
        float var17 = 0.7519531F;
        float var18 = 0.7519531F;
        float var19 = 0.0F;
        float var20 = 0.0625F;

        for (int var21 = 0; var21 < p_77010_2_ / 16; ++var21) {
            for (int var22 = 0; var22 < p_77010_3_ / 16; ++var22) {
                float var23 = var6 + (float) ((var21 + 1) * 16);
                float var24 = var6 + (float) (var21 * 16);
                float var25 = var7 + (float) ((var22 + 1) * 16);
                float var26 = var7 + (float) (var22 * 16);
                this.func_77008_a(p_77010_1_, (var23 + var24) / 2.0F, (var25 + var26) / 2.0F);
                float var27 = (float) (p_77010_4_ + p_77010_2_ - var21 * 16) / 256.0F;
                float var28 = (float) (p_77010_4_ + p_77010_2_ - (var21 + 1) * 16) / 256.0F;
                float var29 = (float) (p_77010_5_ + p_77010_3_ - var22 * 16) / 256.0F;
                float var30 = (float) (p_77010_5_ + p_77010_3_ - (var22 + 1) * 16) / 256.0F;
                Tessellator var31 = this.mc.getTessellator();

                if (var31 == null)
                    return;
                var31.startDrawingQuads();
                var31.setNormal(0.0F, 0.0F, -1.0F);
                var31.addVertexWithUV((double) var23, (double) var26, (double) (-var8), (double) var28, (double) var29);
                var31.addVertexWithUV((double) var24, (double) var26, (double) (-var8), (double) var27, (double) var29);
                var31.addVertexWithUV((double) var24, (double) var25, (double) (-var8), (double) var27, (double) var30);
                var31.addVertexWithUV((double) var23, (double) var25, (double) (-var8), (double) var28, (double) var30);
                var31.setNormal(0.0F, 0.0F, 1.0F);
                var31.addVertexWithUV((double) var23, (double) var25, (double) var8, (double) var9, (double) var11);
                var31.addVertexWithUV((double) var24, (double) var25, (double) var8, (double) var10, (double) var11);
                var31.addVertexWithUV((double) var24, (double) var26, (double) var8, (double) var10, (double) var12);
                var31.addVertexWithUV((double) var23, (double) var26, (double) var8, (double) var9, (double) var12);
                var31.setNormal(0.0F, 1.0F, 0.0F);
                var31.addVertexWithUV((double) var23, (double) var25, (double) (-var8), (double) var13, (double) var15);
                var31.addVertexWithUV((double) var24, (double) var25, (double) (-var8), (double) var14, (double) var15);
                var31.addVertexWithUV((double) var24, (double) var25, (double) var8, (double) var14, (double) var16);
                var31.addVertexWithUV((double) var23, (double) var25, (double) var8, (double) var13, (double) var16);
                var31.setNormal(0.0F, -1.0F, 0.0F);
                var31.addVertexWithUV((double) var23, (double) var26, (double) var8, (double) var13, (double) var15);
                var31.addVertexWithUV((double) var24, (double) var26, (double) var8, (double) var14, (double) var15);
                var31.addVertexWithUV((double) var24, (double) var26, (double) (-var8), (double) var14, (double) var16);
                var31.addVertexWithUV((double) var23, (double) var26, (double) (-var8), (double) var13, (double) var16);
                var31.setNormal(-1.0F, 0.0F, 0.0F);
                var31.addVertexWithUV((double) var23, (double) var25, (double) var8, (double) var18, (double) var19);
                var31.addVertexWithUV((double) var23, (double) var26, (double) var8, (double) var18, (double) var20);
                var31.addVertexWithUV((double) var23, (double) var26, (double) (-var8), (double) var17, (double) var20);
                var31.addVertexWithUV((double) var23, (double) var25, (double) (-var8), (double) var17, (double) var19);
                var31.setNormal(1.0F, 0.0F, 0.0F);
                var31.addVertexWithUV((double) var24, (double) var25, (double) (-var8), (double) var18, (double) var19);
                var31.addVertexWithUV((double) var24, (double) var26, (double) (-var8), (double) var18, (double) var20);
                var31.addVertexWithUV((double) var24, (double) var26, (double) var8, (double) var17, (double) var20);
                var31.addVertexWithUV((double) var24, (double) var25, (double) var8, (double) var17, (double) var19);
                var31.draw();
            }
        }
    }

    private void func_77008_a(EntityPainting p_77008_1_, float p_77008_2_, float p_77008_3_) {
        int var4 = MathHelper.floor_double(p_77008_1_.posX);
        int var5 = MathHelper.floor_double(p_77008_1_.posY + (double) (p_77008_3_ / 16.0F));
        int var6 = MathHelper.floor_double(p_77008_1_.posZ);

        if (p_77008_1_.hangingDirection == 2) {
            var4 = MathHelper.floor_double(p_77008_1_.posX + (double) (p_77008_2_ / 16.0F));
        }

        if (p_77008_1_.hangingDirection == 1) {
            var6 = MathHelper.floor_double(p_77008_1_.posZ - (double) (p_77008_2_ / 16.0F));
        }

        if (p_77008_1_.hangingDirection == 0) {
            var4 = MathHelper.floor_double(p_77008_1_.posX - (double) (p_77008_2_ / 16.0F));
        }

        if (p_77008_1_.hangingDirection == 3) {
            var6 = MathHelper.floor_double(p_77008_1_.posZ + (double) (p_77008_2_ / 16.0F));
        }

        int var7 = this.renderManager.worldObj.getLightBrightnessForSkyBlocks(var4, var5, var6, 0);
        int var8 = var7 % 65536;
        int var9 = var7 / 65536;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float) var8, (float) var9);
        GL11.glColor3f(1.0F, 1.0F, 1.0F);
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless
     * you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(Entity p_110775_1_) {
        return this.getEntityTexture((EntityPainting) p_110775_1_);
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
        this.doRender((EntityPainting) p_76986_1_, p_76986_2_, p_76986_4_, p_76986_6_, p_76986_8_, p_76986_9_);
    }
}
