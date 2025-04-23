package net.minecraft.client.gui;

import java.util.Iterator;
import java.util.Map;

import javax.annotation.Nullable;

import com.google.common.collect.Maps;

import gg.mineral.bot.lwjgl.opengl.GL11;
import net.minecraft.block.material.MapColor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.MapData;

public class MapItemRenderer {
    private static final ResourceLocation field_148253_a = new ResourceLocation("textures/map/map_icons.png");
    private final TextureManager field_148251_b;
    private final Map field_148252_c = Maps.newHashMap();

    private final Minecraft mc;

    public MapItemRenderer(Minecraft mc, TextureManager p_i45009_1_) {
        this.field_148251_b = p_i45009_1_;
        this.mc = mc;
    }

    public void func_148246_a(MapData p_148246_1_) {
        this.func_148248_b(p_148246_1_).func_148236_a();
    }

    public void func_148250_a(MapData p_148250_1_, boolean p_148250_2_) {
        this.func_148248_b(p_148250_1_).func_148237_a(p_148250_2_);
    }

    private MapItemRenderer.Instance func_148248_b(MapData p_148248_1_) {
        MapItemRenderer.Instance var2 = (MapItemRenderer.Instance) this.field_148252_c.get(p_148248_1_.mapName);

        if (var2 == null) {
            var2 = new MapItemRenderer.Instance(mc, p_148248_1_, null);
            this.field_148252_c.put(p_148248_1_.mapName, var2);
        }

        return var2;
    }

    public void func_148249_a() {
        Iterator var1 = this.field_148252_c.values().iterator();

        while (var1.hasNext()) {
            MapItemRenderer.Instance var2 = (MapItemRenderer.Instance) var1.next();
            this.field_148251_b.func_147645_c(var2.field_148240_d);
        }

        this.field_148252_c.clear();
    }

    class Instance {
        private final MapData field_148242_b;
        @Nullable
        private final DynamicTexture field_148243_c;
        private final ResourceLocation field_148240_d;
        @Nullable
        private final int[] field_148241_e;
        private final Minecraft mc;

        private Instance(Minecraft mc, MapData p_i45007_2_) {
            this.mc = mc;
            this.field_148242_b = p_i45007_2_;
            TextureUtil textureUtil = mc.textureUtil;

            this.field_148243_c = textureUtil != null ? new DynamicTexture(mc, textureUtil.dataBuffer, 128, 128) : null;
            this.field_148241_e = this.field_148243_c != null ? this.field_148243_c.getTextureData() : null;
            this.field_148240_d = MapItemRenderer.this.field_148251_b
                    .getDynamicTextureLocation("map/" + p_i45007_2_.mapName, this.field_148243_c);

            int[] intArr = this.field_148241_e;
            if (intArr != null)
                for (int var3 = 0; var3 < intArr.length; ++var3)
                    intArr[var3] = 0;

        }

        private void func_148236_a() {
            for (int var1 = 0; var1 < 16384; ++var1) {
                int var2 = this.field_148242_b.colors[var1] & 255;

                int[] intArr = this.field_148241_e;

                if (intArr == null)
                    continue;

                if (var2 / 4 == 0) {
                    intArr[var1] = (var1 + var1 / 128 & 1) * 8 + 16 << 24;
                } else {
                    intArr[var1] = MapColor.mapColorArray[var2 / 4].func_151643_b(var2 & 3);
                }
            }

            if (this.field_148243_c != null)
                this.field_148243_c.updateDynamicTexture();
        }

        private void func_148237_a(boolean p_148237_1_) {
            byte var2 = 0;
            byte var3 = 0;
            Tessellator var4 = this.mc.getTessellator();
            float var5 = 0.0F;
            MapItemRenderer.this.field_148251_b.bindTexture(this.field_148240_d);
            GL11.glEnable(GL11.GL_BLEND);
            OpenGlHelper.glBlendFunc(1, 771, 0, 1);
            GL11.glDisable(GL11.GL_ALPHA_TEST);
            if (var4 != null) {
                var4.startDrawingQuads();
                var4.addVertexWithUV((double) ((float) (var2 + 0) + var5), (double) ((float) (var3 + 128) - var5),
                        -0.009999999776482582D, 0.0D, 1.0D);
                var4.addVertexWithUV((double) ((float) (var2 + 128) - var5), (double) ((float) (var3 + 128) - var5),
                        -0.009999999776482582D, 1.0D, 1.0D);
                var4.addVertexWithUV((double) ((float) (var2 + 128) - var5), (double) ((float) (var3 + 0) + var5),
                        -0.009999999776482582D, 1.0D, 0.0D);
                var4.addVertexWithUV((double) ((float) (var2 + 0) + var5), (double) ((float) (var3 + 0) + var5),
                        -0.009999999776482582D, 0.0D, 0.0D);
                var4.draw();
            }
            GL11.glEnable(GL11.GL_ALPHA_TEST);
            GL11.glDisable(GL11.GL_BLEND);
            MapItemRenderer.this.field_148251_b.bindTexture(MapItemRenderer.field_148253_a);
            int var6 = 0;
            Iterator var7 = this.field_148242_b.playersVisibleOnMap.values().iterator();

            while (var7.hasNext()) {
                MapData.MapCoord var8 = (MapData.MapCoord) var7.next();

                if (!p_148237_1_ || var8.iconSize == 1) {
                    GL11.glPushMatrix();
                    GL11.glTranslatef((float) var2 + (float) var8.centerX / 2.0F + 64.0F,
                            (float) var3 + (float) var8.centerZ / 2.0F + 64.0F, -0.02F);
                    GL11.glRotatef((float) (var8.iconRotation * 360) / 16.0F, 0.0F, 0.0F, 1.0F);
                    GL11.glScalef(4.0F, 4.0F, 3.0F);
                    GL11.glTranslatef(-0.125F, 0.125F, 0.0F);
                    float var9 = (float) (var8.iconSize % 4 + 0) / 4.0F;
                    float var10 = (float) (var8.iconSize / 4 + 0) / 4.0F;
                    float var11 = (float) (var8.iconSize % 4 + 1) / 4.0F;
                    float var12 = (float) (var8.iconSize / 4 + 1) / 4.0F;
                    if (var4 != null) {
                        var4.startDrawingQuads();
                        var4.addVertexWithUV(-1.0D, 1.0D, (double) ((float) var6 * 0.001F), (double) var9,
                                (double) var10);
                        var4.addVertexWithUV(1.0D, 1.0D, (double) ((float) var6 * 0.001F), (double) var11,
                                (double) var10);
                        var4.addVertexWithUV(1.0D, -1.0D, (double) ((float) var6 * 0.001F), (double) var11,
                                (double) var12);
                        var4.addVertexWithUV(-1.0D, -1.0D, (double) ((float) var6 * 0.001F), (double) var9,
                                (double) var12);
                        var4.draw();
                    }
                    GL11.glPopMatrix();
                    ++var6;
                }
            }

            GL11.glPushMatrix();
            GL11.glTranslatef(0.0F, 0.0F, -0.04F);
            GL11.glScalef(1.0F, 1.0F, 1.0F);
            GL11.glPopMatrix();
        }

        Instance(Minecraft mc, MapData p_i45008_2_, Object p_i45008_3_) {
            this(mc, p_i45008_2_);
        }
    }
}
