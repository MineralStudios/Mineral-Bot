package net.minecraft.client.multiplayer;

import gg.mineral.bot.api.entity.ClientEntity;
import gg.mineral.bot.api.world.ClientWorld;
import gg.mineral.bot.impl.config.BotGlobalConfig;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.MovingSoundMinecart;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.particle.EntityFireworkStarterFX;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.profiler.Profiler;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.storage.SaveHandlerMP;

import java.util.Collection;
import java.util.Random;
import java.util.Set;

import static gg.mineral.bot.api.util.dsl.MathDSLKt.highInt;
import static gg.mineral.bot.api.util.dsl.MathDSLKt.lowInt;

public class WorldClient extends World implements ClientWorld {

    public final Minecraft mc;
    private final LongSet previousActiveChunkSet = new LongOpenHashSet();
    /**
     * The packets that need to be sent to the server.
     */
    private final NetHandlerPlayClient sendQueue;
    /**
     * The hash set of entities handled by this client. Uses the entity's ID as the
     * hash set's key.
     */
    private final Int2ObjectOpenHashMap<ClientEntity> entityHashSet = new Int2ObjectOpenHashMap<>();
    /**
     * Contains all entities for this client, both spawned and non-spawned.
     */
    private final Set<Entity> entityList = new ObjectOpenHashSet<>();
    /**
     * Contains all entities for this client that were not spawned due to a
     * non-present chunk. The game will attempt to
     * spawn up to 10 pending entities with each subsequent tick until the spawn
     * queue is empty.
     */
    private final Set<Entity> entitySpawnQueue = new ObjectOpenHashSet<>();
    /**
     * The ChunkProviderClient instance
     */
    ChunkProviderClient clientChunkProvider;

    public WorldClient(Minecraft mc, NetHandlerPlayClient p_i45063_1_, WorldSettings p_i45063_2_, int p_i45063_3_,
                       EnumDifficulty p_i45063_4_, Profiler p_i45063_5_) {
        super(new SaveHandlerMP(), "MpServer", WorldProvider.getProviderForDimension(p_i45063_3_), p_i45063_2_,
                p_i45063_5_);
        this.sendQueue = p_i45063_1_;
        this.mc = mc;
        this.difficultySetting = p_i45063_4_;
        this.setSpawnLocation(8, 64, 8);
        this.mapStorage = p_i45063_1_.mapStorageOrigin;
    }

    /**
     * Runs a single tick for the world
     */
    public void tick() {
        super.tick();
        this.func_82738_a(this.getTotalWorldTime() + 1L);

        if (this.getGameRules().getGameRuleBooleanValue("doDaylightCycle")) {
            this.setWorldTime(this.getWorldTime() + 1L);
        }

        this.theProfiler.startSection("reEntryProcessing");

        for (int var1 = 0; var1 < 10 && !this.entitySpawnQueue.isEmpty(); ++var1) {
            Entity var2 = this.entitySpawnQueue.iterator().next();
            this.entitySpawnQueue.remove(var2);

            if (!this.loadedEntityList.contains(var2)) {
                this.spawnEntityInWorld(var2);
            }
        }

        this.theProfiler.endStartSection("connection");
        this.sendQueue.onNetworkTick();
        this.theProfiler.endStartSection("chunkCache");
        this.clientChunkProvider.unloadQueuedChunks();
        this.theProfiler.endStartSection("blocks");
        this.func_147456_g();
        this.theProfiler.endSection();
    }

    /**
     * Invalidates an AABB region of blocks from the receive queue, in the event
     * that the block has been modified
     * client-side in the intervening 80 receive ticks.
     */
    public void invalidateBlockReceiveRegion(int p_73031_1_, int p_73031_2_, int p_73031_3_, int p_73031_4_,
                                             int p_73031_5_, int p_73031_6_) {
    }

    /**
     * Creates the chunk provider for this world. Called in the constructor.
     * Retrieves provider from worldProvider?
     */
    protected IChunkProvider createChunkProvider() {
        this.clientChunkProvider = new ChunkProviderClient(this);
        return this.clientChunkProvider;
    }

    protected void func_147456_g() {
        if (BotGlobalConfig.optimizedGameLoop)
            return;
        super.func_147456_g();
        this.previousActiveChunkSet.retainAll(this.activeChunkSet);

        if (this.previousActiveChunkSet.size() == this.activeChunkSet.size())
            this.previousActiveChunkSet.clear();

        int var1 = 0;
        LongIterator var2 = this.activeChunkSet.iterator();

        while (var2.hasNext()) {
            long var3 = var2.nextLong();

            if (!this.previousActiveChunkSet.contains(var3)) {
                int chunkXPos = highInt(var3);
                int chunkZPos = lowInt(var3);
                int var4 = chunkXPos * 16;
                int var5 = chunkZPos * 16;
                this.theProfiler.startSection("getChunk");
                Chunk var6 = this.getChunkFromChunkCoords(chunkXPos, chunkZPos);
                this.moodSoundAndCheckLight(var4, var5, var6);
                this.theProfiler.endSection();
                this.previousActiveChunkSet.add(var3);
                ++var1;

                if (var1 >= 10)
                    return;

            }
        }
    }

    public void doPreChunk(int p_73025_1_, int p_73025_2_, boolean p_73025_3_) {
        if (p_73025_3_) {
            this.clientChunkProvider.loadChunk(p_73025_1_, p_73025_2_);
        } else {
            this.clientChunkProvider.unloadChunk(p_73025_1_, p_73025_2_);
        }

        if (!p_73025_3_) {
            this.markBlockRangeForRenderUpdate(p_73025_1_ * 16, 0, p_73025_2_ * 16, p_73025_1_ * 16 + 15, 256,
                    p_73025_2_ * 16 + 15);
        }
    }

    /**
     * Called to place all entities as part of a world
     */
    public boolean spawnEntityInWorld(Entity p_72838_1_) {
        boolean var2 = super.spawnEntityInWorld(p_72838_1_);
        this.entityList.add(p_72838_1_);

        SoundHandler soundHandler = this.mc.getSoundHandler();

        if (!var2)
            this.entitySpawnQueue.add(p_72838_1_);
        else if (p_72838_1_ instanceof EntityMinecart && soundHandler != null)
            soundHandler.playSound(new MovingSoundMinecart((EntityMinecart) p_72838_1_));

        return var2;
    }

    /**
     * Schedule the entity for removal during the next tick. Marks the entity dead
     * in anticipation.
     */
    public void removeEntity(Entity p_72900_1_) {
        super.removeEntity(p_72900_1_);
        this.entityList.remove(p_72900_1_);
    }

    protected void onEntityAdded(Entity p_72923_1_) {
        super.onEntityAdded(p_72923_1_);

        this.entitySpawnQueue.remove(p_72923_1_);
    }

    protected void onEntityRemoved(Entity p_72847_1_) {
        super.onEntityRemoved(p_72847_1_);
        boolean var2 = false;

        if (this.entityList.contains(p_72847_1_)) {
            if (p_72847_1_.isEntityAlive()) {
                this.entitySpawnQueue.add(p_72847_1_);
                var2 = true;
            } else {
                this.entityList.remove(p_72847_1_);
            }
        }

        RenderManager renderManager = this.mc.renderManager;

        if (renderManager != null)
            if (renderManager.getEntityRenderObject(p_72847_1_).func_147905_a() && !var2) {
                RenderGlobal renderGlobal = this.mc.renderGlobal;

                if (renderGlobal != null)
                    renderGlobal.onStaticEntitiesChanged();
            }
    }

    /**
     * Add an ID to Entity mapping to entityHashSet
     */
    public void addEntityToWorld(int p_73027_1_, Entity p_73027_2_) {
        Entity var3 = this.getEntityByID(p_73027_1_);

        if (var3 != null) {
            this.removeEntity(var3);
        }

        this.entityList.add(p_73027_2_);
        p_73027_2_.setEntityId(p_73027_1_);

        if (!this.spawnEntityInWorld(p_73027_2_)) {
            this.entitySpawnQueue.add(p_73027_2_);
        }

        this.entityHashSet.put(p_73027_1_, p_73027_2_);

        RenderManager renderManager = this.mc.renderManager;

        if (renderManager != null)
            if (renderManager.getEntityRenderObject(p_73027_2_).func_147905_a()) {
                RenderGlobal renderGlobal = this.mc.renderGlobal;

                if (renderGlobal != null)
                    renderGlobal.onStaticEntitiesChanged();
            }
    }

    /**
     * Returns the Entity with the given ID, or null if it doesn't exist in this
     * World.
     */
    public Entity getEntityByID(int id) {
        EntityClientPlayerMP thePlayer = this.mc.thePlayer;
        return (thePlayer != null && id == thePlayer.getEntityId() ? thePlayer
                : (Entity) this.entityHashSet.get(id));
    }

    public Entity removeEntityFromWorld(int p_73028_1_) {
        Entity var2 = (Entity) this.entityHashSet.remove(p_73028_1_);

        if (var2 != null) {
            this.entityList.remove(var2);
            this.removeEntity(var2);
        }

        return var2;
    }

    public boolean func_147492_c(int p_147492_1_, int p_147492_2_, int p_147492_3_, Block p_147492_4_,
                                 int p_147492_5_) {
        this.invalidateBlockReceiveRegion(p_147492_1_, p_147492_2_, p_147492_3_, p_147492_1_, p_147492_2_, p_147492_3_);
        return super.setBlock(p_147492_1_, p_147492_2_, p_147492_3_, p_147492_4_, p_147492_5_, 3);
    }

    /**
     * If on MP, sends a quitting packet.
     */
    public void sendQuittingDisconnectingPacket() {
        this.sendQueue.getNetworkManager().closeChannel(new ChatComponentText("Quitting"));
    }

    /**
     * Updates all weather states.
     */
    protected void updateWeather() {
        if (!this.provider.hasNoSky) {
        }
    }

    protected int getViewDistance() {
        return this.mc.gameSettings.renderDistanceChunks;
    }

    public void doVoidFogParticles(int p_73029_1_, int p_73029_2_, int p_73029_3_) {
        byte var4 = 16;
        Random var5 = new Random();

        for (int var6 = 0; var6 < 1000; ++var6) {
            int var7 = p_73029_1_ + this.rand.nextInt(var4) - this.rand.nextInt(var4);
            int var8 = p_73029_2_ + this.rand.nextInt(var4) - this.rand.nextInt(var4);
            int var9 = p_73029_3_ + this.rand.nextInt(var4) - this.rand.nextInt(var4);
            Block var10 = this.getBlock(var7, var8, var9);

            if (var10.getMaterial() == Material.air) {
                if (this.rand.nextInt(8) > var8 && this.provider.getWorldHasVoidParticles()) {
                    this.spawnParticle("depthsuspend", (float) var7 + this.rand.nextFloat(),
                            (float) var8 + this.rand.nextFloat(),
                            (float) var9 + this.rand.nextFloat(), 0.0D, 0.0D, 0.0D);
                }
            } else {
                var10.randomDisplayTick(this, var7, var8, var9, var5);
            }
        }
    }

    /**
     * also releases skins.
     */
    public void removeAllEntities() {
        this.loadedEntityList.removeAll(this.unloadedEntityList);
        int var1;
        Entity var2;
        int var3;
        int var4;

        for (var1 = 0; var1 < this.unloadedEntityList.size(); ++var1) {
            var2 = (Entity) this.unloadedEntityList.get(var1);
            var3 = var2.chunkCoordX;
            var4 = var2.chunkCoordZ;

            if (var2.addedToChunk && this.chunkExists(var3, var4)) {
                this.getChunkFromChunkCoords(var3, var4).removeEntity(var2);
            }
        }

        for (var1 = 0; var1 < this.unloadedEntityList.size(); ++var1) {
            this.onEntityRemoved((Entity) this.unloadedEntityList.get(var1));
        }

        this.unloadedEntityList.clear();

        for (var1 = 0; var1 < this.loadedEntityList.size(); ++var1) {
            var2 = this.loadedEntityList.get(var1);

            if (var2.ridingEntity != null) {
                if (!var2.ridingEntity.isDead && var2.ridingEntity.riddenByEntity == var2) {
                    continue;
                }

                var2.ridingEntity.riddenByEntity = null;
                var2.ridingEntity = null;
            }

            if (var2.isDead) {
                var3 = var2.chunkCoordX;
                var4 = var2.chunkCoordZ;

                if (var2.addedToChunk && this.chunkExists(var3, var4)) {
                    this.getChunkFromChunkCoords(var3, var4).removeEntity(var2);
                }

                this.loadedEntityList.remove(var1--);
                this.onEntityRemoved(var2);
            }
        }
    }

    /**
     * Adds some basic stats of the world to the given crash report.
     */
    public CrashReportCategory addWorldInfoToCrashReport(CrashReport p_72914_1_) {
        CrashReportCategory var2 = super.addWorldInfoToCrashReport(p_72914_1_);
        var2.addCrashSectionCallable("Forced entities",
                () -> WorldClient.this.entityList.size() + " total; " + WorldClient.this.entityList);
        var2.addCrashSectionCallable("Retry entities", () -> WorldClient.this.entitySpawnQueue.size() + " total; "
                + WorldClient.this.entitySpawnQueue);
        var2.addCrashSectionCallable("Server brand", () -> {
            EntityClientPlayerMP thePlayer = WorldClient.this.mc.thePlayer;
            return thePlayer != null ? thePlayer.func_142021_k() : "";
        });
        var2.addCrashSectionCallable("Server type",
                () -> WorldClient.this.mc.getIntegratedServer() == null ? "Non-integrated multiplayer server"
                        : "Integrated singleplayer server");
        return var2;
    }

    /**
     * par8 is loudness, all pars passed to minecraftInstance.sndManager.playSound
     */
    public void playSound(double p_72980_1_, double p_72980_3_, double p_72980_5_, String p_72980_7_, float p_72980_8_,
                          float p_72980_9_, boolean p_72980_10_) {
        double var11 = this.mc.renderViewEntity.getDistanceSq(p_72980_1_, p_72980_3_, p_72980_5_);
        PositionedSoundRecord var13 = new PositionedSoundRecord(new ResourceLocation(p_72980_7_), p_72980_8_,
                p_72980_9_, (float) p_72980_1_, (float) p_72980_3_, (float) p_72980_5_);

        SoundHandler soundHandler = this.mc.getSoundHandler();
        if (soundHandler != null) {
            if (p_72980_10_ && var11 > 100.0D) {
                double var14 = Math.sqrt(var11) / 40.0D;
                soundHandler.playDelayedSound(var13, (int) (var14 * 20.0D));
            } else
                soundHandler.playSound(var13);
        }
    }

    public void makeFireworks(double p_92088_1_, double p_92088_3_, double p_92088_5_, double p_92088_7_,
                              double p_92088_9_, double p_92088_11_, NBTTagCompound p_92088_13_) {
        if (this.mc.effectRenderer != null)
            this.mc.effectRenderer
                    .addEffect(new EntityFireworkStarterFX(this.mc, this, p_92088_1_, p_92088_3_, p_92088_5_,
                            p_92088_7_, p_92088_9_, p_92088_11_, this.mc.effectRenderer, p_92088_13_));
    }

    public void setWorldScoreboard(Scoreboard p_96443_1_) {
        this.worldScoreboard = p_96443_1_;
    }

    /**
     * Sets the world time.
     */
    public void setWorldTime(long p_72877_1_) {
        if (p_72877_1_ < 0L) {
            p_72877_1_ = -p_72877_1_;
            this.getGameRules().setOrCreateGameRule("doDaylightCycle", "false");
        } else {
            this.getGameRules().setOrCreateGameRule("doDaylightCycle", "true");
        }

        super.setWorldTime(p_72877_1_);
    }

    @Override
    public Collection<ClientEntity> getEntities() {
        return entityHashSet.values();
    }

    @Override
    public gg.mineral.bot.api.world.block.Block getBlockAt(int x, int y, int z) {
        return getBlock(x, y, z);
    }

    @Override
    public gg.mineral.bot.api.world.block.Block getBlockAt(double x, double y, double z) {
        return getBlock((int) x, (int) y, (int) z);
    }
}
