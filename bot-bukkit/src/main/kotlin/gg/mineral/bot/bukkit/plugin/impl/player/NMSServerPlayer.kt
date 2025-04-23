package gg.mineral.bot.bukkit.plugin.impl.player

import com.mojang.authlib.GameProfile
import com.mojang.authlib.properties.Property
import gg.mineral.bot.api.entity.living.player.ServerPlayer
import gg.mineral.bot.api.world.ServerWorld
import net.minecraft.server.v1_8_R3.*
import org.bukkit.Bukkit
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld
import org.bukkit.event.player.PlayerInitialSpawnEvent
import org.spigotmc.event.player.PlayerSpawnLocationEvent
import java.util.*

class NMSServerPlayer(
    world: World, gameProfile: GameProfile, vararg skinData: String, private val disableEntityCollisions: Boolean = true
) :
    EntityPlayer(
        MinecraftServer.getServer(), world as WorldServer, gameProfile,
        PlayerInteractManager(world)
    ), ServerPlayer {
    constructor(
        world: ServerWorld<*>,
        uuid: UUID,
        name: String,
        vararg skinData: String,
        disableEntityCollisions: Boolean = true
    ) : this(
        getHandle(
            world.handle!!
        ), GameProfile(uuid, name), *skinData, disableEntityCollisions = disableEntityCollisions
    )

    init {
        if (skinData.size == 2) setSkin(gameProfile, skinData)
        MinecraftServer.getServer().userCache.a(gameProfile)
    }

    private val bukkitPlayer = getBukkitEntity()

    private fun setSkin(gameProfile: GameProfile, textureData: Array<out String>) {
        gameProfile.properties.put(
            "textures", Property(
                "textures",
                textureData[0], textureData[1]
            )
        )
    }

    override fun bL() {
        if (!disableEntityCollisions) super.bL()
    }

    override fun a(blockposition: BlockPosition, block: Block) {
        if (!disableEntityCollisions) super.a(blockposition, block)
    }

    override fun sendSupportedChannels() {
        bukkitPlayer.sendSupportedChannels()
    }

    override val itemInHandIndex: Int
        get() = inventory.itemInHandIndex

    override fun spawnInWorld(world: ServerWorld<*>) {
        this.world = getHandle(world.handle!!)
        this.dimension = (this.world as WorldServer).dimension
        this.spawnWorld = this.world.worldData.name
        this.spawnIn(this.world)
        playerInteractManager.a(this.world as WorldServer)
    }

    override fun onJoin() {
        MinecraftServer.getServer().playerList.onPlayerJoin(
            this,
            "§e" + LocaleI18n.a("multiplayer.player.joined", this.getName())
        )
    }

    override val gameModeId: Int
        get() = playerInteractManager.gameMode.id

    override fun initializeGameMode() {
        MinecraftServer.getServer().playerList.a(this, null, getWorld())
    }

    override val isWorldHardcore: Boolean
        get() = getWorld().getWorldData().isHardcore

    override val dimensionId: Int
        get() = getWorld().worldProvider.dimension

    override val difficultyId: Int
        get() = getWorld().difficulty.a()

    override val maxPlayers: Int
        get() = MinecraftServer.getServer().playerList.maxPlayers

    override val worldTypeName: String
        get() = getWorld().getWorldData().type.name()

    override val isReducedDebugInfo: Boolean
        get() = getWorld().gameRules.getBoolean("reducedDebugInfo")

    override val sprinting: Boolean
        get() = this.isSprinting

    override val serverModName: String
        get() = MinecraftServer.getServer().serverModName

    override fun sendScoreboard() {
        MinecraftServer.getServer().playerList.sendScoreboard(
            getWorld().getScoreboard() as ScoreboardServer,
            this
        )
    }

    override fun resetPlayerSampleUpdateTimer() {
        MinecraftServer.getServer().aH()
    }

    override val isDifficultyLocked: Boolean
        get() = getWorld().getWorldData().isDifficultyLocked

    override val worldSpawn: IntArray
        get() = intArrayOf(
            getWorld().getWorldData().c(),
            getWorld().getWorldData().d(),
            getWorld().getWorldData().e()
        )

    override fun sendLocationToClient() {
        playerConnection.a(
            this.locX, this.locY, this.locZ, this.yaw,
            this.pitch
        )
    }

    override fun initWorld() {
        MinecraftServer.getServer().playerList.b(this, this.u())
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
    override fun initResourcePack() {
        val server = MinecraftServer.getServer()
        val resourcePack = server.resourcePack
        if (resourcePack.isNotEmpty()) setResourcePack(
            resourcePack,
            server.resourcePackHash
        )
    }

    override fun callSpawnEvents() {
        Bukkit.getPluginManager().callEvent(
            PlayerInitialSpawnEvent(
                bukkitPlayer, bukkitPlayer.location
            )
        )
        Bukkit.getPluginManager().callEvent(PlayerSpawnLocationEvent(bukkitPlayer, bukkitPlayer.location))
    }

    companion object {
        fun getHandle(handle: Any): World {
            if (handle is World) return handle
            if (handle is CraftWorld) return handle.handle
            throw IllegalArgumentException("Invalid world type: " + handle.javaClass.name)
        }
    }
}
