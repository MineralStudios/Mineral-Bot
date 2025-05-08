package gg.mineral.bot.bukkit.plugin.compat.v1_8_R3

import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerAbilities
import com.mojang.authlib.GameProfile
import com.mojang.authlib.properties.Property
import gg.mineral.bot.api.world.ServerWorld
import gg.mineral.bot.bukkit.plugin.impl.player.BukkitServerPlayer
import net.minecraft.server.v1_8_R3.*
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer
import java.util.*

class NMSServerPlayer(
    world: ServerWorld<*>,
    uuid: UUID,
    name: String,
    vararg skinData: String,
    disableEntityCollisions: Boolean = true
) :
    BukkitServerPlayer<EntityPlayer>() {
    private val gameProfile: GameProfile = GameProfile(uuid, name)
    private val worldHandle = getHandle(
        world.handle!!
    )
    override val abilities: WrapperPlayServerPlayerAbilities
        get() = run {
            val abilities = entityPlayer.abilities
            return WrapperPlayServerPlayerAbilities(
                abilities.isInvulnerable,
                abilities.isFlying,
                abilities.canFly,
                bukkitPlayer.gameMode == org.bukkit.GameMode.CREATIVE,
                abilities.flySpeed,
                abilities.walkSpeed
            )
        }

    override val entityPlayer: EntityPlayer = object : EntityPlayer(
        MinecraftServer.getServer(), worldHandle as WorldServer, gameProfile,
        PlayerInteractManager(worldHandle)
    ) {
        override fun bL() {
            if (!disableEntityCollisions) super.bL()
        }

        override fun a(blockposition: BlockPosition, block: Block) {
            if (!disableEntityCollisions) super.a(blockposition, block)
        }
    }

    override var playerConnection: Any
        set(value) {
            entityPlayer.playerConnection = value as PlayerConnection?
        }
        get() {
            return entityPlayer.playerConnection
        }
    override val bukkitPlayer: CraftPlayer = entityPlayer.bukkitEntity

    override fun syncInventory() {
        entityPlayer.syncInventory()
    }

    init {
        if (skinData.size == 2) setSkin(gameProfile, skinData)
        MinecraftServer.getServer().userCache.a(gameProfile)
    }

    override fun sendSupportedChannels() {
        bukkitPlayer.sendSupportedChannels()
    }

    private fun setSkin(gameProfile: GameProfile, textureData: Array<out String>) {
        gameProfile.properties.put(
            "textures", Property(
                "textures",
                textureData[0], textureData[1]
            )
        )
    }

    override fun setPosition(x: Double, y: Double, z: Double) {
        entityPlayer.setPosition(x, y, z)
    }

    override fun setResourcePack(resourcePack: String, resourcePackHash: String) {
        entityPlayer.setResourcePack(
            resourcePack,
            resourcePackHash
        )
    }

    override fun setYawPitch(yaw: Float, pitch: Float) {
        entityPlayer.setYawPitch(yaw, pitch)
    }

    override val itemInHandIndex: Int
        get() = entityPlayer.inventory.itemInHandIndex

    override fun spawnInWorld(world: ServerWorld<*>) {
        entityPlayer.world = getHandle(world.handle!!)
        entityPlayer.dimension = (entityPlayer.world as WorldServer).dimension
        entityPlayer.spawnWorld = entityPlayer.world.worldData.name
        entityPlayer.spawnIn(entityPlayer.world)
        entityPlayer.playerInteractManager.a(entityPlayer.world as WorldServer)
    }

    override fun onJoin() {
        MinecraftServer.getServer().playerList.onPlayerJoin(
            entityPlayer,
            "§e" + LocaleI18n.a("multiplayer.player.joined", this.getName())
        )
    }

    override val gameModeId: Int
        get() = entityPlayer.playerInteractManager.gameMode.id

    override fun initializeGameMode() {
        MinecraftServer.getServer().playerList.a(entityPlayer, null, entityPlayer.getWorld())
    }

    override val isWorldHardcore: Boolean
        get() = entityPlayer.getWorld().getWorldData().isHardcore

    override val dimensionId: Int
        get() = entityPlayer.getWorld().worldProvider.dimension

    override val difficultyId: Int
        get() = entityPlayer.getWorld().difficulty.a()

    override val maxPlayers: Int
        get() = MinecraftServer.getServer().playerList.maxPlayers

    override val worldTypeName: String
        get() = entityPlayer.getWorld().getWorldData().type.name()

    override val isReducedDebugInfo: Boolean
        get() = entityPlayer.getWorld().gameRules.getBoolean("reducedDebugInfo")

    override val sprinting: Boolean
        get() = entityPlayer.isSprinting

    override val serverModName: String
        get() = MinecraftServer.getServer().serverModName

    override fun sendScoreboard() {
        MinecraftServer.getServer().playerList.sendScoreboard(
            entityPlayer.getWorld().getScoreboard() as ScoreboardServer,
            entityPlayer
        )
    }

    override fun resetPlayerSampleUpdateTimer() {
        MinecraftServer.getServer().aH()
    }

    override val isDifficultyLocked: Boolean
        get() = entityPlayer.getWorld().getWorldData().isDifficultyLocked

    override val worldSpawn: IntArray
        get() = intArrayOf(
            entityPlayer.getWorld().getWorldData().c(),
            entityPlayer.getWorld().getWorldData().d(),
            entityPlayer.getWorld().getWorldData().e()
        )

    override fun sendLocationToClient() {
        entityPlayer.playerConnection.a(
            entityPlayer.locX, entityPlayer.locY, entityPlayer.locZ, entityPlayer.yaw,
            entityPlayer.pitch
        )
    }

    override fun initWorld() {
        MinecraftServer.getServer().playerList.b(entityPlayer, entityPlayer.u())
    }

    override fun initResourcePack() {
        val server = MinecraftServer.getServer()
        val resourcePack = server.resourcePack
        if (resourcePack.isNotEmpty()) setResourcePack(
            resourcePack,
            server.resourcePackHash
        )
    }

    override fun getId(): Int {
        return entityPlayer.id
    }

    override fun getName(): String {
        return entityPlayer.name
    }

    companion object {
        fun getHandle(handle: Any): World {
            if (handle is World) return handle
            if (handle is CraftWorld) return handle.handle
            throw IllegalArgumentException("Invalid world type: " + handle.javaClass.name)
        }
    }
}
