package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;

public class GuiErrorScreen extends GuiScreen {
    private String field_146313_a;
    private String field_146312_f;

    public GuiErrorScreen(Minecraft mc, String p_i46319_1_, String p_i46319_2_) {
        super(mc);
        this.field_146313_a = p_i46319_1_;
        this.field_146312_f = p_i46319_2_;
    }

    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    public void initGui() {
        super.initGui();
        this.buttonList
                .add(new GuiButton(this.mc, 0, this.width / 2 - 100, 140, I18n.format("gui.cancel", new Object[0])));
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(int p_73863_1_, int p_73863_2_, float p_73863_3_) {
        this.drawGradientRect(0, 0, this.width, this.height, -12574688, -11530224);
        this.drawCenteredString(this.fontRendererObj, this.field_146313_a, this.width / 2, 90, 16777215);
        this.drawCenteredString(this.fontRendererObj, this.field_146312_f, this.width / 2, 110, 16777215);
        super.drawScreen(p_73863_1_, p_73863_2_, p_73863_3_);
    }

    /**
     * Fired when a key is typed. This is the equivalent of
     * KeyListener.keyTyped(KeyEvent e).
     */
    protected void keyTyped(char p_73869_1_, int p_73869_2_) {
    }

    protected void actionPerformed(GuiButton p_146284_1_) {
        this.mc.displayGuiScreen((GuiScreen) null);
    }
}
