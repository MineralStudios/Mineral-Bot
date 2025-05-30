package net.minecraft.client.renderer.texture;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;

public class TextureClock extends TextureAtlasSprite {
    private double field_94239_h;
    private double field_94240_i;

    public TextureClock(Minecraft mc, String p_i1285_1_) {
        super(mc, p_i1285_1_);
    }

    public void updateAnimation() {
        if (!this.framesTextureData.isEmpty()) {
            double var2 = 0.0D;

            WorldClient theWorld = mc.theWorld;

            if (theWorld != null && mc.thePlayer != null) {
                float var4 = theWorld.getCelestialAngle(1.0F);
                var2 = (double) var4;

                if (!theWorld.provider.isSurfaceWorld())
                    var2 = Math.random();
            }

            double var7;

            for (var7 = var2 - this.field_94239_h; var7 < -0.5D; ++var7) {
                ;
            }

            while (var7 >= 0.5D)
                --var7;

            if (var7 < -1.0D)
                var7 = -1.0D;

            if (var7 > 1.0D)
                var7 = 1.0D;

            this.field_94240_i += var7 * 0.1D;
            this.field_94240_i *= 0.8D;
            this.field_94239_h += this.field_94240_i;
            int var6;

            for (var6 = (int) ((this.field_94239_h + 1.0D) * (double) this.framesTextureData.size())
                    % this.framesTextureData.size(); var6 < 0; var6 = (var6 + this.framesTextureData.size())
                            % this.framesTextureData.size())
                ;

            if (var6 != this.frameCounter) {
                this.frameCounter = var6;
                TextureUtil textureUtil = mc.textureUtil;

                if (textureUtil != null)
                    TextureUtil.func_147955_a(this.mc, textureUtil.dataBuffer,
                            (int[][]) this.framesTextureData.get(this.frameCounter), this.width,
                            this.height, this.originX, this.originY, false, false);
            }
        }
    }
}
