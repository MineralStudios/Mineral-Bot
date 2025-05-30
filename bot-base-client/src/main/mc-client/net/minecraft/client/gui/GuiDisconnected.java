package net.minecraft.client.gui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.IChatComponent;

public class GuiDisconnected extends GuiScreen {
    private String field_146306_a;
    private IChatComponent field_146304_f;
    private List field_146305_g;
    private final GuiScreen field_146307_h;

    public GuiDisconnected(Minecraft mc, GuiScreen p_i45020_1_, String p_i45020_2_, IChatComponent p_i45020_3_) {
        super(mc);
        this.field_146307_h = p_i45020_1_;
        this.field_146306_a = I18n.format(p_i45020_2_, new Object[0]);
        this.field_146304_f = p_i45020_3_;

        System.out.println("Disconnected: " + this.field_146306_a + " - " + this.field_146304_f.getUnformattedText());
    }

    /**
     * Fired when a key is typed. This is the equivalent of
     * KeyListener.keyTyped(KeyEvent e).
     */
    protected void keyTyped(char p_73869_1_, int p_73869_2_) {
    }

    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    public void initGui() {
        this.buttonList.clear();
        this.buttonList.add(new GuiButton(this.mc, 0, this.width / 2 - 100, this.height / 4 + 120 + 12,
                I18n.format("gui.toMenu", new Object[0])));
        this.field_146305_g =  this.fontRendererObj != null? this.fontRendererObj.listFormattedStringToWidth(this.field_146304_f.getFormattedText(),
                this.width - 50) : new ArrayList<>();
    }

    protected void actionPerformed(GuiButton p_146284_1_) {
        if (p_146284_1_.id == 0) {
            this.mc.displayGuiScreen(this.field_146307_h);
        }
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(int p_73863_1_, int p_73863_2_, float p_73863_3_) {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRendererObj, this.field_146306_a, this.width / 2, this.height / 2 - 50,
                11184810);
        int var4 = this.height / 2 - 30;

        if (this.field_146305_g != null) {
            for (Iterator var5 = this.field_146305_g.iterator(); var5
                    .hasNext(); var4 += this.fontRendererObj.FONT_HEIGHT) {
                String var6 = (String) var5.next();
                this.drawCenteredString(this.fontRendererObj, var6, this.width / 2, var4, 16777215);
            }
        }

        super.drawScreen(p_73863_1_, p_73863_2_, p_73863_3_);
    }
}
