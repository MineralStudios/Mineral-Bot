package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.WorldInfo;

public class GuiRenameWorld extends GuiScreen {
    private GuiScreen field_146585_a;
    private GuiTextField field_146583_f;
    private final String field_146584_g;

    public GuiRenameWorld(Minecraft mc, GuiScreen p_i46317_1_, String p_i46317_2_) {
        super(mc);
        this.field_146585_a = p_i46317_1_;
        this.field_146584_g = p_i46317_2_;
    }

    /**
     * Called from the main game loop to update the screen.
     */
    public void updateScreen() {
        this.field_146583_f.updateCursorCounter();
    }

    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    public void initGui() {
        this.mc.getKeyboard().enableRepeatEvents(true);
        this.buttonList.clear();
        this.buttonList.add(new GuiButton(this.mc, 0, this.width / 2 - 100, this.height / 4 + 96 + 12,
                I18n.format("selectWorld.renameButton", new Object[0])));
        this.buttonList.add(new GuiButton(this.mc, 1, this.width / 2 - 100, this.height / 4 + 120 + 12,
                I18n.format("gui.cancel", new Object[0])));
        ISaveFormat var1 = this.mc.getSaveLoader();
        WorldInfo var2 = var1.getWorldInfo(this.field_146584_g);
        String var3 = var2.getWorldName();
        this.field_146583_f = new GuiTextField(this.mc, this.fontRendererObj, this.width / 2 - 100, 60, 200, 20);
        this.field_146583_f.setFocused(true);
        this.field_146583_f.setText(var3);
    }

    /**
     * "Called when the screen is unloaded. Used to disable keyboard repeat events."
     */
    public void onGuiClosed() {
        this.mc.getKeyboard().enableRepeatEvents(false);
    }

    protected void actionPerformed(GuiButton p_146284_1_) {
        if (p_146284_1_.enabled) {
            if (p_146284_1_.id == 1) {
                this.mc.displayGuiScreen(this.field_146585_a);
            } else if (p_146284_1_.id == 0) {
                ISaveFormat var2 = this.mc.getSaveLoader();
                var2.renameWorld(this.field_146584_g, this.field_146583_f.getText().trim());
                this.mc.displayGuiScreen(this.field_146585_a);
            }
        }
    }

    /**
     * Fired when a key is typed. This is the equivalent of
     * KeyListener.keyTyped(KeyEvent e).
     */
    protected void keyTyped(char p_73869_1_, int p_73869_2_) {
        this.field_146583_f.textboxKeyTyped(p_73869_1_, p_73869_2_);
        ((GuiButton) this.buttonList.get(0)).enabled = this.field_146583_f.getText().trim().length() > 0;

        if (p_73869_2_ == 28 || p_73869_2_ == 156) {
            this.actionPerformed((GuiButton) this.buttonList.get(0));
        }
    }

    /**
     * Called when the mouse is clicked.
     */
    protected void mouseClicked(int p_73864_1_, int p_73864_2_, int p_73864_3_) {
        super.mouseClicked(p_73864_1_, p_73864_2_, p_73864_3_);
        this.field_146583_f.mouseClicked(p_73864_1_, p_73864_2_, p_73864_3_);
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(int p_73863_1_, int p_73863_2_, float p_73863_3_) {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRendererObj, I18n.format("selectWorld.renameTitle", new Object[0]),
                this.width / 2, 20, 16777215);
        this.drawString(this.fontRendererObj, I18n.format("selectWorld.enterName", new Object[0]), this.width / 2 - 100,
                47, 10526880);
        this.field_146583_f.drawTextBox();
        super.drawScreen(p_73863_1_, p_73863_2_, p_73863_3_);
    }
}
