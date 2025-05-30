package net.minecraft.client.resources;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.gui.GuiScreenResourcePacks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import gg.mineral.bot.lwjgl.opengl.GL11;

public abstract class ResourcePackListEntry implements GuiListExtended.IGuiListEntry {
    private static final ResourceLocation field_148316_c = new ResourceLocation("textures/gui/resource_packs.png");
    protected final Minecraft mc;
    protected final GuiScreenResourcePacks field_148315_b;

    public ResourcePackListEntry(Minecraft mc, GuiScreenResourcePacks p_i45051_1_) {
        this.field_148315_b = p_i45051_1_;
        this.mc = mc;
    }

    public void func_148279_a(int p_148279_1_, int p_148279_2_, int p_148279_3_, int p_148279_4_, int p_148279_5_,
                              Tessellator p_148279_6_, int p_148279_7_, int p_148279_8_, boolean p_148279_9_) {
        this.func_148313_c();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        Gui.func_146110_a(mc, p_148279_2_, p_148279_3_, 0.0F, 0.0F, 32, 32, 32.0F, 32.0F);
        int var11;

        if ((this.mc.gameSettings.touchscreen || p_148279_9_) && this.func_148310_d()) {
            TextureManager textureManager = this.mc.getTextureManager();

            if (textureManager != null)
                textureManager.bindTexture(field_148316_c);
            Gui.drawRect(mc, p_148279_2_, p_148279_3_, p_148279_2_ + 32, p_148279_3_ + 32, -1601138544);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            int var10 = p_148279_7_ - p_148279_2_;
            var11 = p_148279_8_ - p_148279_3_;

            if (this.func_148309_e()) {
                if (var10 < 32) {
                    Gui.func_146110_a(mc, p_148279_2_, p_148279_3_, 0.0F, 32.0F, 32, 32, 256.0F, 256.0F);
                } else {
                    Gui.func_146110_a(mc, p_148279_2_, p_148279_3_, 0.0F, 0.0F, 32, 32, 256.0F, 256.0F);
                }
            } else {
                if (this.func_148308_f()) {
                    if (var10 < 16) {
                        Gui.func_146110_a(mc, p_148279_2_, p_148279_3_, 32.0F, 32.0F, 32, 32, 256.0F, 256.0F);
                    } else {
                        Gui.func_146110_a(mc, p_148279_2_, p_148279_3_, 32.0F, 0.0F, 32, 32, 256.0F, 256.0F);
                    }
                }

                if (this.func_148314_g()) {
                    if (var10 < 32 && var10 > 16 && var11 < 16) {
                        Gui.func_146110_a(mc, p_148279_2_, p_148279_3_, 96.0F, 32.0F, 32, 32, 256.0F, 256.0F);
                    } else {
                        Gui.func_146110_a(mc, p_148279_2_, p_148279_3_, 96.0F, 0.0F, 32, 32, 256.0F, 256.0F);
                    }
                }

                if (this.func_148307_h()) {
                    if (var10 < 32 && var10 > 16 && var11 > 16) {
                        Gui.func_146110_a(mc, p_148279_2_, p_148279_3_, 64.0F, 32.0F, 32, 32, 256.0F, 256.0F);
                    } else {
                        Gui.func_146110_a(mc, p_148279_2_, p_148279_3_, 64.0F, 0.0F, 32, 32, 256.0F, 256.0F);
                    }
                }
            }
        }

        String var14 = this.func_148312_b();
        FontRenderer fontRenderer = this.mc.fontRenderer;

        if (fontRenderer == null)
            return;
        var11 = fontRenderer.getStringWidth(var14);

        if (var11 > 157) {
            var14 = fontRenderer.trimStringToWidth(var14,
                    157 - fontRenderer.getStringWidth("...")) + "...";
        }

        fontRenderer.drawStringWithShadow(var14, p_148279_2_ + 32 + 2, p_148279_3_ + 1, 16777215);
        List var12 = fontRenderer.listFormattedStringToWidth(this.func_148311_a(), 157);

        for (int var13 = 0; var13 < 2 && var13 < var12.size(); ++var13) {
            fontRenderer.drawStringWithShadow((String) var12.get(var13), p_148279_2_ + 32 + 2,
                    p_148279_3_ + 12 + 10 * var13, 8421504);
        }
    }

    protected abstract String func_148311_a();

    protected abstract String func_148312_b();

    protected abstract void func_148313_c();

    protected boolean func_148310_d() {
        return true;
    }

    protected boolean func_148309_e() {
        return !this.field_148315_b.func_146961_a(this);
    }

    protected boolean func_148308_f() {
        return this.field_148315_b.func_146961_a(this);
    }

    protected boolean func_148314_g() {
        List var1 = this.field_148315_b.func_146962_b(this);
        int var2 = var1.indexOf(this);
        return var2 > 0 && ((ResourcePackListEntry) var1.get(var2 - 1)).func_148310_d();
    }

    protected boolean func_148307_h() {
        List var1 = this.field_148315_b.func_146962_b(this);
        int var2 = var1.indexOf(this);
        return var2 >= 0 && var2 < var1.size() - 1 && ((ResourcePackListEntry) var1.get(var2 + 1)).func_148310_d();
    }

    public boolean func_148278_a(int p_148278_1_, int p_148278_2_, int p_148278_3_, int p_148278_4_, int p_148278_5_,
                                 int p_148278_6_) {
        if (this.func_148310_d() && p_148278_5_ <= 32) {
            if (this.func_148309_e()) {
                this.field_148315_b.func_146962_b(this).remove(this);
                this.field_148315_b.func_146963_h().add(0, this);
                return true;
            }

            if (p_148278_5_ < 16 && this.func_148308_f()) {
                this.field_148315_b.func_146962_b(this).remove(this);
                this.field_148315_b.func_146964_g().add(0, this);
                return true;
            }

            List var7;
            int var8;

            if (p_148278_5_ > 16 && p_148278_6_ < 16 && this.func_148314_g()) {
                var7 = this.field_148315_b.func_146962_b(this);
                var8 = var7.indexOf(this);
                var7.remove(this);
                var7.add(var8 - 1, this);
                return true;
            }

            if (p_148278_5_ > 16 && p_148278_6_ > 16 && this.func_148307_h()) {
                var7 = this.field_148315_b.func_146962_b(this);
                var8 = var7.indexOf(this);
                var7.remove(this);
                var7.add(var8 + 1, this);
                return true;
            }
        }

        return false;
    }

    public void func_148277_b(int p_148277_1_, int p_148277_2_, int p_148277_3_, int p_148277_4_, int p_148277_5_,
                              int p_148277_6_) {
    }
}
