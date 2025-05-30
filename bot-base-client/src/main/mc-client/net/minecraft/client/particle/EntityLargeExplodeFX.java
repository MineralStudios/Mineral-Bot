package net.minecraft.client.particle;

import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import gg.mineral.bot.lwjgl.opengl.GL11;

public class EntityLargeExplodeFX extends EntityFX {
    private static final ResourceLocation field_110127_a = new ResourceLocation("textures/entity/explosion.png");
    private int field_70581_a;
    private int field_70584_aq;

    /**
     * The Rendering Engine.
     */
    private TextureManager theRenderEngine;
    private float field_70582_as;

    public EntityLargeExplodeFX(TextureManager p_i1213_1_, World p_i1213_2_, double p_i1213_3_, double p_i1213_5_,
                                double p_i1213_7_, double p_i1213_9_, double p_i1213_11_, double p_i1213_13_) {
        super(p_i1213_2_, p_i1213_3_, p_i1213_5_, p_i1213_7_, 0.0D, 0.0D, 0.0D);
        this.theRenderEngine = p_i1213_1_;
        this.field_70584_aq = 6 + this.rand.nextInt(4);
        this.particleRed = this.particleGreen = this.particleBlue = this.rand.nextFloat() * 0.6F + 0.4F;
        this.field_70582_as = 1.0F - (float) p_i1213_9_ * 0.5F;
    }

    public void renderParticle(Tessellator p_70539_1_, float p_70539_2_, float p_70539_3_, float p_70539_4_,
                               float p_70539_5_, float p_70539_6_, float p_70539_7_) {
        int var8 = (int) (((float) this.field_70581_a + p_70539_2_) * 15.0F / (float) this.field_70584_aq);

        if (var8 <= 15) {
            this.theRenderEngine.bindTexture(field_110127_a);
            float var9 = (float) (var8 % 4) / 4.0F;
            float var10 = var9 + 0.24975F;
            float var11 = (float) (var8 / 4) / 4.0F;
            float var12 = var11 + 0.24975F;
            float var13 = 2.0F * this.field_70582_as;
            float var14 = (float) (this.prevPosX + (this.posX - this.prevPosX) * (double) p_70539_2_ - interpPosX);
            float var15 = (float) (this.prevPosY + (this.posY - this.prevPosY) * (double) p_70539_2_ - interpPosY);
            float var16 = (float) (this.prevPosZ + (this.posZ - this.prevPosZ) * (double) p_70539_2_ - interpPosZ);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glDisable(GL11.GL_LIGHTING);
            RenderHelper.disableStandardItemLighting();
            p_70539_1_.startDrawingQuads();
            p_70539_1_.setColorRGBA_F(this.particleRed, this.particleGreen, this.particleBlue, 1.0F);
            p_70539_1_.setNormal(0.0F, 1.0F, 0.0F);
            p_70539_1_.setBrightness(240);
            p_70539_1_.addVertexWithUV((double) (var14 - p_70539_3_ * var13 - p_70539_6_ * var13),
                    (double) (var15 - p_70539_4_ * var13), (double) (var16 - p_70539_5_ * var13 - p_70539_7_ * var13),
                    (double) var10, (double) var12);
            p_70539_1_.addVertexWithUV((double) (var14 - p_70539_3_ * var13 + p_70539_6_ * var13),
                    (double) (var15 + p_70539_4_ * var13), (double) (var16 - p_70539_5_ * var13 + p_70539_7_ * var13),
                    (double) var10, (double) var11);
            p_70539_1_.addVertexWithUV((double) (var14 + p_70539_3_ * var13 + p_70539_6_ * var13),
                    (double) (var15 + p_70539_4_ * var13), (double) (var16 + p_70539_5_ * var13 + p_70539_7_ * var13),
                    (double) var9, (double) var11);
            p_70539_1_.addVertexWithUV((double) (var14 + p_70539_3_ * var13 - p_70539_6_ * var13),
                    (double) (var15 - p_70539_4_ * var13), (double) (var16 + p_70539_5_ * var13 - p_70539_7_ * var13),
                    (double) var9, (double) var12);
            p_70539_1_.draw();
            GL11.glPolygonOffset(0.0F, 0.0F);
            GL11.glEnable(GL11.GL_LIGHTING);
        }
    }

    public int getBrightnessForRender(float p_70070_1_) {
        return 61680;
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        ++this.field_70581_a;

        if (this.field_70581_a == this.field_70584_aq) {
            this.setDead();
        }
    }

    public int getFXLayer() {
        return 3;
    }
}
