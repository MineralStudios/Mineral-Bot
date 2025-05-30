package net.minecraft.client.gui;

import gg.mineral.bot.lwjgl.opengl.GL11;
import gg.mineral.bot.lwjgl.opengl.GLContext;
import gg.mineral.bot.lwjgl.util.glu.Project;
import gg.mineral.bot.impl.config.BotGlobalConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.demo.DemoWorldServer;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.WorldInfo;
import org.apache.commons.io.Charsets;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.eclipse.jdt.annotation.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class GuiMainMenu extends GuiScreen implements GuiYesNoCallback {
    private static final Logger logger = LogManager.getLogger(GuiMainMenu.class);

    /**
     * The RNG used by the Main Menu Screen.
     */
    private static final Random rand = new Random();

    /**
     * Counts the number of screen updates.
     */
    private float updateCounter;

    /**
     * The splash message.
     */
    private String splashText;
    private GuiButton buttonResetDemo;

    /**
     * Timer used to rotate the panorama, increases every tick.
     */
    private int panoramaTimer;

    /**
     * Texture allocated for the current viewport of the main menu's panorama
     * background.
     */
    @Nullable
    private DynamicTexture viewportTexture;
    private final Object field_104025_t = new Object();
    private String field_92025_p;
    private String field_146972_A;
    private String field_104024_v;
    private static final ResourceLocation splashTexts = new ResourceLocation("texts/splashes.txt");
    private static final ResourceLocation minecraftTitleTextures = new ResourceLocation(
            "textures/gui/title/minecraft.png");

    /**
     * An array of all the paths to the panorama pictures.
     */
    private static final ResourceLocation[] titlePanoramaPaths = new ResourceLocation[]{
            new ResourceLocation("textures/gui/title/background/panorama_0.png"),
            new ResourceLocation("textures/gui/title/background/panorama_1.png"),
            new ResourceLocation("textures/gui/title/background/panorama_2.png"),
            new ResourceLocation("textures/gui/title/background/panorama_3.png"),
            new ResourceLocation("textures/gui/title/background/panorama_4.png"),
            new ResourceLocation("textures/gui/title/background/panorama_5.png")};
    public static final String field_96138_a = "Please click " + EnumChatFormatting.UNDERLINE + "here"
            + EnumChatFormatting.RESET + " for more information.";
    private int field_92024_r;
    private int field_92023_s;
    private int field_92022_t;
    private int field_92021_u;
    private int field_92020_v;
    private int field_92019_w;
    private ResourceLocation field_110351_G;

    @SuppressWarnings("deprecation")
    public GuiMainMenu(Minecraft mc) {
        super(mc);
        this.field_146972_A = field_96138_a;
        this.splashText = "missingno";
        if (!BotGlobalConfig.optimizedGameLoop) {
            BufferedReader var1 = null;

            try {
                ArrayList<String> var2 = new ArrayList<>();
                var1 = new BufferedReader(new InputStreamReader(
                        mc.getResourceManager().getResource(splashTexts).getInputStream(),
                        Charsets.UTF_8));
                String var3;

                while ((var3 = var1.readLine()) != null) {
                    var3 = var3.trim();

                    if (!var3.isEmpty()) {
                        var2.add(var3);
                    }
                }

                if (!var2.isEmpty()) {
                    do {
                        this.splashText = (String) var2.get(rand.nextInt(var2.size()));
                    } while (this.splashText.hashCode() == 125780783);
                }
            } catch (IOException var12) {
                ;
            } finally {
                if (var1 != null) {
                    try {
                        var1.close();
                    } catch (IOException var11) {
                        ;
                    }
                }
            }
        }

        this.updateCounter = rand.nextFloat();
        this.field_92025_p = "";

        if (!GLContext.getCapabilities().OpenGL20 && !OpenGlHelper.func_153193_b()) {
            this.field_92025_p = I18n.format("title.oldgl1", new Object[0]);
            this.field_146972_A = I18n.format("title.oldgl2", new Object[0]);
            this.field_104024_v = "https://help.mojang.com/customer/portal/articles/325948?ref=game";
        }
    }

    /**
     * Called from the main game loop to update the screen.
     */
    public void updateScreen() {
        ++this.panoramaTimer;
    }

    /**
     * Returns true if this GUI should pause the game when it is displayed in
     * single-player
     */
    public boolean doesGuiPauseGame() {
        return false;
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
        this.viewportTexture = this.mc.textureUtil == null ? null
                : new DynamicTexture(this.mc, this.mc.textureUtil.dataBuffer, 256, 256);

        TextureManager textureManager = this.mc.getTextureManager();

        if (textureManager != null)
            this.field_110351_G = textureManager.getDynamicTextureLocation("background", this.viewportTexture);
        Calendar var1 = Calendar.getInstance();
        var1.setTime(new Date());

        if (var1.get(2) + 1 == 11 && var1.get(5) == 9) {
            this.splashText = "Happy birthday, ez!";
        } else if (var1.get(2) + 1 == 6 && var1.get(5) == 1) {
            this.splashText = "Happy birthday, Notch!";
        } else if (var1.get(2) + 1 == 12 && var1.get(5) == 24) {
            this.splashText = "Merry X-mas!";
        } else if (var1.get(2) + 1 == 1 && var1.get(5) == 1) {
            this.splashText = "Happy new year!";
        } else if (var1.get(2) + 1 == 10 && var1.get(5) == 31) {
            this.splashText = "OOoooOOOoooo! Spooky!";
        }

        int var3 = this.height / 4 + 48;

        if (this.mc.isDemo()) {
            this.addDemoButtons(var3, 24);
        } else {
            this.addSingleplayerMultiplayerButtons(var3, 24);
        }

        this.buttonList.add(new GuiButton(this.mc, 0, this.width / 2 - 100, var3 + 72 + 12, 98, 20,
                I18n.format("menu.options", new Object[0])));
        this.buttonList.add(
                new GuiButton(this.mc, 4, this.width / 2 + 2, var3 + 72 + 12, 98, 20,
                        I18n.format("menu.quit", new Object[0])));
        this.buttonList.add(new GuiButtonLanguage(this.mc, 5, this.width / 2 - 124, var3 + 72 + 12));
        synchronized (this.field_104025_t) {
            this.field_92023_s = this.fontRendererObj.getStringWidth(this.field_92025_p);
            this.field_92024_r = this.fontRendererObj.getStringWidth(this.field_146972_A);
            int var5 = Math.max(this.field_92023_s, this.field_92024_r);
            this.field_92022_t = (this.width - var5) / 2;
            this.field_92021_u = ((GuiButton) this.buttonList.get(0)).field_146129_i - 24;
            this.field_92020_v = this.field_92022_t + var5;
            this.field_92019_w = this.field_92021_u + 24;
        }
    }

    /**
     * Adds Singleplayer and Multiplayer buttons on Main Menu for players who have
     * bought the game.
     */
    private void addSingleplayerMultiplayerButtons(int p_73969_1_, int p_73969_2_) {
        this.buttonList.add(
                new GuiButton(this.mc, 1, this.width / 2 - 100, p_73969_1_,
                        I18n.format("menu.singleplayer", new Object[0])));
        this.buttonList.add(new GuiButton(this.mc, 2, this.width / 2 - 100, p_73969_1_ + p_73969_2_ * 1,
                I18n.format("menu.multiplayer", new Object[0])));
        this.buttonList.add(new GuiButton(this.mc, 14, this.width / 2 - 100, p_73969_1_ + p_73969_2_ * 2,
                I18n.format("menu.online", new Object[0])));
    }

    /**
     * Adds Demo buttons on Main Menu for players who are playing Demo.
     */
    private void addDemoButtons(int p_73972_1_, int p_73972_2_) {
        this.buttonList
                .add(new GuiButton(this.mc, 11, this.width / 2 - 100, p_73972_1_,
                        I18n.format("menu.playdemo", new Object[0])));
        this.buttonList.add(
                this.buttonResetDemo = new GuiButton(this.mc, 12, this.width / 2 - 100, p_73972_1_ + p_73972_2_ * 1,
                        I18n.format("menu.resetdemo", new Object[0])));
        ISaveFormat var3 = this.mc.getSaveLoader();
        WorldInfo var4 = var3.getWorldInfo("Demo_World");

        if (var4 == null) {
            this.buttonResetDemo.enabled = false;
        }
    }

    protected void actionPerformed(GuiButton button) {
        if (button.id == 0) {
            this.mc.displayGuiScreen(new GuiOptions(this.mc, this, this.mc.gameSettings));
        }

        if (button.id == 5) {
            this.mc.displayGuiScreen(
                    new GuiLanguage(this.mc, this, this.mc.gameSettings, this.mc.getLanguageManager()));
        }

        if (button.id == 1) {
            this.mc.displayGuiScreen(new GuiSelectWorld(this.mc, this));
        }

        if (button.id == 2) {
            this.mc.displayGuiScreen(new GuiMultiplayer(this.mc, this));
        }

        if (button.id == 4) {
            this.mc.shutdown();
        }

        if (button.id == 11) {
            this.mc.launchIntegratedServer("Demo_World", "Demo_World", DemoWorldServer.demoWorldSettings);
        }

        if (button.id == 12) {
            ISaveFormat var2 = this.mc.getSaveLoader();
            WorldInfo var3 = var2.getWorldInfo("Demo_World");

            if (var3 != null) {
                GuiYesNo var4 = GuiSelectWorld.func_152129_a(this.mc, this, var3.getWorldName(), 12);
                this.mc.displayGuiScreen(var4);
            }
        }
    }

    public void confirmClicked(boolean p_73878_1_, int p_73878_2_) {
        if (p_73878_1_ && p_73878_2_ == 12) {
            ISaveFormat var6 = this.mc.getSaveLoader();
            var6.flushCache();
            var6.deleteWorldDirectory("Demo_World");
            this.mc.displayGuiScreen(this);
        } else if (p_73878_2_ == 13) {
            if (p_73878_1_) {
                try {
                    Class<?> var3 = Class.forName("java.awt.Desktop");
                    Object var4 = var3.getMethod("getDesktop", new Class[0]).invoke((Object) null, new Object[0]);
                    var3.getMethod("browse", new Class[]{URI.class}).invoke(var4,
                            new Object[]{new URI(this.field_104024_v)});
                } catch (Throwable var5) {
                    logger.error("Couldn\'t open link", var5);
                }
            }

            this.mc.displayGuiScreen(this);
        }
    }

    /**
     * Draws the main menu panorama
     */
    private void drawPanorama(int p_73970_1_, int p_73970_2_, float p_73970_3_) {
        Tessellator var4 = this.mc.getTessellator();
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        Project.gluPerspective(120.0F, 1.0F, 0.05F, 10.0F);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(90.0F, 0.0F, 0.0F, 1.0F);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glDepthMask(false);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        byte var5 = 8;

        for (int var6 = 0; var6 < var5 * var5; ++var6) {
            GL11.glPushMatrix();
            float var7 = ((float) (var6 % var5) / (float) var5 - 0.5F) / 64.0F;
            float var8 = ((float) (var6 / var5) / (float) var5 - 0.5F) / 64.0F;
            float var9 = 0.0F;
            GL11.glTranslatef(var7, var8, var9);
            GL11.glRotatef(MathHelper.sin(((float) this.panoramaTimer + p_73970_3_) / 400.0F) * 25.0F + 20.0F, 1.0F,
                    0.0F, 0.0F);
            GL11.glRotatef(-((float) this.panoramaTimer + p_73970_3_) * 0.1F, 0.0F, 1.0F, 0.0F);

            for (int var10 = 0; var10 < 6; ++var10) {
                GL11.glPushMatrix();

                if (var10 == 1)
                    GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);

                if (var10 == 2)
                    GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);

                if (var10 == 3)
                    GL11.glRotatef(-90.0F, 0.0F, 1.0F, 0.0F);

                if (var10 == 4)
                    GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);

                if (var10 == 5)
                    GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);

                TextureManager textureManager = this.mc.getTextureManager();

                if (textureManager != null)
                    textureManager.bindTexture(titlePanoramaPaths[var10]);

                if (var4 != null) {
                    var4.startDrawingQuads();
                    var4.setColorRGBA_I(16777215, 255 / (var6 + 1));
                    float var11 = 0.0F;
                    var4.addVertexWithUV(-1.0D, -1.0D, 1.0D, (double) (0.0F + var11), (double) (0.0F + var11));
                    var4.addVertexWithUV(1.0D, -1.0D, 1.0D, (double) (1.0F - var11), (double) (0.0F + var11));
                    var4.addVertexWithUV(1.0D, 1.0D, 1.0D, (double) (1.0F - var11), (double) (1.0F - var11));
                    var4.addVertexWithUV(-1.0D, 1.0D, 1.0D, (double) (0.0F + var11), (double) (1.0F - var11));
                    var4.draw();
                }
                GL11.glPopMatrix();
            }

            GL11.glPopMatrix();
            GL11.glColorMask(true, true, true, false);
        }

        if (var4 != null)
            var4.setTranslation(0.0D, 0.0D, 0.0D);
        GL11.glColorMask(true, true, true, true);
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glPopMatrix();
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glPopMatrix();
        GL11.glDepthMask(true);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
    }

    /**
     * Rotate and blurs the skybox view in the main menu
     */
    private void rotateAndBlurSkybox(float p_73968_1_) {
        TextureManager textureManager = this.mc.getTextureManager();

        if (textureManager != null)
            textureManager.bindTexture(this.field_110351_G);

        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        GL11.glCopyTexSubImage2D(GL11.GL_TEXTURE_2D, 0, 0, 0, 0, 0, 256, 256);
        GL11.glEnable(GL11.GL_BLEND);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        GL11.glColorMask(true, true, true, false);
        Tessellator var2 = this.mc.getTessellator();
        if (var2 != null) {
            var2.startDrawingQuads();
            GL11.glDisable(GL11.GL_ALPHA_TEST);
            byte var3 = 3;

            for (int var4 = 0; var4 < var3; ++var4) {
                var2.setColorRGBA_F(1.0F, 1.0F, 1.0F, 1.0F / (float) (var4 + 1));
                int var5 = this.width;
                int var6 = this.height;
                float var7 = (float) (var4 - var3 / 2) / 256.0F;
                var2.addVertexWithUV((double) var5, (double) var6, (double) this.zLevel, (double) (0.0F + var7), 1.0D);
                var2.addVertexWithUV((double) var5, 0.0D, (double) this.zLevel, (double) (1.0F + var7), 1.0D);
                var2.addVertexWithUV(0.0D, 0.0D, (double) this.zLevel, (double) (1.0F + var7), 0.0D);
                var2.addVertexWithUV(0.0D, (double) var6, (double) this.zLevel, (double) (0.0F + var7), 0.0D);
            }

            var2.draw();
        }
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glColorMask(true, true, true, true);
    }

    /**
     * Renders the skybox in the main menu
     */
    private void renderSkybox(int p_73971_1_, int p_73971_2_, float p_73971_3_) {
        this.mc.getFramebuffer().unbindFramebuffer();
        GL11.glViewport(0, 0, 256, 256);
        this.drawPanorama(p_73971_1_, p_73971_2_, p_73971_3_);
        this.rotateAndBlurSkybox(p_73971_3_);
        this.rotateAndBlurSkybox(p_73971_3_);
        this.rotateAndBlurSkybox(p_73971_3_);
        this.rotateAndBlurSkybox(p_73971_3_);
        this.rotateAndBlurSkybox(p_73971_3_);
        this.rotateAndBlurSkybox(p_73971_3_);
        this.rotateAndBlurSkybox(p_73971_3_);
        this.mc.getFramebuffer().bindFramebuffer(true);
        GL11.glViewport(0, 0, this.mc.displayWidth, this.mc.displayHeight);
        Tessellator var4 = this.mc.getTessellator();
        if (var4 == null)
            return;
        var4.startDrawingQuads();
        float var5 = this.width > this.height ? 120.0F / (float) this.width : 120.0F / (float) this.height;
        float var6 = (float) this.height * var5 / 256.0F;
        float var7 = (float) this.width * var5 / 256.0F;
        var4.setColorRGBA_F(1.0F, 1.0F, 1.0F, 1.0F);
        int var8 = this.width;
        int var9 = this.height;
        var4.addVertexWithUV(0.0D, (double) var9, (double) this.zLevel, (double) (0.5F - var6), (double) (0.5F + var7));
        var4.addVertexWithUV((double) var8, (double) var9, (double) this.zLevel, (double) (0.5F - var6),
                (double) (0.5F - var7));
        var4.addVertexWithUV((double) var8, 0.0D, (double) this.zLevel, (double) (0.5F + var6), (double) (0.5F - var7));
        var4.addVertexWithUV(0.0D, 0.0D, (double) this.zLevel, (double) (0.5F + var6), (double) (0.5F + var7));
        var4.draw();
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(int p_73863_1_, int p_73863_2_, float p_73863_3_) {
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        this.renderSkybox(p_73863_1_, p_73863_2_, p_73863_3_);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        Tessellator var4 = this.mc.getTessellator();
        short var5 = 274;
        int var6 = this.width / 2 - var5 / 2;
        byte var7 = 30;
        this.drawGradientRect(0, 0, this.width, this.height, -2130706433, 16777215);
        this.drawGradientRect(0, 0, this.width, this.height, 0, Integer.MIN_VALUE);

        TextureManager textureManager = this.mc.getTextureManager();

        if (textureManager != null)
            textureManager.bindTexture(minecraftTitleTextures);

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        if ((double) this.updateCounter < 1.0E-4D) {
            this.drawTexturedModalRect(var6 + 0, var7 + 0, 0, 0, 99, 44);
            this.drawTexturedModalRect(var6 + 99, var7 + 0, 129, 0, 27, 44);
            this.drawTexturedModalRect(var6 + 99 + 26, var7 + 0, 126, 0, 3, 44);
            this.drawTexturedModalRect(var6 + 99 + 26 + 3, var7 + 0, 99, 0, 26, 44);
            this.drawTexturedModalRect(var6 + 155, var7 + 0, 0, 45, 155, 44);
        } else {
            this.drawTexturedModalRect(var6 + 0, var7 + 0, 0, 0, 155, 44);
            this.drawTexturedModalRect(var6 + 155, var7 + 0, 0, 45, 155, 44);
        }

        if (var4 != null)
            var4.setColorOpaque_I(-1);
        GL11.glPushMatrix();
        GL11.glTranslatef((float) (this.width / 2 + 90), 70.0F, 0.0F);
        GL11.glRotatef(-20.0F, 0.0F, 0.0F, 1.0F);
        float var8 = 1.8F - MathHelper.abs(
                MathHelper.sin((float) (Minecraft.getSystemTime() % 1000L) / 1000.0F * (float) Math.PI * 2.0F) * 0.1F);
        var8 = var8 * 100.0F / (float) (this.fontRendererObj.getStringWidth(this.splashText) + 32);
        GL11.glScalef(var8, var8, var8);
        this.drawCenteredString(this.fontRendererObj, this.splashText, 0, -8, -256);
        GL11.glPopMatrix();
        String var9 = "Minecraft 1.7.10";

        if (this.mc.isDemo())
            var9 = var9 + " Demo";

        this.drawString(this.fontRendererObj, var9, 2, this.height - 10, -1);
        String var10 = "Copyright Mojang AB. Do not distribute!";
        this.drawString(this.fontRendererObj, var10, this.width - this.fontRendererObj.getStringWidth(var10) - 2,
                this.height - 10, -1);

        if (this.field_92025_p != null && this.field_92025_p.length() > 0) {
            drawRect(this.mc, this.field_92022_t - 2, this.field_92021_u - 2, this.field_92020_v + 2,
                    this.field_92019_w - 1,
                    1428160512);
            this.drawString(this.fontRendererObj, this.field_92025_p, this.field_92022_t, this.field_92021_u, -1);
            this.drawString(this.fontRendererObj, this.field_146972_A, (this.width - this.field_92024_r) / 2,
                    ((GuiButton) this.buttonList.get(0)).field_146129_i - 12, -1);
        }

        super.drawScreen(p_73863_1_, p_73863_2_, p_73863_3_);
    }

    /**
     * Called when the mouse is clicked.
     */
    protected void mouseClicked(int p_73864_1_, int p_73864_2_, int p_73864_3_) {
        super.mouseClicked(p_73864_1_, p_73864_2_, p_73864_3_);
        synchronized (this.field_104025_t) {
            if (this.field_92025_p.length() > 0 && p_73864_1_ >= this.field_92022_t && p_73864_1_ <= this.field_92020_v
                    && p_73864_2_ >= this.field_92021_u && p_73864_2_ <= this.field_92019_w) {
                GuiConfirmOpenLink var5 = new GuiConfirmOpenLink(this.mc, this, this.field_104024_v, 13, true);
                var5.func_146358_g();
                this.mc.displayGuiScreen(var5);
            }
        }
    }
}
