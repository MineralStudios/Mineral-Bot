package net.minecraft.client.renderer.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelSpider;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.util.ResourceLocation;
import gg.mineral.bot.lwjgl.opengl.GL11;

public class RenderSpider extends RenderLiving {
    private static final ResourceLocation spiderEyesTextures = new ResourceLocation("textures/entity/spider_eyes.png");
    private static final ResourceLocation spiderTextures = new ResourceLocation("textures/entity/spider/spider.png");

    public RenderSpider(Minecraft mc) {
        super(mc, new ModelSpider(mc), 1.0F);
        this.setRenderPassModel(new ModelSpider(mc));
    }

    protected float getDeathMaxRotation(EntitySpider p_77037_1_) {
        return 180.0F;
    }

    /**
     * Queries whether should render the specified pass or not.
     */
    protected int shouldRenderPass(EntitySpider p_77032_1_, int p_77032_2_, float p_77032_3_) {
        if (p_77032_2_ != 0) {
            return -1;
        } else {
            this.bindTexture(spiderEyesTextures);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glDisable(GL11.GL_ALPHA_TEST);
            GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE);

            if (p_77032_1_.isInvisible()) {
                GL11.glDepthMask(false);
            } else {
                GL11.glDepthMask(true);
            }

            char var4 = 61680;
            int var5 = var4 % 65536;
            int var6 = var4 / 65536;
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float) var5 / 1.0F,
                    (float) var6 / 1.0F);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            return 1;
        }
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless
     * you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(EntitySpider p_110775_1_) {
        return spiderTextures;
    }

    protected float getDeathMaxRotation(EntityLivingBase p_77037_1_) {
        return this.getDeathMaxRotation((EntitySpider) p_77037_1_);
    }

    /**
     * Queries whether should render the specified pass or not.
     */
    protected int shouldRenderPass(EntityLivingBase p_77032_1_, int p_77032_2_, float p_77032_3_) {
        return this.shouldRenderPass((EntitySpider) p_77032_1_, p_77032_2_, p_77032_3_);
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless
     * you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(Entity p_110775_1_) {
        return this.getEntityTexture((EntitySpider) p_110775_1_);
    }
}
