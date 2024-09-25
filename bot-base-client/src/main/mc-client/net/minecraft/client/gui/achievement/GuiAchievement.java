package net.minecraft.client.gui.achievement;

import gg.mineral.bot.base.lwjgl.opengl.GL11;
import gg.mineral.bot.base.lwjgl.opengl.GL12;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.stats.Achievement;
import net.minecraft.util.ResourceLocation;

public class GuiAchievement extends Gui {
    private static final ResourceLocation field_146261_a = new ResourceLocation(
            "textures/gui/achievement/achievement_background.png");
    private int field_146260_g;
    private int field_146267_h;
    private String field_146268_i;
    private String field_146265_j;
    private Achievement field_146266_k;
    private long field_146263_l;
    private RenderItem field_146264_m;
    private boolean field_146262_n;
    private static final String __OBFID = "CL_00000721";

    public GuiAchievement(Minecraft mc) {
        super(mc);
        this.field_146264_m = new RenderItem(mc);
    }

    public void func_146256_a(Achievement p_146256_1_) {
        this.field_146268_i = I18n.format("achievement.get", new Object[0]);
        this.field_146265_j = p_146256_1_.func_150951_e().getUnformattedText();
        this.field_146263_l = Minecraft.getSystemTime();
        this.field_146266_k = p_146256_1_;
        this.field_146262_n = false;
    }

    public void func_146255_b(Achievement p_146255_1_) {
        this.field_146268_i = p_146255_1_.func_150951_e().getUnformattedText();
        this.field_146265_j = p_146255_1_.getDescription();
        this.field_146263_l = Minecraft.getSystemTime() + 2500L;
        this.field_146266_k = p_146255_1_;
        this.field_146262_n = true;
    }

    private void func_146258_c() {
        GL11.glViewport(0, 0, this.mc.displayWidth, this.mc.displayHeight);
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glLoadIdentity();
        this.field_146260_g = this.mc.displayWidth;
        this.field_146267_h = this.mc.displayHeight;
        ScaledResolution var1 = new ScaledResolution(this.mc, this.mc.displayWidth, this.mc.displayHeight);
        this.field_146260_g = var1.getScaledWidth();
        this.field_146267_h = var1.getScaledHeight();
        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        GL11.glOrtho(0.0D, (double) this.field_146260_g, (double) this.field_146267_h, 0.0D, 1000.0D, 3000.0D);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glLoadIdentity();
        GL11.glTranslatef(0.0F, 0.0F, -2000.0F);
    }

    public void func_146254_a() {
        if (this.field_146266_k != null && this.field_146263_l != 0L && this.mc.thePlayer != null) {
            double var1 = (double) (Minecraft.getSystemTime() - this.field_146263_l) / 3000.0D;

            if (!this.field_146262_n) {
                if (var1 < 0.0D || var1 > 1.0D) {
                    this.field_146263_l = 0L;
                    return;
                }
            } else if (var1 > 0.5D) {
                var1 = 0.5D;
            }

            this.func_146258_c();
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            GL11.glDepthMask(false);
            double var3 = var1 * 2.0D;

            if (var3 > 1.0D) {
                var3 = 2.0D - var3;
            }

            var3 *= 4.0D;
            var3 = 1.0D - var3;

            if (var3 < 0.0D) {
                var3 = 0.0D;
            }

            var3 *= var3;
            var3 *= var3;
            int var5 = this.field_146260_g - 160;
            int var6 = 0 - (int) (var3 * 36.0D);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            TextureManager textureManager = this.mc.getTextureManager();
            if (textureManager != null)
                textureManager.bindTexture(field_146261_a);
            GL11.glDisable(GL11.GL_LIGHTING);
            this.drawTexturedModalRect(var5, var6, 96, 202, 160, 32);

            FontRenderer fontRenderer = this.mc.fontRenderer;

            if (fontRenderer != null) {
                if (this.field_146262_n) {
                    fontRenderer.drawSplitString(this.field_146265_j, var5 + 30, var6 + 7, 120, -1);
                } else {
                    fontRenderer.drawString(this.field_146268_i, var5 + 30, var6 + 7, -256);
                    fontRenderer.drawString(this.field_146265_j, var5 + 30, var6 + 18, -1);
                }
            }

            RenderHelper.enableGUIStandardItemLighting();
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
            GL11.glEnable(GL11.GL_COLOR_MATERIAL);
            GL11.glEnable(GL11.GL_LIGHTING);
            this.field_146264_m.renderItemAndEffectIntoGUI(this.mc.fontRenderer, this.mc.getTextureManager(),
                    this.field_146266_k.theItemStack, var5 + 8, var6 + 8);
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glDepthMask(true);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
        }
    }

    public void func_146257_b() {
        this.field_146266_k = null;
        this.field_146263_l = 0L;
    }
}
