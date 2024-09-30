package net.minecraft.client.gui.inventory;

import java.util.Iterator;
import java.util.Set;

import javax.annotation.Nullable;

import gg.mineral.bot.api.screen.type.ContainerScreen;
import gg.mineral.bot.base.lwjgl.opengl.GL11;
import gg.mineral.bot.base.lwjgl.opengl.GL12;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

public abstract class GuiContainer extends GuiScreen implements ContainerScreen {
    protected static final ResourceLocation field_147001_a = new ResourceLocation(
            "textures/gui/container/inventory.png");
    protected int field_146999_f = 176, field_147000_g = 166;
    public Container container;
    protected int xShift, yShift;
    private Slot field_147006_u, field_147005_v;
    private boolean field_147004_w;
    private ItemStack field_147012_x;
    private int field_147011_y, field_147010_z;
    private Slot field_146989_A;
    private long field_146990_B;
    private ItemStack field_146991_C;
    private Slot field_146985_D;
    private long field_146986_E;
    protected final Set<Slot> field_147008_s = new ObjectOpenHashSet<>();
    protected boolean field_147007_t;
    private int field_146987_F, field_146988_G;
    private boolean field_146995_H;
    private int field_146996_I;
    private long field_146997_J;
    private Slot field_146998_K;
    private int field_146992_L;
    private boolean field_146993_M;
    private ItemStack field_146994_N;

    public GuiContainer(Minecraft mc, Container container) {
        super(mc);
        this.container = container;
        this.field_146995_H = true;
    }

    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    public void initGui() {
        super.initGui();
        EntityClientPlayerMP thePlayer = this.mc.thePlayer;

        if (thePlayer != null)
            thePlayer.openContainer = this.container;

        this.xShift = (this.width - this.field_146999_f) / 2;
        this.yShift = (this.height - this.field_147000_g) / 2;
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(int p_73863_1_, int p_73863_2_, float p_73863_3_) {
        this.drawDefaultBackground();
        int var4 = this.xShift;
        int var5 = this.yShift;
        this.func_146976_a(p_73863_3_, p_73863_1_, p_73863_2_);
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        super.drawScreen(p_73863_1_, p_73863_2_, p_73863_3_);
        RenderHelper.enableGUIStandardItemLighting();
        GL11.glPushMatrix();
        GL11.glTranslatef((float) var4, (float) var5, 0.0F);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        this.field_147006_u = null;
        short var6 = 240;
        short var7 = 240;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float) var6 / 1.0F, (float) var7 / 1.0F);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        int var11;

        for (int var8 = 0; var8 < this.container.inventorySlots.size(); ++var8) {
            Slot var9 = (Slot) this.container.inventorySlots.get(var8);
            this.func_146977_a(var9);

            if (this.isMouseOver(var9, p_73863_1_, p_73863_2_) && var9.func_111238_b()) {
                this.field_147006_u = var9;
                GL11.glDisable(GL11.GL_LIGHTING);
                GL11.glDisable(GL11.GL_DEPTH_TEST);
                int var10 = var9.xDisplayPosition;
                var11 = var9.yDisplayPosition;
                GL11.glColorMask(true, true, true, false);
                this.drawGradientRect(var10, var11, var10 + 16, var11 + 16, -2130706433, -2130706433);
                GL11.glColorMask(true, true, true, true);
                GL11.glEnable(GL11.GL_LIGHTING);
                GL11.glEnable(GL11.GL_DEPTH_TEST);
            }
        }

        this.func_146979_b(p_73863_1_, p_73863_2_);
        EntityClientPlayerMP thePlayer = this.mc.thePlayer;
        @Nullable
        InventoryPlayer var15 = thePlayer != null ? thePlayer.inventory : null;
        @Nullable
        ItemStack var16 = this.field_147012_x == null && var15 != null ? var15.getItemStack() : this.field_147012_x;

        if (var16 != null) {
            byte var17 = 8;
            var11 = this.field_147012_x == null ? 8 : 16;
            String var12 = null;

            if (this.field_147012_x != null && this.field_147004_w) {
                var16 = var16.copy();

                if (var16 != null)
                    var16.stackSize = MathHelper.ceiling_float_int((float) var16.stackSize / 2.0F);
            } else if (this.field_147007_t && this.field_147008_s.size() > 1) {
                var16 = var16.copy();
                if (var16 != null)
                    var16.stackSize = this.field_146996_I;

                if (var16 != null && var16.stackSize == 0)
                    var12 = "" + EnumChatFormatting.YELLOW + "0";

            }

            this.func_146982_a(var16, p_73863_1_ - var4 - var17, p_73863_2_ - var5 - var11, var12);
        }

        if (this.field_146991_C != null) {
            float var18 = (float) (Minecraft.getSystemTime() - this.field_146990_B) / 100.0F;

            if (var18 >= 1.0F) {
                var18 = 1.0F;
                this.field_146991_C = null;
            }

            var11 = this.field_146989_A.xDisplayPosition - this.field_147011_y;
            int var20 = this.field_146989_A.yDisplayPosition - this.field_147010_z;
            int var13 = this.field_147011_y + (int) ((float) var11 * var18);
            int var14 = this.field_147010_z + (int) ((float) var20 * var18);
            this.func_146982_a(this.field_146991_C, var13, var14, (String) null);
        }

        GL11.glPopMatrix();

        if (var15 != null && var15.getItemStack() == null && this.field_147006_u != null
                && this.field_147006_u.getHasStack()) {
            ItemStack var19 = this.field_147006_u.getStack();
            this.func_146285_a(var19, p_73863_1_, p_73863_2_);
        }

        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        RenderHelper.enableStandardItemLighting();
    }

    private void func_146982_a(ItemStack p_146982_1_, int p_146982_2_, int p_146982_3_, String p_146982_4_) {
        GL11.glTranslatef(0.0F, 0.0F, 32.0F);
        this.zLevel = 200.0F;
        itemRender.zLevel = 200.0F;
        itemRender.renderItemAndEffectIntoGUI(this.fontRendererObj, this.mc.getTextureManager(), p_146982_1_,
                p_146982_2_, p_146982_3_);
        itemRender.renderItemOverlayIntoGUI(this.fontRendererObj, this.mc.getTextureManager(), p_146982_1_, p_146982_2_,
                p_146982_3_ - (this.field_147012_x == null ? 0 : 8), p_146982_4_);
        this.zLevel = 0.0F;
        itemRender.zLevel = 0.0F;
    }

    protected void func_146979_b(int p_146979_1_, int p_146979_2_) {
    }

    protected abstract void func_146976_a(float p_146976_1_, int p_146976_2_, int p_146976_3_);

    private void func_146977_a(Slot slot) {
        int var2 = slot.xDisplayPosition;
        int var3 = slot.yDisplayPosition;
        ItemStack var4 = slot.getStack();
        boolean var5 = false;
        boolean var6 = slot == this.field_147005_v && this.field_147012_x != null && !this.field_147004_w;
        EntityClientPlayerMP thePlayer = this.mc.thePlayer;
        @Nullable
        ItemStack var7 = thePlayer != null ? thePlayer.inventory.getItemStack() : null;
        String var8 = null;

        if (slot == this.field_147005_v && this.field_147012_x != null && this.field_147004_w && var4 != null) {
            var4 = var4.copy();
            var4.stackSize /= 2;
        } else if (this.field_147007_t && this.field_147008_s.contains(slot) && var7 != null) {
            if (this.field_147008_s.size() == 1)
                return;

            if (Container.func_94527_a(slot, var7, true) && this.container.canDragIntoSlot(slot)) {
                var4 = var7.copy();
                var5 = true;
                Container.func_94525_a(this.field_147008_s, this.field_146987_F, var4,
                        slot.getStack() == null ? 0 : slot.getStack().stackSize);

                if (var4.stackSize > var4.getMaxStackSize()) {
                    var8 = EnumChatFormatting.YELLOW + "" + var4.getMaxStackSize();
                    var4.stackSize = var4.getMaxStackSize();
                }

                if (var4.stackSize > slot.getSlotStackLimit()) {
                    var8 = EnumChatFormatting.YELLOW + "" + slot.getSlotStackLimit();
                    var4.stackSize = slot.getSlotStackLimit();
                }
            } else {
                this.field_147008_s.remove(slot);
                this.func_146980_g();
            }
        }

        this.zLevel = 100.0F;
        itemRender.zLevel = 100.0F;

        if (var4 == null) {
            IIcon var9 = slot.getBackgroundIconIndex();

            if (var9 != null) {
                GL11.glDisable(GL11.GL_LIGHTING);
                TextureManager textureManager = this.mc.getTextureManager();

                if (textureManager != null)
                    textureManager.bindTexture(TextureMap.locationItemsTexture);

                this.drawTexturedModelRectFromIcon(var2, var3, var9, 16, 16);
                GL11.glEnable(GL11.GL_LIGHTING);
                var6 = true;
            }
        }

        if (!var6) {
            if (var5)
                drawRect(this.mc, var2, var3, var2 + 16, var3 + 16, -2130706433);

            GL11.glEnable(GL11.GL_DEPTH_TEST);
            itemRender.renderItemAndEffectIntoGUI(this.fontRendererObj, this.mc.getTextureManager(), var4, var2, var3);
            itemRender.renderItemOverlayIntoGUI(this.fontRendererObj, this.mc.getTextureManager(), var4, var2, var3,
                    var8);
        }

        itemRender.zLevel = 0.0F;
        this.zLevel = 0.0F;
    }

    private void func_146980_g() {
        EntityClientPlayerMP thePlayer = this.mc.thePlayer;
        @Nullable
        ItemStack var1 = thePlayer != null ? thePlayer.inventory.getItemStack() : null;

        if (var1 != null && this.field_147007_t) {
            this.field_146996_I = var1.stackSize;
            ItemStack var4;
            int var5;

            for (Iterator var2 = this.field_147008_s.iterator(); var2
                    .hasNext(); this.field_146996_I -= var4.stackSize - var5) {
                Slot var3 = (Slot) var2.next();
                var4 = var1.copy();
                var5 = var3.getStack() == null ? 0 : var3.getStack().stackSize;
                Container.func_94525_a(this.field_147008_s, this.field_146987_F, var4, var5);

                if (var4.stackSize > var4.getMaxStackSize())
                    var4.stackSize = var4.getMaxStackSize();

                if (var4.stackSize > var3.getSlotStackLimit())
                    var4.stackSize = var3.getSlotStackLimit();

            }
        }
    }

    @Nullable
    private Slot getHoveringSlot(int x, int y) {
        for (int index = 0; index < this.container.inventorySlots.size(); ++index) {
            Slot slot = this.container.inventorySlots.get(index);

            if (this.isMouseOver(slot, x, y))
                return slot;
        }

        return null;
    }

    /**
     * Called when the mouse is clicked.
     */
    protected void mouseClicked(int p_73864_1_, int p_73864_2_, int p_73864_3_) {
        super.mouseClicked(p_73864_1_, p_73864_2_, p_73864_3_);
        boolean var4 = p_73864_3_ == this.mc.gameSettings.keyBindPickBlock.getKeyCode() + 100;
        Slot hoveringSlot = this.getHoveringSlot(p_73864_1_, p_73864_2_);
        long var6 = Minecraft.getSystemTime();
        this.field_146993_M = this.field_146998_K == hoveringSlot && var6 - this.field_146997_J < 250L
                && this.field_146992_L == p_73864_3_;
        this.field_146995_H = false;

        if (p_73864_3_ == 0 || p_73864_3_ == 1 || var4) {
            int var8 = this.xShift;
            int var9 = this.yShift;
            boolean var10 = p_73864_1_ < var8 || p_73864_2_ < var9 || p_73864_1_ >= var8 + this.field_146999_f
                    || p_73864_2_ >= var9 + this.field_147000_g;
            int hoveringSlotIndex = -1;

            if (hoveringSlot != null)
                hoveringSlotIndex = hoveringSlot.slotNumber;

            if (var10)
                hoveringSlotIndex = -999;

            EntityClientPlayerMP thePlayer = this.mc.thePlayer;

            if (thePlayer != null && this.mc.gameSettings.touchscreen && var10
                    && thePlayer.inventory.getItemStack() == null) {
                this.mc.displayGuiScreen((GuiScreen) null);
                return;
            }

            if (hoveringSlotIndex != -1) {
                if (this.mc.gameSettings.touchscreen) {
                    if (hoveringSlot != null && hoveringSlot.getHasStack()) {
                        this.field_147005_v = hoveringSlot;
                        this.field_147012_x = null;
                        this.field_147004_w = p_73864_3_ == 1;
                    } else {
                        this.field_147005_v = null;
                    }
                } else if (!this.field_147007_t) {
                    if (thePlayer == null || thePlayer.inventory.getItemStack() == null) {
                        if (p_73864_3_ == this.mc.gameSettings.keyBindPickBlock.getKeyCode() + 100) {
                            this.func_146984_a(hoveringSlot, hoveringSlotIndex, p_73864_3_, 3);
                        } else {
                            boolean var12 = hoveringSlotIndex != -999
                                    && (this.mc.getKeyboard().isKeyDown(42) || this.mc.getKeyboard().isKeyDown(54));
                            byte var13 = 0;

                            if (var12) {
                                this.field_146994_N = hoveringSlot != null && hoveringSlot.getHasStack()
                                        ? hoveringSlot.getStack()
                                        : null;
                                var13 = 1;
                            } else if (hoveringSlotIndex == -999) {
                                var13 = 4;
                            }

                            this.func_146984_a(hoveringSlot, hoveringSlotIndex, p_73864_3_, var13);
                        }

                        this.field_146995_H = true;
                    } else {
                        this.field_147007_t = true;
                        this.field_146988_G = p_73864_3_;
                        this.field_147008_s.clear();

                        if (p_73864_3_ == 0) {
                            this.field_146987_F = 0;
                        } else if (p_73864_3_ == 1) {
                            this.field_146987_F = 1;
                        }
                    }
                }
            }
        }

        this.field_146998_K = hoveringSlot;
        this.field_146997_J = var6;
        this.field_146992_L = p_73864_3_;
    }

    protected void mouseClickMove(int p_146273_1_, int p_146273_2_, int p_146273_3_, long p_146273_4_) {
        Slot var6 = this.getHoveringSlot(p_146273_1_, p_146273_2_);
        EntityClientPlayerMP thePlayer = this.mc.thePlayer;

        ItemStack var7 = thePlayer != null ? thePlayer.inventory.getItemStack() : null;

        if (this.field_147005_v != null && this.mc.gameSettings.touchscreen) {
            if (p_146273_3_ == 0 || p_146273_3_ == 1) {
                if (this.field_147012_x == null) {
                    if (var6 != this.field_147005_v)
                        this.field_147012_x = this.field_147005_v.getStack().copy();

                } else if (this.field_147012_x.stackSize > 1 && var6 != null
                        && Container.func_94527_a(var6, this.field_147012_x, false)) {
                    long var8 = Minecraft.getSystemTime();

                    if (this.field_146985_D == var6) {
                        if (var8 - this.field_146986_E > 500L) {
                            this.func_146984_a(this.field_147005_v, this.field_147005_v.slotNumber, 0, 0);
                            this.func_146984_a(var6, var6.slotNumber, 1, 0);
                            this.func_146984_a(this.field_147005_v, this.field_147005_v.slotNumber, 0, 0);
                            this.field_146986_E = var8 + 750L;
                            --this.field_147012_x.stackSize;
                        }
                    } else {
                        this.field_146985_D = var6;
                        this.field_146986_E = var8;
                    }
                }
            }
        } else if (this.field_147007_t && var6 != null && var7 != null && var7.stackSize > this.field_147008_s.size()
                && Container.func_94527_a(var6, var7, true) && var6.isItemValid(var7)
                && this.container.canDragIntoSlot(var6)) {
            this.field_147008_s.add(var6);
            this.func_146980_g();
        }
    }

    protected void mouseMovedOrUp(int p_146286_1_, int p_146286_2_, int p_146286_3_) {

        EntityClientPlayerMP thePlayer = this.mc.thePlayer;
        Slot var4 = this.getHoveringSlot(p_146286_1_, p_146286_2_);
        int var5 = this.xShift;
        int var6 = this.yShift;
        boolean var7 = p_146286_1_ < var5 || p_146286_2_ < var6 || p_146286_1_ >= var5 + this.field_146999_f
                || p_146286_2_ >= var6 + this.field_147000_g;
        int var8 = -1;

        if (var4 != null)
            var8 = var4.slotNumber;

        if (var7)
            var8 = -999;

        Slot var10;
        Iterator<Slot> var11;

        if (this.field_146993_M && var4 != null && p_146286_3_ == 0
                && this.container.func_94530_a((ItemStack) null, var4)) {
            if (isShiftKeyDown(this.mc)) {
                if (var4 != null && var4.inventory != null && this.field_146994_N != null) {
                    var11 = this.container.inventorySlots.iterator();

                    while (var11.hasNext()) {
                        var10 = var11.next();

                        if (var10 != null && var10.canTakeStack(this.mc.thePlayer) && var10.getHasStack()
                                && var10.inventory == var4.inventory
                                && Container.func_94527_a(var10, this.field_146994_N, true))
                            this.func_146984_a(var10, var10.slotNumber, p_146286_3_, 1);
                    }
                }
            } else {
                this.func_146984_a(var4, var8, p_146286_3_, 6);
            }

            this.field_146993_M = false;
            this.field_146997_J = 0L;
        } else {
            if (this.field_147007_t && this.field_146988_G != p_146286_3_) {
                this.field_147007_t = false;
                this.field_147008_s.clear();
                this.field_146995_H = true;
                return;
            }

            if (this.field_146995_H) {
                this.field_146995_H = false;
                return;
            }

            boolean var9;

            if (this.field_147005_v != null && this.mc.gameSettings.touchscreen) {
                if (p_146286_3_ == 0 || p_146286_3_ == 1) {
                    if (this.field_147012_x == null && var4 != this.field_147005_v) {
                        this.field_147012_x = this.field_147005_v.getStack();
                    }

                    var9 = Container.func_94527_a(var4, this.field_147012_x, false);

                    if (var8 != -1 && this.field_147012_x != null && var9) {
                        this.func_146984_a(this.field_147005_v, this.field_147005_v.slotNumber, p_146286_3_, 0);
                        this.func_146984_a(var4, var8, 0, 0);

                        if (thePlayer != null && thePlayer.inventory.getItemStack() != null) {
                            this.func_146984_a(this.field_147005_v, this.field_147005_v.slotNumber, p_146286_3_, 0);
                            this.field_147011_y = p_146286_1_ - var5;
                            this.field_147010_z = p_146286_2_ - var6;
                            this.field_146989_A = this.field_147005_v;
                            this.field_146991_C = this.field_147012_x;
                            this.field_146990_B = Minecraft.getSystemTime();
                        } else {
                            this.field_146991_C = null;
                        }
                    } else if (this.field_147012_x != null) {
                        this.field_147011_y = p_146286_1_ - var5;
                        this.field_147010_z = p_146286_2_ - var6;
                        this.field_146989_A = this.field_147005_v;
                        this.field_146991_C = this.field_147012_x;
                        this.field_146990_B = Minecraft.getSystemTime();
                    }

                    this.field_147012_x = null;
                    this.field_147005_v = null;
                }
            } else if (this.field_147007_t && !this.field_147008_s.isEmpty()) {
                this.func_146984_a((Slot) null, -999, Container.func_94534_d(0, this.field_146987_F), 5);
                var11 = this.field_147008_s.iterator();

                while (var11.hasNext()) {
                    var10 = var11.next();
                    this.func_146984_a(var10, var10.slotNumber, Container.func_94534_d(1, this.field_146987_F), 5);
                }

                this.func_146984_a((Slot) null, -999, Container.func_94534_d(2, this.field_146987_F), 5);
            } else if (thePlayer != null && thePlayer.inventory.getItemStack() != null) {
                if (p_146286_3_ == this.mc.gameSettings.keyBindPickBlock.getKeyCode() + 100) {
                    this.func_146984_a(var4, var8, p_146286_3_, 3);
                } else {
                    var9 = var8 != -999 && (this.mc.getKeyboard().isKeyDown(42) || this.mc.getKeyboard().isKeyDown(54));

                    if (var9)
                        this.field_146994_N = var4 != null && var4.getHasStack() ? var4.getStack() : null;

                    this.func_146984_a(var4, var8, p_146286_3_, var9 ? 1 : 0);
                }
            }
        }

        if (thePlayer != null && thePlayer.inventory.getItemStack() == null)
            this.field_146997_J = 0L;

        this.field_147007_t = false;
    }

    private boolean isMouseOver(Slot slot, int x, int y) {
        return this.isMouseOver(slot.xDisplayPosition, slot.yDisplayPosition, 16, 16, x,
                y);
    }

    protected boolean isMouseOver(int slotX, int slotY, int width, int height, int x,
            int y) {
        int xShift = this.xShift;
        int yShift = this.yShift;
        x -= xShift;
        y -= yShift;
        return x >= slotX - 1 && x < slotX + width + 1
                && y >= slotY - 1 && y < slotY + height + 1;
    }

    @Override
    public int getSlotX(gg.mineral.bot.api.inv.Slot slot) {
        int xShift = this.xShift;
        int slotWidth = 16;
        int xCenter = slot.getXDisplayPosition() + (slotWidth / 2);
        return xCenter + xShift;
    }

    @Override
    public int getSlotY(gg.mineral.bot.api.inv.Slot slot) {
        int yShift = this.yShift;
        int slotHeight = 16;
        int yCenter = slot.getYDisplayPosition() + (slotHeight / 2);
        return yCenter + yShift;
    }

    protected void func_146984_a(Slot p_146984_1_, int p_146984_2_, int p_146984_3_, int p_146984_4_) {
        if (p_146984_1_ != null)
            p_146984_2_ = p_146984_1_.slotNumber;

        this.mc.playerController.windowClick(this.container.windowId, p_146984_2_, p_146984_3_, p_146984_4_,
                this.mc.thePlayer);
    }

    /**
     * Fired when a key is typed. This is the equivalent of
     * KeyListener.keyTyped(KeyEvent e).
     */
    protected void keyTyped(char p_73869_1_, int p_73869_2_) {
        EntityClientPlayerMP thePlayer = this.mc.thePlayer;
        if (p_73869_2_ == 1 || p_73869_2_ == this.mc.gameSettings.keyBindInventory.getKeyCode())
            if (thePlayer != null)
                thePlayer.closeScreen();

        this.func_146983_a(p_73869_2_);

        if (this.field_147006_u != null && this.field_147006_u.getHasStack()) {
            if (p_73869_2_ == this.mc.gameSettings.keyBindPickBlock.getKeyCode()) {
                this.func_146984_a(this.field_147006_u, this.field_147006_u.slotNumber, 0, 3);
            } else if (p_73869_2_ == this.mc.gameSettings.keyBindDrop.getKeyCode()) {
                this.func_146984_a(this.field_147006_u, this.field_147006_u.slotNumber, isCtrlKeyDown(this.mc) ? 1 : 0,
                        4);
            }
        }
    }

    protected boolean func_146983_a(int p_146983_1_) {
        EntityClientPlayerMP thePlayer = this.mc.thePlayer;

        if (thePlayer != null && thePlayer.inventory.getItemStack() == null && this.field_147006_u != null) {
            for (int var2 = 0; var2 < 9; ++var2) {
                if (p_146983_1_ == this.mc.gameSettings.keyBindsHotbar[var2].getKeyCode()) {
                    this.func_146984_a(this.field_147006_u, this.field_147006_u.slotNumber, var2, 2);
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * "Called when the screen is unloaded. Used to disable keyboard repeat events."
     */
    public void onGuiClosed() {
        if (this.mc.thePlayer != null)
            this.container.onContainerClosed(this.mc.thePlayer);
    }

    /**
     * Returns true if this GUI should pause the game when it is displayed in
     * single-player
     */
    public boolean doesGuiPauseGame() {
        return false;
    }

    /**
     * Called from the main game loop to update the screen.
     */
    public void updateScreen() {
        super.updateScreen();

        EntityClientPlayerMP thePlayer = this.mc.thePlayer;

        if (thePlayer == null)
            return;

        if (!thePlayer.isEntityAlive() || thePlayer.isDead)
            thePlayer.closeScreen();
    }
}
