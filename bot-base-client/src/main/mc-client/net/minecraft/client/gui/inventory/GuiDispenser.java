package net.minecraft.client.gui.inventory;

import gg.mineral.bot.base.lwjgl.opengl.GL11;
import lombok.val;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ContainerDispenser;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.util.ResourceLocation;

public class GuiDispenser extends GuiContainer {
    private static final ResourceLocation field_147088_v = new ResourceLocation("textures/gui/container/dispenser.png");
    public TileEntityDispenser field_147089_u;

    public GuiDispenser(Minecraft mc, InventoryPlayer p_i46384_1_, TileEntityDispenser p_i46384_2_) {
        super(mc, new ContainerDispenser(p_i46384_1_, p_i46384_2_));
        this.field_147089_u = p_i46384_2_;
    }

    protected void func_146979_b(int p_146979_1_, int p_146979_2_) {
        String var3 = this.field_147089_u.isInventoryNameLocalized() ? this.field_147089_u.getInventoryName()
                : I18n.format(this.field_147089_u.getInventoryName(), new Object[0]);
        val fontRendererObj = this.fontRendererObj;

        if (fontRendererObj != null) {
            fontRendererObj.drawString(var3,
                    this.field_146999_f / 2 - fontRendererObj.getStringWidth(var3) / 2,
                    6, 4210752);
            fontRendererObj.drawString(I18n.format("container.inventory", new Object[0]), 8,
                    this.field_147000_g - 96 + 2, 4210752);
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

    protected void func_146976_a(float p_146976_1_, int p_146976_2_, int p_146976_3_) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        TextureManager textureManager = this.mc.getTextureManager();

        if (textureManager != null)
            textureManager.bindTexture(field_147088_v);

        int var4 = (this.width - this.field_146999_f) / 2;
        int var5 = (this.height - this.field_147000_g) / 2;
        this.drawTexturedModalRect(var4, var5, 0, 0, this.field_146999_f, this.field_147000_g);
    }
}
