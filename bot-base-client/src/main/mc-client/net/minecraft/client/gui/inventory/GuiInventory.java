package net.minecraft.client.gui.inventory;

import gg.mineral.bot.api.screen.type.InventoryScreen;
import gg.mineral.bot.lwjgl.opengl.GL11;
import gg.mineral.bot.lwjgl.opengl.GL12;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.achievement.GuiAchievements;
import net.minecraft.client.gui.achievement.GuiStats;
import net.minecraft.client.renderer.InventoryEffectRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

public class GuiInventory extends InventoryEffectRenderer implements InventoryScreen {
    private float field_147048_u;
    private float field_147047_v;

    public GuiInventory(Minecraft mc, EntityPlayer p_i1094_1_) {
        super(mc, p_i1094_1_.inventoryContainer);
        this.field_146291_p = true;
    }

    @Override
    public int getScaleFactor() {
        int scaleFactor = 1;
        // This flag is usually used for checking if fancy graphics are enabled.
        boolean fancyGraphics = mc.func_152349_b();
        // Get the user-specified GUI scale; if it is 0, use a large number (effectively "no limit")
        int guiScale = mc.gameSettings.guiScale;
        if (guiScale == 0) {
            guiScale = 1000;
        }

        // Increase the scale factor as long as it doesn't cause the scaled dimensions
        // to drop below 320x240 and we haven't reached the user limit.
        while (scaleFactor < guiScale
                && mc.displayWidth / (scaleFactor + 1) >= 320
                && mc.displayHeight / (scaleFactor + 1) >= 240) {
            scaleFactor++;
        }

        // In some cases (e.g., fancy graphics enabled), adjust the scale factor so it is even.
        if (fancyGraphics && scaleFactor % 2 != 0 && scaleFactor != 1) {
            scaleFactor--;
        }

        return scaleFactor;
    }

    /**
     * Called from the main game loop to update the screen.
     */
    public void updateScreen() {
        if (this.mc.playerController.isInCreativeMode()) {
            this.mc.displayGuiScreen(new GuiContainerCreative(this.mc, this.mc.thePlayer));
        }
    }

    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    public void initGui() {
        this.buttonList.clear();

        if (this.mc.playerController.isInCreativeMode()) {
            this.mc.displayGuiScreen(new GuiContainerCreative(this.mc, this.mc.thePlayer));
        } else {
            super.initGui();
        }
    }

    protected void func_146979_b(int p_146979_1_, int p_146979_2_) {
        if (this.fontRendererObj != null)
            this.fontRendererObj.drawString(I18n.format("container.crafting"), 86, 16, 4210752);
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(int p_73863_1_, int p_73863_2_, float p_73863_3_) {
        super.drawScreen(p_73863_1_, p_73863_2_, p_73863_3_);
        this.field_147048_u = (float) p_73863_1_;
        this.field_147047_v = (float) p_73863_2_;
    }

    protected void func_146976_a(float p_146976_1_, int p_146976_2_, int p_146976_3_) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        TextureManager textureManager = this.mc.getTextureManager();

        if (textureManager != null)
            textureManager.bindTexture(field_147001_a);

        int var4 = this.xShift;
        int var5 = this.yShift;
        this.drawTexturedModalRect(var4, var5, 0, 0, this.field_146999_f, this.field_147000_g);
        func_147046_a(this.mc, this.mc.renderManager, var4 + 51, var5 + 75, 30, (float) (var4 + 51) - this.field_147048_u,
                (float) (var5 + 75 - 50) - this.field_147047_v, this.mc.thePlayer);
    }

    public static void func_147046_a(Minecraft mc, RenderManager instance, int p_147046_0_, int p_147046_1_, int p_147046_2_,
                                     float p_147046_3_,
                                     float p_147046_4_, EntityLivingBase p_147046_5_) {
        GL11.glEnable(GL11.GL_COLOR_MATERIAL);
        GL11.glPushMatrix();
        GL11.glTranslatef((float) p_147046_0_, (float) p_147046_1_, 50.0F);
        GL11.glScalef((float) (-p_147046_2_), (float) p_147046_2_, (float) p_147046_2_);
        GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
        float var6 = p_147046_5_.renderYawOffset;
        float var7 = p_147046_5_.rotationYaw;
        float var8 = p_147046_5_.rotationPitch;
        float var9 = p_147046_5_.prevRotationYawHead;
        float var10 = p_147046_5_.rotationYawHead;
        GL11.glRotatef(135.0F, 0.0F, 1.0F, 0.0F);
        mc.renderHelper.enableStandardItemLighting();
        GL11.glRotatef(-135.0F, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(-((float) Math.atan(p_147046_4_ / 40.0F)) * 20.0F, 1.0F, 0.0F, 0.0F);
        p_147046_5_.renderYawOffset = (float) Math.atan(p_147046_3_ / 40.0F) * 20.0F;
        p_147046_5_.rotationYaw = (float) Math.atan(p_147046_3_ / 40.0F) * 40.0F;
        p_147046_5_.rotationPitch = -((float) Math.atan(p_147046_4_ / 40.0F)) * 20.0F;
        p_147046_5_.rotationYawHead = p_147046_5_.rotationYaw;
        p_147046_5_.prevRotationYawHead = p_147046_5_.rotationYaw;
        GL11.glTranslatef(0.0F, p_147046_5_.yOffset, 0.0F);
        if (instance != null) {
            instance.playerViewY = 180.0F;
            instance.func_147940_a(p_147046_5_, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F);
        }
        p_147046_5_.renderYawOffset = var6;
        p_147046_5_.rotationYaw = var7;
        p_147046_5_.rotationPitch = var8;
        p_147046_5_.prevRotationYawHead = var9;
        p_147046_5_.rotationYawHead = var10;
        GL11.glPopMatrix();
        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
    }

    protected void actionPerformed(GuiButton p_146284_1_) {
        EntityClientPlayerMP thePlayer = this.mc.thePlayer;

        if (thePlayer == null)
            return;
        if (p_146284_1_.id == 0)
            this.mc.displayGuiScreen(new GuiAchievements(this.mc, this, thePlayer.getStatFileWriter()));

        if (p_146284_1_.id == 1)
            this.mc.displayGuiScreen(new GuiStats(this.mc, this, thePlayer.getStatFileWriter()));
    }
}
