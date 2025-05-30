package net.minecraft.client.resources;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.InsecureTextureException;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IImageBuffer;
import net.minecraft.client.renderer.ImageBufferDownload;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class SkinManager {
    public static final ResourceLocation field_152793_a = new ResourceLocation("textures/entity/steve.png");
    private static final ExecutorService field_152794_b = new ThreadPoolExecutor(0, 2, 1L, TimeUnit.MINUTES,
            new LinkedBlockingQueue());
    private final TextureManager field_152795_c;
    private final File field_152796_d;
    @Nullable
    private final MinecraftSessionService authenticationService;
    private final LoadingCache<GameProfile, Map<MinecraftProfileTexture.Type, MinecraftProfileTexture>> field_152798_f;
    private final Minecraft mc;

    public SkinManager(Minecraft mc, TextureManager p_i1044_1_, File p_i1044_2_,
                       @Nullable MinecraftSessionService authenticationService) {
        this.field_152795_c = p_i1044_1_;
        this.field_152796_d = p_i1044_2_;
        this.authenticationService = authenticationService;
        this.mc = mc;
        this.field_152798_f = CacheBuilder.newBuilder().expireAfterAccess(15L, TimeUnit.SECONDS)
                .build(new CacheLoader<>() {

                    @Override
                    public @NonNull Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> load(
                            @NonNull GameProfile gameProfile) {
                        MinecraftSessionService mcSessionService = mc.getAuthenticationService();

                        if (mcSessionService == null)
                            return Maps.newHashMap();

                        return mcSessionService.getTextures(gameProfile, false);
                    }
                });
    }

    public ResourceLocation func_152792_a(MinecraftProfileTexture p_152792_1_, Type p_152792_2_) {
        return this.func_152789_a(p_152792_1_, p_152792_2_, null);
    }

    public ResourceLocation func_152789_a(MinecraftProfileTexture p_152789_1_, final Type p_152789_2_,
                                          final SkinManager.SkinAvailableCallback p_152789_3_) {
        final ResourceLocation var4 = new ResourceLocation("skins/" + p_152789_1_.getHash());
        ITextureObject var5 = this.field_152795_c.getTexture(var4);

        if (var5 != null) {
            if (p_152789_3_ != null) {
                p_152789_3_.func_152121_a(p_152789_2_, var4);
            }
        } else {
            File var6 = new File(this.field_152796_d, p_152789_1_.getHash().substring(0, 2));
            File var7 = new File(var6, p_152789_1_.getHash());
            final ImageBufferDownload var8 = p_152789_2_ == Type.SKIN ? new ImageBufferDownload() : null;
            ThreadDownloadImageData var9 = new ThreadDownloadImageData(this.mc, var7, p_152789_1_.getUrl(),
                    field_152793_a,
                    new IImageBuffer() {

                        public BufferedImage parseUserSkin(BufferedImage p_78432_1_) {
                            if (var8 != null) {
                                p_78432_1_ = var8.parseUserSkin(p_78432_1_);
                            }

                            return p_78432_1_;
                        }

                        public void func_152634_a() {
                            if (var8 != null) {
                                var8.func_152634_a();
                            }

                            if (p_152789_3_ != null) {
                                p_152789_3_.func_152121_a(p_152789_2_, var4);
                            }
                        }
                    });
            this.field_152795_c.loadTexture(var4, var9);
        }

        return var4;
    }

    public void func_152790_a(final GameProfile p_152790_1_, final SkinManager.SkinAvailableCallback p_152790_2_,
                              final boolean p_152790_3_) {

        MinecraftSessionService authenticationService = this.authenticationService;

        if (authenticationService == null)
            return;
        field_152794_b.submit(new Runnable() {

            public void run() {
                final HashMap var1 = Maps.newHashMap();

                try {
                    var1.putAll(authenticationService.getTextures(p_152790_1_, p_152790_3_));
                } catch (InsecureTextureException var3) {
                }

                if (var1.isEmpty()
                        && p_152790_1_.getId().equals(SkinManager.this.mc.getSession().getGameProfile().getId())) {
                    var1.putAll(authenticationService.getTextures(
                            authenticationService.fillProfileProperties(p_152790_1_, false), false));
                }

                SkinManager.this.mc.scheduleOnMainThread(new Runnable() {

                    public void run() {
                        if (var1.containsKey(Type.SKIN)) {
                            SkinManager.this.func_152789_a((MinecraftProfileTexture) var1.get(Type.SKIN), Type.SKIN,
                                    p_152790_2_);
                        }

                        if (var1.containsKey(Type.CAPE)) {
                            SkinManager.this.func_152789_a((MinecraftProfileTexture) var1.get(Type.CAPE), Type.CAPE,
                                    p_152790_2_);
                        }
                    }
                });
            }
        });
    }

    public Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> func_152788_a(GameProfile p_152788_1_) {
        return this.field_152798_f.getUnchecked(p_152788_1_);
    }

    public interface SkinAvailableCallback {
        void func_152121_a(Type p_152121_1_, ResourceLocation p_152121_2_);
    }
}
