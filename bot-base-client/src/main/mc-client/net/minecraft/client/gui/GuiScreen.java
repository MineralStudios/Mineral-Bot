package net.minecraft.client.gui;

import gg.mineral.bot.api.screen.Screen;
import gg.mineral.bot.lwjgl.opengl.GL11;
import gg.mineral.bot.lwjgl.opengl.GL12;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import org.eclipse.jdt.annotation.Nullable;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GuiScreen extends Gui implements Screen {
    /**
     * Holds a instance of RenderItem, used to draw the achievement icons on screen
     * (is based on ItemStack)
     */
    protected RenderItem itemRender;

    /**
     * The width of the screen object.
     */
    @Getter
    public int width;

    /**
     * The height of the screen object.
     */
    @Getter
    public int height;

    /**
     * A list of all the buttons in this container.
     */
    protected List<GuiButton> buttonList = new ArrayList<>();

    /**
     * A list of all the labels in this container.
     */
    protected List<GuiLabel> labelList = new ArrayList<>();
    public boolean field_146291_p;

    /**
     * The FontRenderer used by GuiScreen
     */
    @Nullable
    protected FontRenderer fontRendererObj;

    /**
     * The button that was just pressed.
     */
    private GuiButton selectedButton;
    private int eventButton;
    private long lastMouseEvent;
    private int field_146298_h;

    public GuiScreen(Minecraft mc) {
        super(mc);
        this.itemRender = new RenderItem(mc);
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(int p_73863_1_, int p_73863_2_, float p_73863_3_) {
        int var4;

        for (var4 = 0; var4 < this.buttonList.size(); ++var4) {
            this.buttonList.get(var4).drawButton(this.mc, p_73863_1_, p_73863_2_);
        }

        for (var4 = 0; var4 < this.labelList.size(); ++var4) {
            ((GuiLabel) this.labelList.get(var4)).func_146159_a(this.mc, p_73863_1_, p_73863_2_);
        }
    }

    /**
     * Fired when a key is typed. This is the equivalent of
     * KeyListener.keyTyped(KeyEvent e).
     */
    protected void keyTyped(char p_73869_1_, int p_73869_2_) {
        if (p_73869_2_ == 1) {
            this.mc.displayGuiScreen(null);
            this.mc.setIngameFocus();
        }
    }

    /**
     * Returns a string stored in the system clipboard.
     */
    public static String getClipboardString() {
        try {
            Transferable var0 = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);

            if (var0 != null && var0.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                return (String) var0.getTransferData(DataFlavor.stringFlavor);
            }
        } catch (Exception var1) {
        }

        return "";
    }

    /**
     * Stores the given string in the system clipboard
     */
    public static void setClipboardString(String p_146275_0_) {
        try {
            StringSelection var1 = new StringSelection(p_146275_0_);
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(var1, null);
        } catch (Exception var2) {
        }
    }

    protected void func_146285_a(ItemStack p_146285_1_, int p_146285_2_, int p_146285_3_) {
        List var4 = p_146285_1_.getTooltip(this.mc.thePlayer, this.mc.gameSettings.advancedItemTooltips);

        for (int var5 = 0; var5 < var4.size(); ++var5) {
            if (var5 == 0) {
                var4.set(var5, p_146285_1_.getRarity().rarityColor + (String) var4.get(var5));
            } else {
                var4.set(var5, EnumChatFormatting.GRAY + (String) var4.get(var5));
            }
        }

        this.func_146283_a(var4, p_146285_2_, p_146285_3_);
    }

    protected void func_146279_a(String p_146279_1_, int p_146279_2_, int p_146279_3_) {
        this.func_146283_a(Collections.singletonList(p_146279_1_), p_146279_2_, p_146279_3_);
    }

    protected void func_146283_a(List<String> p_146283_1_, int p_146283_2_, int p_146283_3_) {
        if (!p_146283_1_.isEmpty()) {
            GL11.glDisable(GL12.GL_RESCALE_NORMAL);
            RenderHelper.disableStandardItemLighting();
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            int var4 = 0;

            for (String o : p_146283_1_) {
                int var7 = this.fontRendererObj != null ? this.fontRendererObj.getStringWidth(o) : 0;

                if (var7 > var4) {
                    var4 = var7;
                }
            }

            int var14 = p_146283_2_ + 12;
            int var15 = p_146283_3_ - 12;
            int var8 = 8;

            if (p_146283_1_.size() > 1) {
                var8 += 2 + (p_146283_1_.size() - 1) * 10;
            }

            if (var14 + var4 > this.width) {
                var14 -= 28 + var4;
            }

            if (var15 + var8 + 6 > this.height) {
                var15 = this.height - var8 - 6;
            }

            this.zLevel = 300.0F;
            itemRender.zLevel = 300.0F;
            int var9 = -267386864;
            this.drawGradientRect(var14 - 3, var15 - 4, var14 + var4 + 3, var15 - 3, var9, var9);
            this.drawGradientRect(var14 - 3, var15 + var8 + 3, var14 + var4 + 3, var15 + var8 + 4, var9, var9);
            this.drawGradientRect(var14 - 3, var15 - 3, var14 + var4 + 3, var15 + var8 + 3, var9, var9);
            this.drawGradientRect(var14 - 4, var15 - 3, var14 - 3, var15 + var8 + 3, var9, var9);
            this.drawGradientRect(var14 + var4 + 3, var15 - 3, var14 + var4 + 4, var15 + var8 + 3, var9, var9);
            int var10 = 1347420415;
            int var11 = (var10 & 16711422) >> 1 | var10 & -16777216;
            this.drawGradientRect(var14 - 3, var15 - 3 + 1, var14 - 3 + 1, var15 + var8 + 3 - 1, var10, var11);
            this.drawGradientRect(var14 + var4 + 2, var15 - 3 + 1, var14 + var4 + 3, var15 + var8 + 3 - 1, var10,
                    var11);
            this.drawGradientRect(var14 - 3, var15 - 3, var14 + var4 + 3, var15 - 3 + 1, var10, var10);
            this.drawGradientRect(var14 - 3, var15 + var8 + 2, var14 + var4 + 3, var15 + var8 + 3, var11, var11);

            for (int var12 = 0; var12 < p_146283_1_.size(); ++var12) {
                String var13 = p_146283_1_.get(var12);
                if (this.fontRendererObj != null)
                    this.fontRendererObj.drawStringWithShadow(var13, var14, var15, -1);

                if (var12 == 0) {
                    var15 += 2;
                }

                var15 += 10;
            }

            this.zLevel = 0.0F;
            itemRender.zLevel = 0.0F;
            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            this.mc.renderHelper.enableStandardItemLighting();
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        }
    }

    /**
     * Called when the mouse is clicked.
     */
    protected void mouseClicked(int p_73864_1_, int p_73864_2_, int p_73864_3_) {
        if (p_73864_3_ == 0) {
            for (int var4 = 0; var4 < this.buttonList.size(); ++var4) {
                GuiButton var5 = this.buttonList.get(var4);

                if (var5.mousePressed(this.mc, p_73864_1_, p_73864_2_)) {
                    this.selectedButton = var5;
                    var5.func_146113_a(this.mc.getSoundHandler());
                    this.actionPerformed(var5);
                }
            }
        }
    }

    protected void mouseMovedOrUp(int p_146286_1_, int p_146286_2_, int p_146286_3_) {
        if (this.selectedButton != null && p_146286_3_ == 0) {
            this.selectedButton.mouseReleased(p_146286_1_, p_146286_2_);
            this.selectedButton = null;
        }
    }

    protected void mouseClickMove(int p_146273_1_, int p_146273_2_, int p_146273_3_, long p_146273_4_) {
    }

    protected void actionPerformed(GuiButton p_146284_1_) {
    }

    /**
     * Causes the screen to lay out its subcomponents again. This is the equivalent
     * of the Java call
     * Container.validate()
     */
    public void setWorldAndResolution(Minecraft p_146280_1_, int p_146280_2_, int p_146280_3_) {
        this.fontRendererObj = p_146280_1_.fontRenderer;
        this.width = p_146280_2_;
        this.height = p_146280_3_;
        this.buttonList.clear();
        this.initGui();
    }

    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    public void initGui() {
    }

    /**
     * Delegates mouse and keyboard input.
     */
    public void handleInput() {
        if (this.mc.getMouse().isCreated())
            while (this.mc.getMouse().next())
                this.handleMouseInput();

        if (this.mc.getKeyboard().isCreated())
            while (this.mc.getKeyboard().next())
                this.handleKeyboardInput();
    }

    /**
     * Handles mouse input.
     */
    public void handleMouseInput() {
        int var1 = this.mc.getMouse().getEventX() * this.width / this.mc.displayWidth;
        int var2 = this.height - this.mc.getMouse().getEventY() * this.height / this.mc.displayHeight - 1;
        int var3 = this.mc.getMouse().getEventButton();

        if (this.mc.getMouse().getEventButtonState()) {
            if (this.mc.gameSettings.touchscreen && this.field_146298_h++ > 0) {
                return;
            }

            this.eventButton = var3;
            this.lastMouseEvent = Minecraft.getSystemTime();
            this.mouseClicked(var1, var2, this.eventButton);
        } else if (var3 != -1) {
            if (this.mc.gameSettings.touchscreen && --this.field_146298_h > 0) {
                return;
            }

            this.eventButton = -1;
            this.mouseMovedOrUp(var1, var2, var3);
        } else if (this.eventButton != -1 && this.lastMouseEvent > 0L) {
            long var4 = Minecraft.getSystemTime() - this.lastMouseEvent;
            this.mouseClickMove(var1, var2, this.eventButton, var4);
        }
    }

    /**
     * Handles keyboard input.
     */
    public void handleKeyboardInput() {
        if (this.mc.getKeyboard().getEventKeyState()) {
            this.keyTyped(this.mc.getKeyboard().getEventCharacter(), this.mc.getKeyboard().getEventKey());
        }

        this.mc.func_152348_aa();
    }

    /**
     * Called from the main game loop to update the screen.
     */
    public void updateScreen() {
    }

    /**
     * "Called when the screen is unloaded. Used to disable keyboard repeat events."
     */
    public void onGuiClosed() {
    }

    public void drawDefaultBackground() {
        this.func_146270_b(0);
    }

    public void func_146270_b(int p_146270_1_) {
        if (this.mc.theWorld != null) {
            this.drawGradientRect(0, 0, this.width, this.height, -1072689136, -804253680);
        } else {
            this.func_146278_c(p_146270_1_);
        }
    }

    public void func_146278_c(int p_146278_1_) {
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_FOG);
        Tessellator var2 = this.mc.getTessellator();
        TextureManager textureManager = this.mc.getTextureManager();

        if (textureManager != null)
            textureManager.bindTexture(optionsBackground);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        float var3 = 32.0F;

        if (var2 == null)
            return;
        var2.startDrawingQuads();
        var2.setColorOpaque_I(4210752);
        var2.addVertexWithUV(0.0D, this.height, 0.0D, 0.0D,
                (float) this.height / var3 + (float) p_146278_1_);
        var2.addVertexWithUV(this.width, this.height, 0.0D, (float) this.width / var3,
                (float) this.height / var3 + (float) p_146278_1_);
        var2.addVertexWithUV(this.width, 0.0D, 0.0D, (float) this.width / var3,
                p_146278_1_);
        var2.addVertexWithUV(0.0D, 0.0D, 0.0D, 0.0D, p_146278_1_);
        var2.draw();
    }

    /**
     * Returns true if this GUI should pause the game when it is displayed in
     * single-player
     */
    public boolean doesGuiPauseGame() {
        return true;
    }

    public void confirmClicked(boolean p_73878_1_, int p_73878_2_) {
    }

    /**
     * Returns true if either windows ctrl key is down or if either mac meta key is
     * down
     */
    public static boolean isCtrlKeyDown(Minecraft mc) {
        return Minecraft.isRunningOnMac ? mc.getKeyboard().isKeyDown(219) || mc.getKeyboard().isKeyDown(220)
                : mc.getKeyboard().isKeyDown(29) || mc.getKeyboard().isKeyDown(157);
    }

    /**
     * Returns true if either shift key is down
     */
    public static boolean isShiftKeyDown(Minecraft mc) {
        return mc.getKeyboard().isKeyDown(42) || mc.getKeyboard().isKeyDown(54);
    }
}
