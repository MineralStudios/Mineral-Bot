package net.minecraft.client.renderer.entity;

import gg.mineral.bot.lwjgl.opengl.GL11;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.Getter;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.model.*;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.tileentity.RenderEnderCrystal;
import net.minecraft.client.renderer.tileentity.RenderItemFrame;
import net.minecraft.client.renderer.tileentity.RenderWitherSkull;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLeashKnot;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityMinecartMobSpawner;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.item.*;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.*;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ReportedException;
import net.minecraft.world.World;

import java.util.Iterator;
import java.util.Map;

public class RenderManager {
    /**
     * A map of entity classes and the associated renderer.
     */
    private final Map<Class<? extends Entity>, Render> entityRenderMap = new Object2ObjectOpenHashMap<>();

    /**
     * Renders fonts
     */
    private FontRenderer fontRenderer;
    public static double renderPosX;
    public static double renderPosY;
    public static double renderPosZ;
    public TextureManager renderEngine;
    public ItemRenderer itemRenderer;

    /**
     * Reference to the World object.
     */
    public World worldObj;

    /**
     * Rendermanager's variable for the player
     */
    public EntityLivingBase livingPlayer;
    public Entity field_147941_i;
    public float playerViewY;
    public float playerViewX;

    /**
     * Reference to the GameSettings object.
     */
    public GameSettings options;
    public double viewerPosX;
    public double viewerPosY;
    public double viewerPosZ;
    public static boolean field_85095_o;
    @Getter
    private final Minecraft mc;

    public RenderManager(Minecraft mc) {
        this.mc = mc;
        this.entityRenderMap.put(EntityCaveSpider.class, new RenderCaveSpider(mc));
        this.entityRenderMap.put(EntitySpider.class, new RenderSpider(mc));
        this.entityRenderMap.put(EntityPig.class, new RenderPig(mc, new ModelPig(mc), new ModelPig(mc, 0.5F), 0.7F));
        this.entityRenderMap.put(EntitySheep.class,
                new RenderSheep(mc, new ModelSheep2(mc), new ModelSheep1(mc), 0.7F));
        this.entityRenderMap.put(EntityCow.class, new RenderCow(mc, new ModelCow(mc), 0.7F));
        this.entityRenderMap.put(EntityMooshroom.class, new RenderMooshroom(mc, new ModelCow(mc), 0.7F));
        this.entityRenderMap.put(EntityWolf.class, new RenderWolf(mc, new ModelWolf(mc), new ModelWolf(mc), 0.5F));
        this.entityRenderMap.put(EntityChicken.class, new RenderChicken(mc, new ModelChicken(mc), 0.3F));
        this.entityRenderMap.put(EntityOcelot.class, new RenderOcelot(mc, new ModelOcelot(mc), 0.4F));
        this.entityRenderMap.put(EntitySilverfish.class, new RenderSilverfish(mc));
        this.entityRenderMap.put(EntityCreeper.class, new RenderCreeper(mc));
        this.entityRenderMap.put(EntityEnderman.class, new RenderEnderman(mc));
        this.entityRenderMap.put(EntitySnowman.class, new RenderSnowMan(mc));
        this.entityRenderMap.put(EntitySkeleton.class, new RenderSkeleton(mc));
        this.entityRenderMap.put(EntityWitch.class, new RenderWitch(mc));
        this.entityRenderMap.put(EntityBlaze.class, new RenderBlaze(mc));
        this.entityRenderMap.put(EntityZombie.class, new RenderZombie(mc));
        this.entityRenderMap.put(EntitySlime.class,
                new RenderSlime(mc, new ModelSlime(mc, 16), new ModelSlime(mc, 0), 0.25F));
        this.entityRenderMap.put(EntityMagmaCube.class, new RenderMagmaCube(mc));
        this.entityRenderMap.put(EntityPlayer.class, new RenderPlayer(mc));
        this.entityRenderMap.put(EntityGiantZombie.class, new RenderGiantZombie(mc, new ModelZombie(mc), 0.5F, 6.0F));
        this.entityRenderMap.put(EntityGhast.class, new RenderGhast(mc));
        this.entityRenderMap.put(EntitySquid.class, new RenderSquid(mc, new ModelSquid(mc), 0.7F));
        this.entityRenderMap.put(EntityVillager.class, new RenderVillager(mc));
        this.entityRenderMap.put(EntityIronGolem.class, new RenderIronGolem(mc));
        this.entityRenderMap.put(EntityBat.class, new RenderBat(mc));
        this.entityRenderMap.put(EntityDragon.class, new RenderDragon(mc));
        this.entityRenderMap.put(EntityEnderCrystal.class, new RenderEnderCrystal(mc));
        this.entityRenderMap.put(EntityWither.class, new RenderWither(mc));
        this.entityRenderMap.put(Entity.class, new RenderEntity(mc));
        this.entityRenderMap.put(EntityPainting.class, new RenderPainting(mc));
        this.entityRenderMap.put(EntityItemFrame.class, new RenderItemFrame(mc));
        this.entityRenderMap.put(EntityLeashKnot.class, new RenderLeashKnot(mc));
        this.entityRenderMap.put(EntityArrow.class, new RenderArrow(mc));
        this.entityRenderMap.put(EntitySnowball.class, new RenderSnowball(mc, Items.snowball));
        this.entityRenderMap.put(EntityEnderPearl.class, new RenderSnowball(mc, Items.ender_pearl));
        this.entityRenderMap.put(EntityEnderEye.class, new RenderSnowball(mc, Items.ender_eye));
        this.entityRenderMap.put(EntityEgg.class, new RenderSnowball(mc, Items.egg));
        this.entityRenderMap.put(EntityPotion.class, new RenderSnowball(mc, Items.potionitem, 16384));
        this.entityRenderMap.put(EntityExpBottle.class, new RenderSnowball(mc, Items.experience_bottle));
        this.entityRenderMap.put(EntityFireworkRocket.class, new RenderSnowball(mc, Items.fireworks));
        this.entityRenderMap.put(EntityLargeFireball.class, new RenderFireball(mc, 2.0F));
        this.entityRenderMap.put(EntitySmallFireball.class, new RenderFireball(mc, 0.5F));
        this.entityRenderMap.put(EntityWitherSkull.class, new RenderWitherSkull(mc));
        this.entityRenderMap.put(EntityItem.class, new RenderItem(mc));
        this.entityRenderMap.put(EntityXPOrb.class, new RenderXPOrb(mc));
        this.entityRenderMap.put(EntityTNTPrimed.class, new RenderTNTPrimed(mc));
        this.entityRenderMap.put(EntityFallingBlock.class, new RenderFallingBlock(mc));
        this.entityRenderMap.put(EntityMinecartTNT.class, new RenderTntMinecart(mc));
        this.entityRenderMap.put(EntityMinecartMobSpawner.class, new RenderMinecartMobSpawner(mc));
        this.entityRenderMap.put(EntityMinecart.class, new RenderMinecart(mc));
        this.entityRenderMap.put(EntityBoat.class, new RenderBoat(mc));
        this.entityRenderMap.put(EntityFishHook.class, new RenderFish(mc));
        this.entityRenderMap.put(EntityHorse.class, new RenderHorse(mc, new ModelHorse(mc), 0.75F));
        this.entityRenderMap.put(EntityLightningBolt.class, new RenderLightningBolt(mc));
        Iterator var1 = this.entityRenderMap.values().iterator();

        while (var1.hasNext()) {
            Render var2 = (Render) var1.next();
            var2.setRenderManager(this);
        }
    }

    public Render getEntityClassRenderObject(Class p_78715_1_) {
        Render var2 = this.entityRenderMap.get(p_78715_1_);

        if (var2 == null && p_78715_1_ != Entity.class) {
            var2 = this.getEntityClassRenderObject(p_78715_1_.getSuperclass());
            this.entityRenderMap.put(p_78715_1_, var2);
        }

        return var2;
    }

    public Render getEntityRenderObject(Entity p_78713_1_) {
        return this.getEntityClassRenderObject(p_78713_1_.getClass());
    }

    public void func_147938_a(World p_147938_1_, TextureManager p_147938_2_, FontRenderer p_147938_3_,
                              EntityLivingBase p_147938_4_, Entity p_147938_5_, GameSettings p_147938_6_, float p_147938_7_) {
        this.worldObj = p_147938_1_;
        this.renderEngine = p_147938_2_;
        this.options = p_147938_6_;
        this.livingPlayer = p_147938_4_;
        this.field_147941_i = p_147938_5_;
        this.fontRenderer = p_147938_3_;

        if (p_147938_4_.isPlayerSleeping()) {
            Block var8 = p_147938_1_.getBlock(MathHelper.floor_double(p_147938_4_.posX),
                    MathHelper.floor_double(p_147938_4_.posY), MathHelper.floor_double(p_147938_4_.posZ));

            if (var8 == Blocks.bed) {
                int var9 = p_147938_1_.getBlockMetadata(MathHelper.floor_double(p_147938_4_.posX),
                        MathHelper.floor_double(p_147938_4_.posY), MathHelper.floor_double(p_147938_4_.posZ));
                int var10 = var9 & 3;
                this.playerViewY = (float) (var10 * 90 + 180);
                this.playerViewX = 0.0F;
            }
        } else {
            this.playerViewY = p_147938_4_.prevRotationYaw
                    + (p_147938_4_.rotationYaw - p_147938_4_.prevRotationYaw) * p_147938_7_;
            this.playerViewX = p_147938_4_.prevRotationPitch
                    + (p_147938_4_.rotationPitch - p_147938_4_.prevRotationPitch) * p_147938_7_;
        }

        if (p_147938_6_.thirdPersonView == 2) {
            this.playerViewY += 180.0F;
        }

        this.viewerPosX = p_147938_4_.lastTickPosX
                + (p_147938_4_.posX - p_147938_4_.lastTickPosX) * (double) p_147938_7_;
        this.viewerPosY = p_147938_4_.lastTickPosY
                + (p_147938_4_.posY - p_147938_4_.lastTickPosY) * (double) p_147938_7_;
        this.viewerPosZ = p_147938_4_.lastTickPosZ
                + (p_147938_4_.posZ - p_147938_4_.lastTickPosZ) * (double) p_147938_7_;
    }

    public boolean func_147937_a(Entity p_147937_1_, float p_147937_2_) {
        return this.func_147936_a(p_147937_1_, p_147937_2_, false);
    }

    public boolean func_147936_a(Entity p_147936_1_, float p_147936_2_, boolean p_147936_3_) {
        if (p_147936_1_.ticksExisted == 0) {
            p_147936_1_.lastTickPosX = p_147936_1_.posX;
            p_147936_1_.lastTickPosY = p_147936_1_.posY;
            p_147936_1_.lastTickPosZ = p_147936_1_.posZ;
        }

        double var4 = p_147936_1_.lastTickPosX + (p_147936_1_.posX - p_147936_1_.lastTickPosX) * (double) p_147936_2_;
        double var6 = p_147936_1_.lastTickPosY + (p_147936_1_.posY - p_147936_1_.lastTickPosY) * (double) p_147936_2_;
        double var8 = p_147936_1_.lastTickPosZ + (p_147936_1_.posZ - p_147936_1_.lastTickPosZ) * (double) p_147936_2_;
        float var10 = p_147936_1_.prevRotationYaw
                + (p_147936_1_.rotationYaw - p_147936_1_.prevRotationYaw) * p_147936_2_;
        int var11 = p_147936_1_.getBrightnessForRender(p_147936_2_);

        if (p_147936_1_.isBurning()) {
            var11 = 15728880;
        }

        int var12 = var11 % 65536;
        int var13 = var11 / 65536;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float) var12, (float) var13);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        return this.func_147939_a(p_147936_1_, var4 - renderPosX, var6 - renderPosY, var8 - renderPosZ, var10,
                p_147936_2_, p_147936_3_);
    }

    public boolean func_147940_a(Entity p_147940_1_, double p_147940_2_, double p_147940_4_, double p_147940_6_,
                                 float p_147940_8_, float p_147940_9_) {
        return this.func_147939_a(p_147940_1_, p_147940_2_, p_147940_4_, p_147940_6_, p_147940_8_, p_147940_9_, false);
    }

    public boolean func_147939_a(Entity p_147939_1_, double p_147939_2_, double p_147939_4_, double p_147939_6_,
                                 float p_147939_8_, float p_147939_9_, boolean p_147939_10_) {
        Render var11 = null;

        try {
            var11 = this.getEntityRenderObject(p_147939_1_);

            if (var11 != null && this.renderEngine != null) {
                if (!var11.func_147905_a() || p_147939_10_) {
                    try {
                        var11.doRender(p_147939_1_, p_147939_2_, p_147939_4_, p_147939_6_, p_147939_8_, p_147939_9_);
                    } catch (Throwable var18) {
                        throw new ReportedException(CrashReport.makeCrashReport(var18, "Rendering entity in world"));
                    }

                    try {
                        var11.doRenderShadowAndFire(p_147939_1_, p_147939_2_, p_147939_4_, p_147939_6_, p_147939_8_,
                                p_147939_9_);
                    } catch (Throwable var17) {
                        throw new ReportedException(
                                CrashReport.makeCrashReport(var17, "Post-rendering entity in world"));
                    }

                    if (field_85095_o && !p_147939_1_.isInvisible() && !p_147939_10_) {
                        try {
                            this.func_85094_b(p_147939_1_, p_147939_2_, p_147939_4_, p_147939_6_, p_147939_8_,
                                    p_147939_9_);
                        } catch (Throwable var16) {
                            throw new ReportedException(
                                    CrashReport.makeCrashReport(var16, "Rendering entity hitbox in world"));
                        }
                    }
                }
            } else return this.renderEngine == null;

            return true;
        } catch (Throwable var19) {
            CrashReport var13 = CrashReport.makeCrashReport(var19, "Rendering entity in world");
            CrashReportCategory var14 = var13.makeCategory("Entity being rendered");
            p_147939_1_.addEntityCrashInfo(var14);
            CrashReportCategory var15 = var13.makeCategory("Renderer details");
            var15.addCrashSection("Assigned renderer", var11);
            var15.addCrashSection("Location", CrashReportCategory.func_85074_a(p_147939_2_, p_147939_4_, p_147939_6_));
            var15.addCrashSection("Rotation", Float.valueOf(p_147939_8_));
            var15.addCrashSection("Delta", Float.valueOf(p_147939_9_));
            throw new ReportedException(var13);
        }
    }

    private void func_85094_b(Entity p_85094_1_, double p_85094_2_, double p_85094_4_, double p_85094_6_,
                              float p_85094_8_, float p_85094_9_) {
        GL11.glDepthMask(false);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glDisable(GL11.GL_BLEND);
        float var10 = p_85094_1_.width / 2.0F;
        AxisAlignedBB var11 = AxisAlignedBB.getBoundingBox(p_85094_2_ - (double) var10, p_85094_4_,
                p_85094_6_ - (double) var10, p_85094_2_ + (double) var10, p_85094_4_ + (double) p_85094_1_.height,
                p_85094_6_ + (double) var10);
        RenderGlobal.drawOutlinedBoundingBox(this.mc, var11, 16777215);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glDepthMask(true);
    }

    /**
     * World sets this RenderManager's worldObj to the world provided
     */
    public void set(World p_78717_1_) {
        this.worldObj = p_78717_1_;
    }

    public double getDistanceToCamera(double p_78714_1_, double p_78714_3_, double p_78714_5_) {
        double var7 = p_78714_1_ - this.viewerPosX;
        double var9 = p_78714_3_ - this.viewerPosY;
        double var11 = p_78714_5_ - this.viewerPosZ;
        return var7 * var7 + var9 * var9 + var11 * var11;
    }

    /**
     * Returns the font renderer
     */
    public FontRenderer getFontRenderer() {
        return this.fontRenderer;
    }

    public void updateIcons(IIconRegister p_94178_1_) {
        Iterator var2 = this.entityRenderMap.values().iterator();

        while (var2.hasNext()) {
            Render var3 = (Render) var2.next();
            var3.updateIcons(p_94178_1_);
        }
    }
}
