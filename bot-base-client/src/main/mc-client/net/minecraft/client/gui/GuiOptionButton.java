package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;

public class GuiOptionButton extends GuiButton {
    private final GameSettings.Options field_146137_o;

    public GuiOptionButton(Minecraft mc, int p_i45011_1_, int p_i45011_2_, int p_i45011_3_, String p_i45011_4_) {
        this(mc, p_i45011_1_, p_i45011_2_, p_i45011_3_, (GameSettings.Options) null, p_i45011_4_);
    }

    public GuiOptionButton(Minecraft mc, int p_i45012_1_, int p_i45012_2_, int p_i45012_3_, int p_i45012_4_,
            int p_i45012_5_, String p_i45012_6_) {
        super(mc, p_i45012_1_, p_i45012_2_, p_i45012_3_, p_i45012_4_, p_i45012_5_, p_i45012_6_);
        this.field_146137_o = null;
    }

    public GuiOptionButton(Minecraft mc, int p_i45013_1_, int p_i45013_2_, int p_i45013_3_,
            GameSettings.Options p_i45013_4_,
            String p_i45013_5_) {
        super(mc, p_i45013_1_, p_i45013_2_, p_i45013_3_, 150, 20, p_i45013_5_);
        this.field_146137_o = p_i45013_4_;
    }

    public GameSettings.Options func_146136_c() {
        return this.field_146137_o;
    }
}
