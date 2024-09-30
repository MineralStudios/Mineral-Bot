package net.minecraft.client.renderer.texture;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.IntBuffer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResourceManager;

public class DynamicTexture extends AbstractTexture {
    private final int[] dynamicTextureData;

    /** width of this icon in pixels */
    private final int width;

    /** height of this icon in pixels */
    private final int height;

    private final Minecraft mc;
    private final IntBuffer dataBuffer;

    public DynamicTexture(Minecraft mc, IntBuffer dataBuffer, BufferedImage p_i1270_1_) {
        this(mc, dataBuffer, p_i1270_1_.getWidth(), p_i1270_1_.getHeight());
        p_i1270_1_.getRGB(0, 0, p_i1270_1_.getWidth(), p_i1270_1_.getHeight(), this.dynamicTextureData, 0,
                p_i1270_1_.getWidth());
        this.updateDynamicTexture();
    }

    public DynamicTexture(Minecraft mc, IntBuffer dataBuffer, int p_i1271_1_, int p_i1271_2_) {
        this.width = p_i1271_1_;
        this.height = p_i1271_2_;
        this.mc = mc;
        this.dataBuffer = dataBuffer;
        this.dynamicTextureData = new int[p_i1271_1_ * p_i1271_2_];
        TextureUtil.allocateTexture(this.getGlTextureId(), p_i1271_1_, p_i1271_2_);
    }

    public void loadTexture(IResourceManager p_110551_1_) throws IOException {
    }

    public void updateDynamicTexture() {
        TextureUtil.uploadTexture(mc, dataBuffer, this.getGlTextureId(), this.dynamicTextureData, this.width,
                this.height);
    }

    public int[] getTextureData() {
        return this.dynamicTextureData;
    }
}
