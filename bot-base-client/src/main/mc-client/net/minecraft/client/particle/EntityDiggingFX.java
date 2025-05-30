package net.minecraft.client.particle;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class EntityDiggingFX extends EntityFX {
    private Block field_145784_a;

    public EntityDiggingFX(World p_i1234_1_, double p_i1234_2_, double p_i1234_4_, double p_i1234_6_, double p_i1234_8_,
            double p_i1234_10_, double p_i1234_12_, Block p_i1234_14_, int p_i1234_15_) {
        super(p_i1234_1_, p_i1234_2_, p_i1234_4_, p_i1234_6_, p_i1234_8_, p_i1234_10_, p_i1234_12_);
        this.field_145784_a = p_i1234_14_;
        IIcon icon = p_i1234_14_.getIcon(0, p_i1234_15_);

        if (icon != null)
            this.setParticleIcon(icon);
        this.particleGravity = p_i1234_14_.blockParticleGravity;
        this.particleRed = this.particleGreen = this.particleBlue = 0.6F;
        this.particleScale /= 2.0F;
    }

    /**
     * If the block has a colour multiplier, copies it to this particle and returns
     * this particle.
     */
    public EntityDiggingFX applyColourMultiplier(int p_70596_1_, int p_70596_2_, int p_70596_3_) {
        if (this.field_145784_a == Blocks.grass) {
            return this;
        } else {
            int var4 = this.field_145784_a.colorMultiplier(this.worldObj, p_70596_1_, p_70596_2_, p_70596_3_);
            this.particleRed *= (float) (var4 >> 16 & 255) / 255.0F;
            this.particleGreen *= (float) (var4 >> 8 & 255) / 255.0F;
            this.particleBlue *= (float) (var4 & 255) / 255.0F;
            return this;
        }
    }

    /**
     * Creates a new EntityDiggingFX with the block render color applied to the base
     * particle color
     */
    public EntityDiggingFX applyRenderColor(int p_90019_1_) {
        if (this.field_145784_a == Blocks.grass) {
            return this;
        } else {
            int var2 = this.field_145784_a.getRenderColor(p_90019_1_);
            this.particleRed *= (float) (var2 >> 16 & 255) / 255.0F;
            this.particleGreen *= (float) (var2 >> 8 & 255) / 255.0F;
            this.particleBlue *= (float) (var2 & 255) / 255.0F;
            return this;
        }
    }

    public int getFXLayer() {
        return 1;
    }

    public void renderParticle(Tessellator p_70539_1_, float p_70539_2_, float p_70539_3_, float p_70539_4_,
            float p_70539_5_, float p_70539_6_, float p_70539_7_) {
        float var8 = ((float) this.particleTextureIndexX + this.particleTextureJitterX / 4.0F) / 16.0F;
        float var9 = var8 + 0.015609375F;
        float var10 = ((float) this.particleTextureIndexY + this.particleTextureJitterY / 4.0F) / 16.0F;
        float var11 = var10 + 0.015609375F;
        float var12 = 0.1F * this.particleScale;

        if (this.particleIcon != null) {
            var8 = this.particleIcon.getInterpolatedU((double) (this.particleTextureJitterX / 4.0F * 16.0F));
            var9 = this.particleIcon.getInterpolatedU((double) ((this.particleTextureJitterX + 1.0F) / 4.0F * 16.0F));
            var10 = this.particleIcon.getInterpolatedV((double) (this.particleTextureJitterY / 4.0F * 16.0F));
            var11 = this.particleIcon.getInterpolatedV((double) ((this.particleTextureJitterY + 1.0F) / 4.0F * 16.0F));
        }

        float var13 = (float) (this.prevPosX + (this.posX - this.prevPosX) * (double) p_70539_2_ - interpPosX);
        float var14 = (float) (this.prevPosY + (this.posY - this.prevPosY) * (double) p_70539_2_ - interpPosY);
        float var15 = (float) (this.prevPosZ + (this.posZ - this.prevPosZ) * (double) p_70539_2_ - interpPosZ);
        p_70539_1_.setColorOpaque_F(this.particleRed, this.particleGreen, this.particleBlue);
        p_70539_1_.addVertexWithUV((double) (var13 - p_70539_3_ * var12 - p_70539_6_ * var12),
                (double) (var14 - p_70539_4_ * var12), (double) (var15 - p_70539_5_ * var12 - p_70539_7_ * var12),
                (double) var8, (double) var11);
        p_70539_1_.addVertexWithUV((double) (var13 - p_70539_3_ * var12 + p_70539_6_ * var12),
                (double) (var14 + p_70539_4_ * var12), (double) (var15 - p_70539_5_ * var12 + p_70539_7_ * var12),
                (double) var8, (double) var10);
        p_70539_1_.addVertexWithUV((double) (var13 + p_70539_3_ * var12 + p_70539_6_ * var12),
                (double) (var14 + p_70539_4_ * var12), (double) (var15 + p_70539_5_ * var12 + p_70539_7_ * var12),
                (double) var9, (double) var10);
        p_70539_1_.addVertexWithUV((double) (var13 + p_70539_3_ * var12 - p_70539_6_ * var12),
                (double) (var14 - p_70539_4_ * var12), (double) (var15 + p_70539_5_ * var12 - p_70539_7_ * var12),
                (double) var9, (double) var11);
    }
}
