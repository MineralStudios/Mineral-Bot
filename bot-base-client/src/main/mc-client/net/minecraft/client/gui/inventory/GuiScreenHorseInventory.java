package net.minecraft.client.gui.inventory;

import gg.mineral.bot.lwjgl.opengl.GL11;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.inventory.ContainerHorseInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;

public class GuiScreenHorseInventory extends GuiContainer {
    private static final ResourceLocation field_147031_u = new ResourceLocation("textures/gui/container/horse.png");
    private final IInventory field_147030_v;
    private final IInventory field_147029_w;
    private final EntityHorse field_147034_x;
    private float field_147033_y;
    private float field_147032_z;

    public GuiScreenHorseInventory(Minecraft mc, IInventory p_i1093_1_, IInventory p_i1093_2_, EntityHorse p_i1093_3_) {
        super(mc, new ContainerHorseInventory(p_i1093_1_, p_i1093_2_, p_i1093_3_));
        this.field_147030_v = p_i1093_1_;
        this.field_147029_w = p_i1093_2_;
        this.field_147034_x = p_i1093_3_;
        this.field_146291_p = false;
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

    protected void func_146979_b(int p_146979_1_, int p_146979_2_) {
        if (this.fontRendererObj != null)
            this.fontRendererObj
                    .drawString(this.field_147029_w.isInventoryNameLocalized() ? this.field_147029_w.getInventoryName()
                            : I18n.format(this.field_147029_w.getInventoryName()), 8, 6, 4210752);
        if (this.fontRendererObj != null)
            this.fontRendererObj.drawString(
                    this.field_147030_v.isInventoryNameLocalized() ? this.field_147030_v.getInventoryName()
                            : I18n.format(this.field_147030_v.getInventoryName()),
                    8, this.field_147000_g - 96 + 2, 4210752);
    }

    protected void func_146976_a(float p_146976_1_, int p_146976_2_, int p_146976_3_) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        TextureManager textureManager = this.mc.getTextureManager();

        if (textureManager != null)
            textureManager.bindTexture(field_147031_u);

        int var4 = (this.width - this.field_146999_f) / 2;
        int var5 = (this.height - this.field_147000_g) / 2;
        this.drawTexturedModalRect(var4, var5, 0, 0, this.field_146999_f, this.field_147000_g);

        if (this.field_147034_x.isChested()) {
            this.drawTexturedModalRect(var4 + 79, var5 + 17, 0, this.field_147000_g, 90, 54);
        }

        if (this.field_147034_x.func_110259_cr()) {
            this.drawTexturedModalRect(var4 + 7, var5 + 35, 0, this.field_147000_g + 54, 18, 18);
        }

        GuiInventory.func_147046_a(this.mc, this.mc.renderManager, var4 + 51, var5 + 60, 17,
                (float) (var4 + 51) - this.field_147033_y,
                (float) (var5 + 75 - 50) - this.field_147032_z, this.field_147034_x);
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(int p_73863_1_, int p_73863_2_, float p_73863_3_) {
        this.field_147033_y = (float) p_73863_1_;
        this.field_147032_z = (float) p_73863_2_;
        super.drawScreen(p_73863_1_, p_73863_2_, p_73863_3_);
    }
}
