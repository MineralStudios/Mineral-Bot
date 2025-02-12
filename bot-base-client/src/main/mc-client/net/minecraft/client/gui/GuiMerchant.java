package net.minecraft.client.gui;

import gg.mineral.bot.base.lwjgl.opengl.GL11;
import gg.mineral.bot.base.lwjgl.opengl.GL12;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ContainerMerchant;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.util.ResourceLocation;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GuiMerchant extends GuiContainer {
    private static final Logger logger = LogManager.getLogger(GuiMerchant.class);
    private static final ResourceLocation field_147038_v = new ResourceLocation("textures/gui/container/villager.png");
    private IMerchant field_147037_w;
    private GuiMerchant.MerchantButton field_147043_x;
    private GuiMerchant.MerchantButton field_147042_y;
    private int field_147041_z;
    private String field_147040_A;

    public GuiMerchant(Minecraft mc, InventoryPlayer p_i46380_1_, IMerchant p_i46380_2_, World p_i46380_3_,
                       String p_i46380_4_) {
        super(mc, new ContainerMerchant(p_i46380_1_, p_i46380_2_, p_i46380_3_));
        this.field_147037_w = p_i46380_2_;
        this.field_147040_A = p_i46380_4_ != null && p_i46380_4_.length() >= 1 ? p_i46380_4_
                : I18n.format("entity.Villager.name", new Object[0]);
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
     * Adds the buttons (and other controls) to the screen in question.
     */
    public void initGui() {
        super.initGui();
        int var1 = (this.width - this.field_146999_f) / 2;
        int var2 = (this.height - this.field_147000_g) / 2;
        this.buttonList
                .add(this.field_147043_x = new GuiMerchant.MerchantButton(this.mc, 1, var1 + 120 + 27, var2 + 24 - 1,
                        true));
        this.buttonList
                .add(this.field_147042_y = new GuiMerchant.MerchantButton(this.mc, 2, var1 + 36 - 19, var2 + 24 - 1,
                        false));
        this.field_147043_x.enabled = false;
        this.field_147042_y.enabled = false;
    }

    protected void func_146979_b(int p_146979_1_, int p_146979_2_) {
        this.fontRendererObj.drawString(this.field_147040_A,
                this.field_146999_f / 2 - this.fontRendererObj.getStringWidth(this.field_147040_A) / 2, 6, 4210752);
        this.fontRendererObj.drawString(I18n.format("container.inventory", new Object[0]), 8,
                this.field_147000_g - 96 + 2, 4210752);
    }

    /**
     * Called from the main game loop to update the screen.
     */
    public void updateScreen() {
        super.updateScreen();
        MerchantRecipeList var1 = this.field_147037_w.getRecipes(this.mc.thePlayer);

        if (var1 != null) {
            this.field_147043_x.enabled = this.field_147041_z < var1.size() - 1;
            this.field_147042_y.enabled = this.field_147041_z > 0;
        }
    }

    protected void actionPerformed(GuiButton p_146284_1_) {
        boolean var2 = false;

        if (p_146284_1_ == this.field_147043_x) {
            ++this.field_147041_z;
            var2 = true;
        } else if (p_146284_1_ == this.field_147042_y) {
            --this.field_147041_z;
            var2 = true;
        }

        if (var2) {
            ((ContainerMerchant) this.container).setCurrentRecipeIndex(this.field_147041_z);
            ByteBuf var3 = Unpooled.buffer();

            try {
                var3.writeInt(this.field_147041_z);
                this.mc.getNetHandler().addToSendQueue(new C17PacketCustomPayload("MC|TrSel", var3));
            } catch (Exception var8) {
                logger.error("Couldn\'t send trade info", var8);
            } finally {
                var3.release();
            }
        }
    }

    protected void func_146976_a(float p_146976_1_, int p_146976_2_, int p_146976_3_) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        TextureManager textureManager = this.mc.getTextureManager();

        if (textureManager != null)
            textureManager.bindTexture(field_147038_v);
        int var4 = (this.width - this.field_146999_f) / 2;
        int var5 = (this.height - this.field_147000_g) / 2;
        this.drawTexturedModalRect(var4, var5, 0, 0, this.field_146999_f, this.field_147000_g);
        MerchantRecipeList var6 = this.field_147037_w.getRecipes(this.mc.thePlayer);

        if (var6 != null && !var6.isEmpty()) {
            int var7 = this.field_147041_z;
            MerchantRecipe var8 = (MerchantRecipe) var6.get(var7);

            if (var8.isRecipeDisabled()) {
                if (textureManager != null)
                    textureManager.bindTexture(field_147038_v);
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                GL11.glDisable(GL11.GL_LIGHTING);
                this.drawTexturedModalRect(this.xShift + 83, this.yShift + 21, 212, 0, 28, 21);
                this.drawTexturedModalRect(this.xShift + 83, this.yShift + 51, 212, 0, 28, 21);
            }
        }
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(int p_73863_1_, int p_73863_2_, float p_73863_3_) {
        super.drawScreen(p_73863_1_, p_73863_2_, p_73863_3_);
        MerchantRecipeList var4 = this.field_147037_w.getRecipes(this.mc.thePlayer);

        if (var4 != null && !var4.isEmpty()) {
            int var5 = (this.width - this.field_146999_f) / 2;
            int var6 = (this.height - this.field_147000_g) / 2;
            int var7 = this.field_147041_z;
            MerchantRecipe var8 = (MerchantRecipe) var4.get(var7);
            GL11.glPushMatrix();
            ItemStack var9 = var8.getItemToBuy();
            ItemStack var10 = var8.getSecondItemToBuy();
            ItemStack var11 = var8.getItemToSell();
            RenderHelper.enableGUIStandardItemLighting();
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
            GL11.glEnable(GL11.GL_COLOR_MATERIAL);
            GL11.glEnable(GL11.GL_LIGHTING);
            itemRender.zLevel = 100.0F;
            itemRender.renderItemAndEffectIntoGUI(this.fontRendererObj, this.mc.getTextureManager(), var9, var5 + 36,
                    var6 + 24);
            itemRender.renderItemOverlayIntoGUI(this.fontRendererObj, this.mc.getTextureManager(), var9, var5 + 36,
                    var6 + 24);

            if (var10 != null) {
                itemRender.renderItemAndEffectIntoGUI(this.fontRendererObj, this.mc.getTextureManager(), var10,
                        var5 + 62, var6 + 24);
                itemRender.renderItemOverlayIntoGUI(this.fontRendererObj, this.mc.getTextureManager(), var10, var5 + 62,
                        var6 + 24);
            }

            itemRender.renderItemAndEffectIntoGUI(this.fontRendererObj, this.mc.getTextureManager(), var11, var5 + 120,
                    var6 + 24);
            itemRender.renderItemOverlayIntoGUI(this.fontRendererObj, this.mc.getTextureManager(), var11, var5 + 120,
                    var6 + 24);
            itemRender.zLevel = 0.0F;
            GL11.glDisable(GL11.GL_LIGHTING);

            if (this.isMouseOver(36, 24, 16, 16, p_73863_1_, p_73863_2_)) {
                this.func_146285_a(var9, p_73863_1_, p_73863_2_);
            } else if (var10 != null && this.isMouseOver(62, 24, 16, 16, p_73863_1_, p_73863_2_)) {
                this.func_146285_a(var10, p_73863_1_, p_73863_2_);
            } else if (this.isMouseOver(120, 24, 16, 16, p_73863_1_, p_73863_2_)) {
                this.func_146285_a(var11, p_73863_1_, p_73863_2_);
            }

            GL11.glPopMatrix();
            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            RenderHelper.enableStandardItemLighting();
        }
    }

    public IMerchant func_147035_g() {
        return this.field_147037_w;
    }

    static class MerchantButton extends GuiButton {
        private final boolean field_146157_o;

        public MerchantButton(Minecraft mc, int p_i1095_1_, int p_i1095_2_, int p_i1095_3_, boolean p_i1095_4_) {
            super(mc, p_i1095_1_, p_i1095_2_, p_i1095_3_, 12, 19, "");
            this.field_146157_o = p_i1095_4_;
        }

        public void drawButton(Minecraft p_146112_1_, int p_146112_2_, int p_146112_3_) {
            if (this.field_146125_m) {
                TextureManager textureManager = p_146112_1_.getTextureManager();
                if (textureManager != null)
                    textureManager.bindTexture(GuiMerchant.field_147038_v);
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                boolean var4 = p_146112_2_ >= this.field_146128_h && p_146112_3_ >= this.field_146129_i
                        && p_146112_2_ < this.field_146128_h + this.field_146120_f
                        && p_146112_3_ < this.field_146129_i + this.field_146121_g;
                int var5 = 0;
                int var6 = 176;

                if (!this.enabled) {
                    var6 += this.field_146120_f * 2;
                } else if (var4) {
                    var6 += this.field_146120_f;
                }

                if (!this.field_146157_o) {
                    var5 += this.field_146121_g;
                }

                this.drawTexturedModalRect(this.field_146128_h, this.field_146129_i, var6, var5, this.field_146120_f,
                        this.field_146121_g);
            }
        }
    }
}
