package net.minecraft.client.entity;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import gg.mineral.bot.api.entity.living.player.ClientPlayer;
import gg.mineral.bot.api.inv.Inventory;
import gg.mineral.bot.api.inv.InventoryContainer;
import gg.mineral.bot.api.math.simulation.PlayerMotionSimulator;
import gg.mineral.bot.impl.config.BotGlobalConfig;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ImageBufferDownload;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.SkinManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraft.world.World;
import optifine.CapeUtils;
import optifine.Config;
import optifine.PlayerConfigurations;

import javax.annotation.Nullable;
import java.io.File;

public abstract class AbstractClientPlayer extends EntityPlayer implements SkinManager.SkinAvailableCallback, ClientPlayer {
    public static final ResourceLocation locationStevePng = new ResourceLocation("textures/entity/steve.png");
    private ResourceLocation locationSkin;
    private ResourceLocation locationCape;
    private ResourceLocation locationOfCape = null;
    private String nameClear = null;
    @Getter
    private final Minecraft mc;


    @Override
    public Inventory getInventory() {
        return inventory;
    }

    @Override
    public InventoryContainer getInventoryContainer() {
        return inventoryContainer;
    }

    @Override
    public float getHunger() {
        return this.foodStats.getFoodLevel();
    }

    @Override
    public String getUsername() {
        return this.nameClear;
    }

    public AbstractClientPlayer(Minecraft mc, World p_i45074_1_, GameProfile p_i45074_2_) {
        super(p_i45074_1_, p_i45074_2_);
        this.mc = mc;
        String var3 = this.getCommandSenderName();

        if (!var3.isEmpty()) {
            SkinManager var4 = mc.getSkinManager();
            if (var4 != null)
                var4.func_152790_a(p_i45074_2_, this, true);
        }

        this.nameClear = p_i45074_2_.getName();

        if (this.nameClear != null && !this.nameClear.isEmpty())
            this.nameClear = StringUtils.stripControlCodes(this.nameClear);

        if (BotGlobalConfig.isOptimizedGameLoop())
            return;
        CapeUtils.downloadCape(this);
        PlayerConfigurations.getPlayerConfiguration(this);
    }

    @Override
    public PlayerMotionSimulator getMotionSimulator() {
        return new gg.mineral.bot.base.client.math.simulation.PlayerMotionSimulator(this.mc, this);
    }

    public boolean func_152122_n() {
        return !Config.isShowCapes() ? false : (this.locationOfCape != null ? true : this.locationCape != null);
    }

    public boolean func_152123_o() {
        return this.locationSkin != null;
    }

    public ResourceLocation getLocationSkin() {
        return this.locationSkin == null ? locationStevePng : this.locationSkin;
    }

    public ResourceLocation getLocationCape() {
        return !Config.isShowCapes() ? null : (this.locationOfCape != null ? this.locationOfCape : this.locationCape);
    }

    @Nullable
    public static ThreadDownloadImageData getDownloadImageSkin(Minecraft mc, ResourceLocation par0ResourceLocation,
                                                               String par1Str) {
        TextureManager var2 = mc.getTextureManager();

        if (var2 == null)
            return null;

        Object var3 = var2.getTexture(par0ResourceLocation);

        if (var3 == null) {
            var3 = new ThreadDownloadImageData(mc, (File) null,
                    String.format("http://skins.minecraft.net/MinecraftSkins/%s.png",
                            new Object[]{StringUtils.stripControlCodes(par1Str)}),
                    locationStevePng, new ImageBufferDownload());
            var2.loadTexture(par0ResourceLocation, (ITextureObject) var3);
        }

        return (ThreadDownloadImageData) var3;
    }

    public static ResourceLocation getLocationSkin(String par0Str) {
        return new ResourceLocation("skins/" + StringUtils.stripControlCodes(par0Str));
    }

    public void func_152121_a(Type p_152121_1_, ResourceLocation p_152121_2_) {
        switch (AbstractClientPlayer.SwitchType.field_152630_a[p_152121_1_.ordinal()]) {
            case 1:
                this.locationSkin = p_152121_2_;
                break;

            case 2:
                this.locationCape = p_152121_2_;
        }
    }

    public String getNameClear() {
        return this.nameClear;
    }

    public ResourceLocation getLocationOfCape() {
        return this.locationOfCape;
    }

    public void setLocationOfCape(ResourceLocation locationOfCape) {
        this.locationOfCape = locationOfCape;
    }

    static final class SwitchType {
        static final int[] field_152630_a = new int[Type.values().length];

        static {
            try {
                field_152630_a[Type.SKIN.ordinal()] = 1;
            } catch (NoSuchFieldError var2) {
                ;
            }

            try {
                field_152630_a[Type.CAPE.ordinal()] = 2;
            } catch (NoSuchFieldError var1) {
                ;
            }
        }
    }
}
