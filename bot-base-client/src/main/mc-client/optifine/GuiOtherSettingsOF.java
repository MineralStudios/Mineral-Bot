package optifine;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiOptionButton;
import net.minecraft.client.gui.GuiOptionSlider;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiVideoSettings;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.client.gui.GuiYesNoCallback;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;

public class GuiOtherSettingsOF extends GuiScreen implements GuiYesNoCallback {
    private GuiScreen prevScreen;
    protected String title = "Other Settings";
    private GameSettings settings;
    private static GameSettings.Options[] enumOptions = new GameSettings.Options[] { GameSettings.Options.LAGOMETER,
            GameSettings.Options.PROFILER, GameSettings.Options.WEATHER, GameSettings.Options.TIME,
            GameSettings.Options.USE_FULLSCREEN, GameSettings.Options.FULLSCREEN_MODE, GameSettings.Options.SHOW_FPS,
            GameSettings.Options.AUTOSAVE_TICKS };
    private int lastMouseX = 0;
    private int lastMouseY = 0;
    private long mouseStillTime = 0L;

    public GuiOtherSettingsOF(Minecraft mc, GuiScreen guiscreen, GameSettings gamesettings) {
        super(mc);
        this.prevScreen = guiscreen;
        this.settings = gamesettings;
    }

    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    public void initGui() {
        int i = 0;
        GameSettings.Options[] aenumoptions = enumOptions;
        int j = aenumoptions.length;

        for (int k = 0; k < j; ++k) {
            GameSettings.Options enumoptions = aenumoptions[k];
            int x = this.width / 2 - 155 + i % 2 * 160;
            int y = this.height / 6 + 21 * (i / 2) - 10;

            if (!enumoptions.getEnumFloat()) {
                this.buttonList.add(new GuiOptionButton(this.mc, enumoptions.returnEnumOrdinal(), x, y, enumoptions,
                        this.settings.getKeyBinding(enumoptions)));
            } else {
                this.buttonList.add(new GuiOptionSlider(this.mc, enumoptions.returnEnumOrdinal(), x, y, enumoptions));
            }

            ++i;
        }

        this.buttonList.add(
                new GuiButton(this.mc, 210, this.width / 2 - 100, this.height / 6 + 168 + 11 - 44,
                        "Reset Video Settings..."));
        this.buttonList.add(new GuiButton(this.mc, 200, this.width / 2 - 100, this.height / 6 + 168 + 11,
                I18n.format("gui.done", new Object[0])));
    }

    protected void actionPerformed(GuiButton guibutton) {
        if (guibutton.enabled) {
            if (guibutton.id < 200 && guibutton instanceof GuiOptionButton) {
                this.settings.setOptionValue(((GuiOptionButton) guibutton).func_146136_c(), 1);
                guibutton.displayString = this.settings
                        .getKeyBinding(GameSettings.Options.getEnumOptions(guibutton.id));
            }

            if (guibutton.id == 200) {
                this.mc.gameSettings.saveOptions();
                this.mc.displayGuiScreen(this.prevScreen);
            }

            if (guibutton.id == 210) {
                this.mc.gameSettings.saveOptions();
                GuiYesNo scaledresolution = new GuiYesNo(this.mc, this,
                        "Reset all video settings to their default values?", "",
                        9999);
                this.mc.displayGuiScreen(scaledresolution);
            }

            if (guibutton.id != GameSettings.Options.CLOUD_HEIGHT.ordinal()) {
                ScaledResolution scaledresolution1 = new ScaledResolution(this.mc, this.mc.displayWidth,
                        this.mc.displayHeight);
                int i = scaledresolution1.getScaledWidth();
                int j = scaledresolution1.getScaledHeight();
                this.setWorldAndResolution(this.mc, i, j);
            }
        }
    }

    public void confirmClicked(boolean flag, int i) {
        if (flag) {
            this.mc.gameSettings.resetSettings();
        }

        this.mc.displayGuiScreen(this);
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(int x, int y, float f) {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRendererObj, this.title, this.width / 2, 20, 16777215);
        super.drawScreen(x, y, f);

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
                        if (this.fontRendererObj != null)
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
        return btnName.equals("Autosave")
                ? new String[] { "Autosave interval", "Default autosave interval (2s) is NOT RECOMMENDED.",
                        "Autosave causes the famous Lag Spike of Death." }
                : (btnName.equals("Lagometer") ? new String[] { "Lagometer", " OFF - no lagometer, faster",
                        " ON - debug screen with lagometer, slower", "Shows the lagometer on the debug screen (F3).",
                        "* White - tick", "* Red - chunk loading", "* Green - frame rendering + internal server",
                        "* Blue - internal server (when Smooth World is ON)" }
                        : (btnName.equals("Debug Profiler")
                                ? new String[] { "Debug Profiler", "  ON - debug profiler is active, slower",
                                        "  OFF - debug profiler is not active, faster",
                                        "The debug profiler collects and shows debug information",
                                        "when the debug screen is open (F3)" }
                                : (btnName.equals("Time") ? new String[] { "Time", " Default - normal day/night cycles",
                                        " Day Only - day only", " Night Only - night only",
                                        "The time setting is only effective in CREATIVE mode", "and for local worlds." }
                                        : (btnName.equals("Weather")
                                                ? new String[] { "Weather", "  ON - weather is active, slower",
                                                        "  OFF - weather is not active, faster",
                                                        "The weather controls rain, snow and thunderstorms.",
                                                        "Weather control is only possible for local worlds." }
                                                : (btnName.equals("Fullscreen")
                                                        ? new String[] { "Fullscreen", "  ON - use fullscreen mode",
                                                                "  OFF - use window mode",
                                                                "Fullscreen mode may be faster or slower than",
                                                                "window mode, depending on the graphics card." }
                                                        : (btnName.equals("Fullscreen Mode") ? new String[] {
                                                                "Fullscreen mode",
                                                                "  Default - use desktop screen resolution, slower",
                                                                "  WxH - use custom screen resolution, may be faster",
                                                                "The selected resolution is used in fullscreen mode (F11).",
                                                                "Lower resolutions should generally be faster." }
                                                                : (btnName.equals("3D Anaglyph") ? new String[] {
                                                                        "3D mode used with red-cyan 3D glasses." }
                                                                        : null)))))));
    }

    private String getButtonName(String displayString) {
        int pos = displayString.indexOf(58);
        return pos < 0 ? displayString : displayString.substring(0, pos);
    }

    private GuiButton getSelectedButton(int i, int j) {
        for (int k = 0; k < this.buttonList.size(); ++k) {
            GuiButton btn = (GuiButton) this.buttonList.get(k);
            int btnWidth = GuiVideoSettings.getButtonWidth(btn);
            int btnHeight = GuiVideoSettings.getButtonHeight(btn);
            boolean flag = i >= btn.field_146128_h && j >= btn.field_146129_i && i < btn.field_146128_h + btnWidth
                    && j < btn.field_146129_i + btnHeight;

            if (flag) {
                return btn;
            }
        }

        return null;
    }
}
