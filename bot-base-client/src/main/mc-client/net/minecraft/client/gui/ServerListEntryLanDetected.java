package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.network.LanServerDetector;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.I18n;

public class ServerListEntryLanDetected implements GuiListExtended.IGuiListEntry {
    private final GuiMultiplayer field_148292_c;
    protected final Minecraft mc;
    protected final LanServerDetector.LanServer field_148291_b;
    private long field_148290_d = 0L;

    protected ServerListEntryLanDetected(Minecraft mc, GuiMultiplayer p_i45046_1_,
            LanServerDetector.LanServer p_i45046_2_) {
        this.field_148292_c = p_i45046_1_;
        this.field_148291_b = p_i45046_2_;
        this.mc = mc;
    }

    public void func_148279_a(int p_148279_1_, int p_148279_2_, int p_148279_3_, int p_148279_4_, int p_148279_5_,
            Tessellator p_148279_6_, int p_148279_7_, int p_148279_8_, boolean p_148279_9_) {

        FontRenderer fontRenderer = this.mc.fontRenderer;

        if (fontRenderer == null)
            return;
        fontRenderer.drawString(I18n.format("lanServer.title", new Object[0]), p_148279_2_ + 32 + 3,
                p_148279_3_ + 1, 16777215);
        fontRenderer.drawString(this.field_148291_b.getServerMotd(), p_148279_2_ + 32 + 3, p_148279_3_ + 12,
                8421504);

        if (this.mc.gameSettings.hideServerAddress) {
            fontRenderer.drawString(I18n.format("selectServer.hiddenAddress", new Object[0]),
                    p_148279_2_ + 32 + 3, p_148279_3_ + 12 + 11, 3158064);
        } else {
            fontRenderer.drawString(this.field_148291_b.getServerIpPort(), p_148279_2_ + 32 + 3,
                    p_148279_3_ + 12 + 11, 3158064);
        }
    }

    public boolean func_148278_a(int p_148278_1_, int p_148278_2_, int p_148278_3_, int p_148278_4_, int p_148278_5_,
            int p_148278_6_) {
        this.field_148292_c.func_146790_a(p_148278_1_);

        if (Minecraft.getSystemTime() - this.field_148290_d < 250L) {
            this.field_148292_c.func_146796_h();
        }

        this.field_148290_d = Minecraft.getSystemTime();
        return false;
    }

    public void func_148277_b(int p_148277_1_, int p_148277_2_, int p_148277_3_, int p_148277_4_, int p_148277_5_,
            int p_148277_6_) {
    }

    public LanServerDetector.LanServer func_148289_a() {
        return this.field_148291_b;
    }
}
