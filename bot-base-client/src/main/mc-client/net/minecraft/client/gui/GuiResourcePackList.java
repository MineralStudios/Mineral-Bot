package net.minecraft.client.gui;

import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.ResourcePackListEntry;
import net.minecraft.util.EnumChatFormatting;

public abstract class GuiResourcePackList extends GuiListExtended {
    protected final Minecraft field_148205_k;
    protected final List field_148204_l;

    public GuiResourcePackList(Minecraft mc, int p_i45055_2_, int p_i45055_3_, List p_i45055_4_) {
        super(mc, p_i45055_2_, p_i45055_3_, 32, p_i45055_3_ - 55 + 4, 36);
        this.field_148205_k = mc;
        this.field_148204_l = p_i45055_4_;
        this.field_148163_i = false;
        FontRenderer fontRenderer = mc.fontRenderer;
        this.func_148133_a(true, (int) ((float) (fontRenderer != null ? fontRenderer.FONT_HEIGHT : 9) * 1.5F));
    }

    protected void func_148129_a(int p_148129_1_, int p_148129_2_, Tessellator p_148129_3_) {
        String var4 = EnumChatFormatting.UNDERLINE + "" + EnumChatFormatting.BOLD + this.func_148202_k();
        FontRenderer fontRenderer = this.field_148205_k.fontRenderer;

        if (fontRenderer != null)
            fontRenderer.drawString(var4,
                    p_148129_1_ + this.field_148155_a / 2 - fontRenderer.getStringWidth(var4) / 2,
                    Math.min(this.field_148153_b + 3, p_148129_2_), 16777215);
    }

    protected abstract String func_148202_k();

    public List func_148201_l() {
        return this.field_148204_l;
    }

    protected int getSize() {
        return this.func_148201_l().size();
    }

    public ResourcePackListEntry func_148180_b(int p_148180_1_) {
        return (ResourcePackListEntry) this.func_148201_l().get(p_148180_1_);
    }

    public int func_148139_c() {
        return this.field_148155_a;
    }

    protected int func_148137_d() {
        return this.field_148151_d - 6;
    }
}
