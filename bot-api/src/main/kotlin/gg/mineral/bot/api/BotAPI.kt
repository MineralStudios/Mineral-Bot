package gg.mineral.bot.api

import gg.mineral.bot.api.collections.OptimizedCollections
import gg.mineral.bot.api.concurrent.ListenableFuture
import gg.mineral.bot.api.configuration.BotConfiguration
import gg.mineral.bot.api.entity.living.player.FakePlayer
import gg.mineral.bot.api.instance.ClientInstance
import gg.mineral.bot.api.math.ServerLocation
import java.lang.ref.WeakReference
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.ExecutorService

abstract class BotAPI {
    abstract fun spawn(
        configuration: BotConfiguration,
        serverIp: String,
        serverPort: Int
    ): ListenableFuture<ClientInstance>

    abstract fun spawn(configuration: BotConfiguration, location: ServerLocation): ListenableFuture<ClientInstance>

    abstract fun despawn(vararg uuids: UUID): BooleanArray

    abstract fun despawn(uuid: UUID): Boolean

    abstract fun despawnAll()

    abstract fun cleanup()

    abstract fun isFakePlayer(uuid: UUID): Boolean

    abstract val fakePlayers: Collection<WeakReference<FakePlayer>>

    @JvmField
    protected val spawnRecords: ConcurrentLinkedQueue<SpawnRecord> = ConcurrentLinkedQueue()

    abstract fun collections(): OptimizedCollections

    abstract val gameLoopExecutor: ExecutorService

    abstract val asyncExecutor: ExecutorService

    @JvmRecord
    data class SpawnRecord(val name: String, val time: Long)

    companion object {
        lateinit var INSTANCE: BotAPI
    }
}
