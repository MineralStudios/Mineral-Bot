package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.WorldSettings;

public class GuiShareToLan extends GuiScreen {
    private final GuiScreen field_146598_a;
    private GuiButton field_146596_f;
    private GuiButton field_146597_g;
    private String field_146599_h = "survival";
    private boolean field_146600_i;

    public GuiShareToLan(Minecraft mc, GuiScreen p_i1055_1_) {
        super(mc);
        this.field_146598_a = p_i1055_1_;
    }

    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    public void initGui() {
        this.buttonList.clear();
        this.buttonList.add(new GuiButton(this.mc, 101, this.width / 2 - 155, this.height - 28, 150, 20,
                I18n.format("lanServer.start", new Object[0])));
        this.buttonList.add(new GuiButton(this.mc, 102, this.width / 2 + 5, this.height - 28, 150, 20,
                I18n.format("gui.cancel", new Object[0])));
        this.buttonList.add(this.field_146597_g = new GuiButton(this.mc, 104, this.width / 2 - 155, 100, 150, 20,
                I18n.format("selectWorld.gameMode", new Object[0])));
        this.buttonList.add(this.field_146596_f = new GuiButton(this.mc, 103, this.width / 2 + 5, 100, 150, 20,
                I18n.format("selectWorld.allowCommands", new Object[0])));
        this.func_146595_g();
    }

    private void func_146595_g() {
        this.field_146597_g.displayString = I18n.format("selectWorld.gameMode", new Object[0]) + " "
                + I18n.format("selectWorld.gameMode." + this.field_146599_h, new Object[0]);
        this.field_146596_f.displayString = I18n.format("selectWorld.allowCommands", new Object[0]) + " ";

        if (this.field_146600_i) {
            this.field_146596_f.displayString = this.field_146596_f.displayString
                    + I18n.format("options.on", new Object[0]);
        } else {
            this.field_146596_f.displayString = this.field_146596_f.displayString
                    + I18n.format("options.off", new Object[0]);
        }
    }

    protected void actionPerformed(GuiButton p_146284_1_) {
        if (p_146284_1_.id == 102) {
            this.mc.displayGuiScreen(this.field_146598_a);
        } else if (p_146284_1_.id == 104) {
            if (this.field_146599_h.equals("survival")) {
                this.field_146599_h = "creative";
            } else if (this.field_146599_h.equals("creative")) {
                this.field_146599_h = "adventure";
            } else {
                this.field_146599_h = "survival";
            }

            this.func_146595_g();
        } else if (p_146284_1_.id == 103) {
            this.field_146600_i = !this.field_146600_i;
            this.func_146595_g();
        } else if (p_146284_1_.id == 101) {
            this.mc.displayGuiScreen((GuiScreen) null);
            String var2 = this.mc.getIntegratedServer()
                    .shareToLAN(WorldSettings.GameType.getByName(this.field_146599_h), this.field_146600_i);
            Object var3;

            if (var2 != null) {
                var3 = new ChatComponentTranslation("commands.publish.started", new Object[] { var2 });
            } else {
                var3 = new ChatComponentText("commands.publish.failed");
            }

            this.mc.ingameGUI.getChatGUI().func_146227_a((IChatComponent) var3);
        }
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(int p_73863_1_, int p_73863_2_, float p_73863_3_) {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRendererObj, I18n.format("lanServer.title", new Object[0]), this.width / 2, 50,
                16777215);
        this.drawCenteredString(this.fontRendererObj, I18n.format("lanServer.otherPlayers", new Object[0]),
                this.width / 2, 82, 16777215);
        super.drawScreen(p_73863_1_, p_73863_2_, p_73863_3_);
    }
}
