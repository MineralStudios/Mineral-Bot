package optifine;

import gg.mineral.bot.lwjgl.opengl.GL11;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.awt.image.BufferedImage;

public class PlayerItemModel {
    private Dimension textureSize = null;
    private boolean usePlayerTexture = false;
    private PlayerItemRenderer[] modelRenderers = new PlayerItemRenderer[0];
    private ResourceLocation textureLocation = null;
    private BufferedImage textureImage = null;
    private DynamicTexture texture = null;
    private ResourceLocation locationMissing = new ResourceLocation("textures/blocks/wool_colored_red.png");
    public static final int ATTACH_BODY = 0;
    public static final int ATTACH_HEAD = 1;
    public static final int ATTACH_LEFT_ARM = 2;
    public static final int ATTACH_RIGHT_ARM = 3;
    public static final int ATTACH_LEFT_LEG = 4;
    public static final int ATTACH_RIGHT_LEG = 5;
    public static final int ATTACH_CAPE = 6;

    public PlayerItemModel(Dimension textureSize, boolean usePlayerTexture, PlayerItemRenderer[] modelRenderers) {
        this.textureSize = textureSize;
        this.usePlayerTexture = usePlayerTexture;
        this.modelRenderers = modelRenderers;
    }

    public void render(ModelBiped modelBiped, AbstractClientPlayer player, float scale, float partialTicks) {
        TextureManager textureManager = player.getMc().getTextureManager();

        if (this.usePlayerTexture) {
            textureManager.bindTexture(player.getLocationSkin());
        } else if (this.textureLocation != null) {
            if (this.texture == null && this.textureImage != null) {
                TextureUtil textureUtil = player.getMc().textureUtil;

                if (textureUtil != null) {
                    this.texture = new DynamicTexture(player.getMc(), textureUtil.dataBuffer,
                            this.textureImage);
                    TextureManager textureManager1 = player.getMc().getTextureManager();

                    if (textureManager1 != null)
                        textureManager1.loadTexture(this.textureLocation, this.texture);
                }
            }

            textureManager.bindTexture(this.textureLocation);
        } else {
            textureManager.bindTexture(this.locationMissing);
        }

        for (int i = 0; i < this.modelRenderers.length; ++i) {
            PlayerItemRenderer pir = this.modelRenderers[i];
            GL11.glPushMatrix();

            if (player.isSneaking()) {
                GL11.glTranslatef(0.0F, 0.2F, 0.0F);
            }

            pir.render(modelBiped, scale);
            GL11.glPopMatrix();
        }
    }

    public static ModelRenderer getAttachModel(ModelBiped modelBiped, int attachTo) {
        switch (attachTo) {
            case 0:
                return modelBiped.bipedBody;

            case 1:
                return modelBiped.bipedHead;

            case 2:
                return modelBiped.bipedLeftArm;

            case 3:
                return modelBiped.bipedRightArm;

            case 4:
                return modelBiped.bipedLeftLeg;

            case 5:
                return modelBiped.bipedRightLeg;

            default:
                return null;
        }
    }

    public BufferedImage getTextureImage() {
        return this.textureImage;
    }

    public void setTextureImage(BufferedImage textureImage) {
        this.textureImage = textureImage;
    }

    public DynamicTexture getTexture() {
        return this.texture;
    }

    public ResourceLocation getTextureLocation() {
        return this.textureLocation;
    }

    public void setTextureLocation(ResourceLocation textureLocation) {
        this.textureLocation = textureLocation;
    }

    public boolean isUsePlayerTexture() {
        return this.usePlayerTexture;
    }
}
