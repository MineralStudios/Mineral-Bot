package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import optifine.Config;
import optifine.GuiAnimationSettingsOF;
import optifine.GuiDetailSettingsOF;
import optifine.GuiOtherSettingsOF;
import optifine.GuiPerformanceSettingsOF;
import optifine.GuiQualitySettingsOF;

public class GuiVideoSettings extends GuiScreen {
    private GuiScreen field_146498_f;
    protected String field_146500_a = "Video Settings";
    private GameSettings field_146499_g;
    private boolean is64bit;
    private static GameSettings.Options[] field_146502_i = new GameSettings.Options[] { GameSettings.Options.GRAPHICS,
            GameSettings.Options.RENDER_DISTANCE, GameSettings.Options.AMBIENT_OCCLUSION,
            GameSettings.Options.FRAMERATE_LIMIT, GameSettings.Options.AO_LEVEL, GameSettings.Options.VIEW_BOBBING,
            GameSettings.Options.GUI_SCALE, GameSettings.Options.ADVANCED_OPENGL, GameSettings.Options.GAMMA,
            GameSettings.Options.CHUNK_LOADING, GameSettings.Options.FOG_FANCY, GameSettings.Options.FOG_START,
            GameSettings.Options.ANAGLYPH };
    private int lastMouseX = 0;
    private int lastMouseY = 0;
    private long mouseStillTime = 0L;

    public GuiVideoSettings(Minecraft mc, GuiScreen par1GuiScreen, GameSettings par2GameSettings) {
        super(mc);
        this.field_146498_f = par1GuiScreen;
        this.field_146499_g = par2GameSettings;
    }

    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    public void initGui() {
        this.field_146500_a = I18n.format("options.videoTitle", new Object[0]);
        this.buttonList.clear();
        this.is64bit = false;
        String[] var1 = new String[] { "sun.arch.data.model", "com.ibm.vm.bitmode", "os.arch" };
        String[] var2 = var1;
        int var3 = var1.length;

        for (int var8 = 0; var8 < var3; ++var8) {
            String var9 = var2[var8];
            String var10 = System.getProperty(var9);

            if (var10 != null && var10.contains("64")) {
                this.is64bit = true;
                break;
            }
        }

        boolean var12 = false;
        boolean var111 = !this.is64bit;
        GameSettings.Options[] var13 = field_146502_i;
        int var14 = var13.length;
        boolean var11 = false;
        int x;
        int var15;

        for (var15 = 0; var15 < var14; ++var15) {
            GameSettings.Options y = var13[var15];

            if (y != null) {
                x = this.width / 2 - 155 + var15 % 2 * 160;
                int y1 = this.height / 6 + 21 * (var15 / 2) - 10;

                if (y.getEnumFloat()) {
                    this.buttonList.add(new GuiOptionSlider(this.mc, y.returnEnumOrdinal(), x, y1, y));
                } else {
                    this.buttonList.add(
                            new GuiOptionButton(this.mc, y.returnEnumOrdinal(), x, y1, y,
                                    this.field_146499_g.getKeyBinding(y)));
                }
            }
        }

        int var16 = this.height / 6 + 21 * (var15 / 2) - 10;
        boolean var17 = false;
        x = this.width / 2 - 155 + 160;
        this.buttonList.add(new GuiOptionButton(this.mc, 202, x, var16, "Quality..."));
        var16 += 21;
        x = this.width / 2 - 155 + 0;
        this.buttonList.add(new GuiOptionButton(this.mc, 201, x, var16, "Details..."));
        x = this.width / 2 - 155 + 160;
        this.buttonList.add(new GuiOptionButton(this.mc, 212, x, var16, "Performance..."));
        var16 += 21;
        x = this.width / 2 - 155 + 0;
        this.buttonList.add(new GuiOptionButton(this.mc, 211, x, var16, "Animations..."));
        x = this.width / 2 - 155 + 160;
        this.buttonList.add(new GuiOptionButton(this.mc, 222, x, var16, "Other..."));
        this.buttonList.add(new GuiButton(this.mc, 200, this.width / 2 - 100, this.height / 6 + 168 + 11,
                I18n.format("gui.done", new Object[0])));
    }

    protected void actionPerformed(GuiButton par1GuiButton) {
        if (par1GuiButton.enabled) {
            int var2 = this.field_146499_g.guiScale;

            if (par1GuiButton.id < 200 && par1GuiButton instanceof GuiOptionButton) {
                this.field_146499_g.setOptionValue(((GuiOptionButton) par1GuiButton).func_146136_c(), 1);
                par1GuiButton.displayString = this.field_146499_g
                        .getKeyBinding(GameSettings.Options.getEnumOptions(par1GuiButton.id));
            }

            if (par1GuiButton.id == 200) {
                this.mc.gameSettings.saveOptions();
                this.mc.displayGuiScreen(this.field_146498_f);
            }

            if (this.field_146499_g.guiScale != var2) {
                ScaledResolution scr = new ScaledResolution(this.mc, this.mc.displayWidth, this.mc.displayHeight);
                int var4 = scr.getScaledWidth();
                int var5 = scr.getScaledHeight();
                this.setWorldAndResolution(this.mc, var4, var5);
            }

            if (par1GuiButton.id == 201) {
                this.mc.gameSettings.saveOptions();
                GuiDetailSettingsOF scr1 = new GuiDetailSettingsOF(this.mc, this, this.field_146499_g);
                this.mc.displayGuiScreen(scr1);
            }

            if (par1GuiButton.id == 202) {
                this.mc.gameSettings.saveOptions();
                GuiQualitySettingsOF scr2 = new GuiQualitySettingsOF(this.mc, this, this.field_146499_g);
                this.mc.displayGuiScreen(scr2);
            }

            if (par1GuiButton.id == 211) {
                this.mc.gameSettings.saveOptions();
                GuiAnimationSettingsOF scr3 = new GuiAnimationSettingsOF(this.mc, this, this.field_146499_g);
                this.mc.displayGuiScreen(scr3);
            }

            if (par1GuiButton.id == 212) {
                this.mc.gameSettings.saveOptions();
                GuiPerformanceSettingsOF scr4 = new GuiPerformanceSettingsOF(this.mc, this, this.field_146499_g);
                this.mc.displayGuiScreen(scr4);
            }

            if (par1GuiButton.id == 222) {
                this.mc.gameSettings.saveOptions();
                GuiOtherSettingsOF scr5 = new GuiOtherSettingsOF(this.mc, this, this.field_146499_g);
                this.mc.displayGuiScreen(scr5);
            }

            if (par1GuiButton.id == GameSettings.Options.AO_LEVEL.ordinal()) {
                return;
            }
        }
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(int x, int y, float z) {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRendererObj, this.field_146500_a, this.width / 2, this.is64bit ? 20 : 5,
                16777215);
        String ver = Config.getVersion();
        String ed = "HD_U";

        if (ed.equals("HD")) {
            ver = "OptiFine HD D4";
        }

        if (ed.equals("HD_U")) {
            ver = "OptiFine HD D4 Ultra";
        }

        if (ed.equals("L")) {
            ver = "OptiFine D4 Light";
        }

        this.drawString(this.fontRendererObj, ver, 2, this.height - 10, 8421504);
        String verMc = "Minecraft 1.7.10";
        int lenMc = this.fontRendererObj.getStringWidth(verMc);
        this.drawString(this.fontRendererObj, verMc, this.width - lenMc - 2, this.height - 10, 8421504);

        if (!this.is64bit && this.field_146499_g.renderDistanceChunks > 8) {
            ;
        }

        super.drawScreen(x, y, z);

        if (Math.abs(x - this.lastMouseX) <= 5 && Math.abs(y - this.lastMouseY) <= 5) {
            short activateDelay = 700;

            if (System.currentTimeMillis() >= this.mouseStillTime + (long) activateDelay) {
                int x1 = this.width / 2 - 150;
                int y1 = this.height / 6 - 5;

                if (y <= y1 + 98) {
                    y1 += 105;
                }

                int x2 = x1 + 150 + 150;
                int y2 = y1 + 84 + 10;
                GuiButton btn = this.getSelectedButton(x, y);

                if (btn != null) {
                    String s = this.getButtonName(btn.displayString);
                    String[] lines = this.getTooltipLines(s);

                    if (lines == null) {
                        return;
                    }

                    this.drawGradientRect(x1, y1, x2, y2, -536870912, -536870912);

                    for (int i = 0; i < lines.length; ++i) {
                        String line = lines[i];
                        this.fontRendererObj.drawStringWithShadow(line, x1 + 5, y1 + 5 + i * 11, 14540253);
                    }
                }
            }
        } else {
            this.lastMouseX = x;
            this.lastMouseY = y;
            this.mouseStillTime = System.currentTimeMillis();
        }
    }

    private String[] getTooltipLines(String btnName) {
        return btnName.equals("Graphics")
                ? new String[] { "Visual quality", "  Fast  - lower quality, faster",
                        "  Fancy - higher quality, slower", "Changes the appearance of clouds, leaves, water,",
                        "shadows and grass sides." }
                : (btnName.equals("Render Distance")
                        ? new String[] { "Visible distance", "  2 Tiny - 32m (fastest)", "  4 Short - 64m (faster)",
                                "  8 Normal - 128m", "  16 Far - 256m (slower)", "  32 Extreme - 512m (slowest!)",
                                "The Extreme view distance is very resource demanding!",
                                "Values over 16 Far are only effective in local worlds." }
                        : (btnName.equals("Smooth Lighting")
                                ? new String[] { "Smooth lighting", "  OFF - no smooth lighting (faster)",
                                        "  Minimum - simple smooth lighting (slower)",
                                        "  Maximum - complex smooth lighting (slowest)" }
                                : (btnName.equals("Smooth Lighting Level")
                                        ? new String[] { "Smooth lighting level", "  OFF - no smooth lighting (faster)",
                                                "  1% - light smooth lighting (slower)",
                                                "  100% - dark smooth lighting (slower)" }
                                        : (btnName.equals("Max Framerate")
                                                ? new String[] { "Max framerate",
                                                        "  VSync - limit to monitor framerate (60, 30, 20)",
                                                        "  5-255 - variable", "  Unlimited - no limit (fastest)",
                                                        "The framerate limit decreases the FPS even if",
                                                        "the limit value is not reached." }
                                                : (btnName.equals("View Bobbing")
                                                        ? new String[] { "More realistic movement.",
                                                                "When using mipmaps set it to OFF for best results." }
                                                        : (btnName.equals("GUI Scale")
                                                                ? new String[] { "GUI Scale",
                                                                        "Smaller GUI might be faster" }
                                                                : (btnName.equals("Server Textures") ? new String[] {
                                                                        "Server textures",
                                                                        "Use the resource pack recommended by the server" }
                                                                        : (btnName.equals("Advanced OpenGL")
                                                                                ? new String[] {
                                                                                        "Detect and render only visible geometry",
                                                                                        "  OFF - all geometry is rendered (slower)",
                                                                                        "  Fast - only visible geometry is rendered (fastest)",
                                                                                        "  Fancy - conservative, avoids visual artifacts (faster)",
                                                                                        "The option is available only if it is supported by the ",
                                                                                        "graphic card." }
                                                                                : (btnName.equals(
                                                                                        "Fog") ? new String[] { "Fog type", "  Fast - faster fog", "  Fancy - slower fog, looks better", "  OFF - no fog, fastest", "The fancy fog is available only if it is supported by the ", "graphic card." } : (btnName.equals("Fog Start") ? new String[] { "Fog start", "  0.2 - the fog starts near the player", "  0.8 - the fog starts far from the player", "This option usually does not affect the performance." } : (btnName.equals("Brightness") ? new String[] { "Increases the brightness of darker objects", "  OFF - standard brightness", "  100% - maximum brightness for darker objects", "This options does not change the brightness of ", "fully black objects" } : (btnName.equals("Chunk Loading") ? new String[] { "Chunk Loading", "  Default - unstable FPS when loading chunks", "  Smooth - stable FPS", "  Multi-Core - stable FPS, 3x faster world loading", "Smooth and Multi-Core remove the stuttering and ", "freezes caused by chunk loading.", "Multi-Core can speed up 3x the world loading and", "increase FPS by using a second CPU core." } : null))))))))))));
    }

    private String getButtonName(String displayString) {
        int pos = displayString.indexOf(58);
        return pos < 0 ? displayString : displayString.substring(0, pos);
    }

    private GuiButton getSelectedButton(int i, int j) {
        for (int k = 0; k < this.buttonList.size(); ++k) {
            GuiButton btn = (GuiButton) this.buttonList.get(k);
            boolean flag = i >= btn.field_146128_h && j >= btn.field_146129_i
                    && i < btn.field_146128_h + btn.field_146120_f && j < btn.field_146129_i + btn.field_146121_g;

            if (flag) {
                return btn;
            }
        }

        return null;
    }

    public static int getButtonWidth(GuiButton btn) {
        return btn.field_146120_f;
    }

    public static int getButtonHeight(GuiButton btn) {
        return btn.field_146121_g;
    }
}
