package net.minecraft.client.renderer;

import gg.mineral.bot.api.screen.type.InvEffectRendererScreen;
import gg.mineral.bot.lwjgl.opengl.GL11;
import lombok.val;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.Container;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

import java.util.Iterator;

public abstract class InventoryEffectRenderer extends GuiContainer implements InvEffectRendererScreen {
    private boolean field_147045_u;

    public InventoryEffectRenderer(Minecraft mc, Container p_i1089_1_) {
        super(mc, p_i1089_1_);
    }

    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    public void initGui() {
        super.initGui();

        if (this.mc.thePlayer != null && !this.mc.thePlayer.getActivePotionEffects().isEmpty()) {
            this.xShift = 160 + (this.width - this.field_146999_f - 200) / 2;
            this.field_147045_u = true;
        }
    }

    @Override
    public int getScaleFactor() {
        int scaleFactor = 1;
        // This flag is usually used for checking if fancy graphics are enabled.
        boolean fancyGraphics = mc.func_152349_b();
        // Get the user-specified GUI scale; if it is 0, use a large number (effectively "no limit")
        int guiScale = mc.gameSettings.guiScale;
        if (guiScale == 0) {
            guiScale = 1000;
        }

        // Increase the scale factor as long as it doesn't cause the scaled dimensions
        // to drop below 320x240 and we haven't reached the user limit.
        while (scaleFactor < guiScale
                && mc.displayWidth / (scaleFactor + 1) >= 320
                && mc.displayHeight / (scaleFactor + 1) >= 240) {
            scaleFactor++;
        }

        // In some cases (e.g., fancy graphics enabled), adjust the scale factor so it is even.
        if (fancyGraphics && scaleFactor % 2 != 0 && scaleFactor != 1) {
            scaleFactor--;
        }

        return scaleFactor;
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(int p_73863_1_, int p_73863_2_, float p_73863_3_) {
        super.drawScreen(p_73863_1_, p_73863_2_, p_73863_3_);

        if (this.field_147045_u) {
            this.func_147044_g();
        }
    }

    private void func_147044_g() {
        int var1 = this.xShift - 124;
        int var2 = this.yShift;
        boolean var3 = true;
        val var4 = this.mc.thePlayer != null ? this.mc.thePlayer.getActivePotionEffects() : null;

        if (var4 != null && !var4.isEmpty()) {
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glDisable(GL11.GL_LIGHTING);
            int var5 = 33;

            if (var4.size() > 5)
                var5 = 132 / (var4.size() - 1);

            if (this.mc.thePlayer != null)
                for (Iterator<PotionEffect> var6 = this.mc.thePlayer.getActivePotionEffects().iterator(); var6
                        .hasNext(); var2 += var5) {
                    PotionEffect var7 = var6.next();
                    Potion var8 = Potion.potionTypes[var7.getPotionID()];
                    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                    TextureManager textureManager = this.mc.getTextureManager();

                    if (textureManager != null)
                        textureManager.bindTexture(field_147001_a);

                    this.drawTexturedModalRect(var1, var2, 0, 166, 140, 32);

                    if (var8.hasStatusIcon()) {
                        int var9 = var8.getStatusIconIndex();
                        this.drawTexturedModalRect(var1 + 6, var2 + 7, var9 % 8 * 18, 198 + var9 / 8 * 18, 18, 18);
                    }

                    String var11 = I18n.format(var8.getName());

                    if (var7.getAmplifier() == 1)
                        var11 = var11 + " " + I18n.format("enchantment.level.2");
                    else if (var7.getAmplifier() == 2)
                        var11 = var11 + " " + I18n.format("enchantment.level.3");
                    else if (var7.getAmplifier() == 3)
                        var11 = var11 + " " + I18n.format("enchantment.level.4");

                    if (this.fontRendererObj != null)
                        this.fontRendererObj.drawStringWithShadow(var11, var1 + 10 + 18, var2 + 6, 16777215);
                    String var10 = Potion.getDurationString(var7);
                    if (this.fontRendererObj != null)
                        this.fontRendererObj.drawStringWithShadow(var10, var1 + 10 + 18, var2 + 6 + 10, 8355711);
                }
        }
    }
}
