package gg.mineral.bot.plugin.impl.player;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;

import org.bukkit.event.player.PlayerInitialSpawnEvent;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import gg.mineral.bot.api.entity.living.player.ServerPlayer;
import gg.mineral.bot.api.world.ServerWorld;
import gg.mineral.bot.impl.config.BotGlobalConfig;
import lombok.val;
import net.minecraft.server.v1_8_R3.Block;
import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.LocaleI18n;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import net.minecraft.server.v1_8_R3.PlayerInteractManager;
import net.minecraft.server.v1_8_R3.ScoreboardServer;
import net.minecraft.server.v1_8_R3.World;
import net.minecraft.server.v1_8_R3.WorldServer;

public class NMSServerPlayer extends EntityPlayer implements ServerPlayer {

    public NMSServerPlayer(ServerWorld<?> world, UUID uuid, String name, String... skinData) {
        this(getHandle(world.getHandle()), new GameProfile(uuid, name), skinData);
    }

    public static World getHandle(Object handle) {
        if (handle instanceof World)
            return (World) handle;
        if (handle instanceof CraftWorld cw)
            return cw.getHandle();
        throw new IllegalArgumentException("Invalid world type: " + handle.getClass().getName());
    }

    public NMSServerPlayer(World world, GameProfile gameProfile, String... skinData) {
        super(MinecraftServer.getServer(), (WorldServer) world, gameProfile,
                new PlayerInteractManager(world));

        if (skinData.length == 2)
            setSkin(gameProfile, skinData);
        MinecraftServer.getServer().getUserCache().a(gameProfile);
    }

    public void setSkin(GameProfile gameProfile, String[] textureData) {
        gameProfile.getProperties().put("textures", new Property("textures", textureData[0], textureData[1]));
    }

    @Override
    public void t_() {
        super.t_();
        this.playerConnection.c(); // Keep Alive
    }

    @Override
    protected void bL() {
        if (!BotGlobalConfig.isDisableEntityCollisions())
            super.bL();
    }

    @Override
    protected void a(BlockPosition blockposition, Block block) {
        if (!BotGlobalConfig.isDisableStepSounds())
            super.a(blockposition, block);
    }

    @Override
    public void sendSupportedChannels() {
        getBukkitEntity().sendSupportedChannels();
    }

    @Override
    public int getItemInHandIndex() {
        return this.inventory.itemInHandIndex;
    }

    @Override
    public void spawnInWorld(ServerWorld<?> world) {
        this.world = getHandle(world.getHandle());
        this.dimension = ((WorldServer) this.world).dimension;
        this.spawnWorld = this.world.worldData.getName();
        this.spawnIn(this.world);
        this.playerInteractManager.a((WorldServer) this.world);
    }

    @Override
    public void onJoin() {
        MinecraftServer.getServer().getPlayerList().onPlayerJoin(this,
                "\u00A7e" + LocaleI18n.a("multiplayer.player.joined", this.getName()));
    }

    @Override
    public int getGameModeId() {
        return this.playerInteractManager.getGameMode().getId();
    }

    @Override
    public void initializeGameMode() {
        MinecraftServer.getServer().getPlayerList().a(this, null, getWorld());
    }

    @Override
    public boolean isWorldHardcore() {
        return getWorld().getWorldData().isHardcore();
    }

    @Override
    public int getDimensionId() {
        return getWorld().getDifficulty().a();
    }

    @Override
    public int getDifficultyId() {
        return getWorld().worldProvider.getDimension();
    }

    @Override
    public int getMaxPlayers() {
        return MinecraftServer.getServer().getPlayerList().getMaxPlayers();
    }

    @Override
    public String getWorldTypeName() {
        return getWorld().getWorldData().getType().name();
    }

    @Override
    public boolean isReducedDebugInfo() {
        return getWorld().getGameRules().getBoolean("reducedDebugInfo");
    }

    @Override
    public String getServerModName() {
        return MinecraftServer.getServer().getServerModName();
    }

    @Override
    public void sendScoreboard() {
        MinecraftServer.getServer().getPlayerList().sendScoreboard((ScoreboardServer) getWorld().getScoreboard(),
                this);
    }

    @Override
    public void resetPlayerSampleUpdateTimer() {
        MinecraftServer.getServer().aH();
    }

    @Override
    public boolean isDifficultyLocked() {
        return getWorld().getWorldData().isDifficultyLocked();
    }

    @Override
    public int[] getWorldSpawn() {
        return new int[] { getWorld().getWorldData().c(),
                getWorld().getWorldData().d(),
                getWorld().getWorldData().e() };
    }

    @Override
    public void sendLocationToClient() {
        this.playerConnection.a(this.locX, this.locY, this.locZ, this.yaw,
                this.pitch);
    }

    @Override
    public void initWorld() {
        MinecraftServer.getServer().getPlayerList().b(this, this.u());
    }

    /*
     * @Override
     * public Stream<Consumer<IClientboundPacketHandler>> getEffectPackets() {
     * return getEffects().stream()
     * .map(effect -> l -> l.entityEffect(this.getId(), (byte) effect.getEffectId(),
     * (byte) effect.getAmplifier(), effect.getDuration(),
     * effect.isShowParticles()));
     * }
     */

    @Override
    public void initResourcePack() {
        val server = MinecraftServer.getServer();
        val resourcePack = server.getResourcePack();
        if (resourcePack.length() > 0)
            setResourcePack(resourcePack,
                    server.getResourcePackHash());
    }

    @Override
    public void callSpawnEvents() {
        val bukkitPlayer = getBukkitEntity();
        Bukkit.getPluginManager().callEvent(new PlayerInitialSpawnEvent(
                bukkitPlayer, bukkitPlayer.getLocation()));
        Bukkit.getPluginManager().callEvent(new PlayerSpawnLocationEvent(bukkitPlayer, bukkitPlayer.getLocation()));
    }
}
