package net.minecraft.client.gui;

import gg.mineral.bot.lwjgl.opengl.GL11;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ContainerHopper;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;

public class GuiHopper extends GuiContainer {
    private static final ResourceLocation field_147085_u = new ResourceLocation("textures/gui/container/hopper.png");
    private IInventory field_147084_v;
    private IInventory field_147083_w;

    public GuiHopper(Minecraft mc, InventoryPlayer p_i1092_1_, IInventory p_i1092_2_) {
        super(mc, new ContainerHopper(p_i1092_1_, p_i1092_2_));
        this.field_147084_v = p_i1092_1_;
        this.field_147083_w = p_i1092_2_;
        this.field_146291_p = false;
        this.field_147000_g = 133;
    }

    protected void func_146979_b(int p_146979_1_, int p_146979_2_) {
        this.fontRendererObj
                .drawString(this.field_147083_w.isInventoryNameLocalized() ? this.field_147083_w.getInventoryName()
                        : I18n.format(this.field_147083_w.getInventoryName(), new Object[0]), 8, 6, 4210752);
        this.fontRendererObj.drawString(
                this.field_147084_v.isInventoryNameLocalized() ? this.field_147084_v.getInventoryName()
                        : I18n.format(this.field_147084_v.getInventoryName(), new Object[0]),
                8, this.field_147000_g - 96 + 2, 4210752);
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

    protected void func_146976_a(float p_146976_1_, int p_146976_2_, int p_146976_3_) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        TextureManager textureManager = this.mc.getTextureManager();

        if (textureManager != null)
            textureManager.bindTexture(field_147085_u);

        int var4 = (this.width - this.field_146999_f) / 2;
        int var5 = (this.height - this.field_147000_g) / 2;
        this.drawTexturedModalRect(var4, var5, 0, 0, this.field_146999_f, this.field_147000_g);
    }
}
