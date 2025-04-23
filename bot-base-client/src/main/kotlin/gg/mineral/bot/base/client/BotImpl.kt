package gg.mineral.bot.base.client

import com.google.common.collect.HashMultimap
import gg.mineral.bot.api.BotAPI
import gg.mineral.bot.api.collections.OptimizedCollections
import gg.mineral.bot.api.configuration.BotConfiguration
import gg.mineral.bot.api.entity.living.player.FakePlayer
import gg.mineral.bot.api.instance.ClientInstance
import gg.mineral.bot.api.math.ServerLocation
import gg.mineral.bot.api.message.ChatColor
import gg.mineral.bot.api.util.dsl.onComplete
import gg.mineral.bot.base.client.instance.ConnectedClientInstance
import gg.mineral.bot.base.client.manager.InstanceManager
import gg.mineral.bot.impl.thread.ThreadManager
import it.unimi.dsi.fastutil.objects.Object2IntMap
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
import java.io.File
import java.lang.ref.WeakReference
import java.net.Proxy
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.function.Consumer

abstract class BotImpl : BotAPI() {
    private var lastSpawnInfo: Long = 0

    fun printSpawnInfo() {
        if (spawnRecords.isEmpty() || System.currentTimeMillis() - lastSpawnInfo < 3000) return

        lastSpawnInfo = System.currentTimeMillis()

        val iterator = spawnRecords.iterator()

        var totalTime = 0
        var amount = 0

        val nameCount = Object2IntOpenHashMap<String>()

        while (iterator.hasNext()) {
            val record = iterator.next()
            nameCount.put(record.name, nameCount.getInt(record.name) + 1)
            amount++
            totalTime += record.time.toInt()
            iterator.remove()
        }

        val sb = StringBuilder()

        val newLine = System.lineSeparator()

        val it: Iterator<Object2IntMap.Entry<String>> = nameCount.object2IntEntrySet().iterator()
        while (it.hasNext()) {
            val e = it.next()
            val name = e.key
            val count = e.intValue

            if (it.hasNext()) sb.append(newLine)

            sb.append("• ").append(name).append(" x").append(count)
        }

        println(ChatColor.WHITE + ChatColor.UNDERLINE + "Recent Spawn Info:" + ChatColor.RESET)
        println(ChatColor.WHITE + "Amount: " + ChatColor.CYAN + amount + ChatColor.RESET)
        println(ChatColor.WHITE + "Total Spawn Time: " + ChatColor.CYAN + totalTime + "ms" + ChatColor.RESET)
        println(
            (ChatColor.WHITE + "Average Spawn Time: " + ChatColor.CYAN + (totalTime / amount) + "ms"
                    + ChatColor.RESET)
        )
        println(ChatColor.WHITE + "Names: " + sb.toString() + ChatColor.RESET)
    }

    private fun println(message: String) {
        kotlin.io.println(message)
    }

    override val gameLoopExecutor: ExecutorService
        get() = ThreadManager.gameLoopExecutor

    override val asyncExecutor: ExecutorService
        get() = ThreadManager.asyncExecutor

    override fun collections(): OptimizedCollections {
        return gg.mineral.bot.base.client.collections.OptimizedCollections()
    }

    override fun spawn(
        configuration: BotConfiguration,
        serverIp: String,
        serverPort: Int
    ): WeakReference<ClientInstance> {
        val startTime = System.nanoTime() / 1000000
        val file = configuration.runDirectory

        if (!file.exists()) file.mkdirs()

        val instance = gg.mineral.bot.base.client.instance.ClientInstance(
            configuration, 1280, 720,
            fullscreen = false,
            demo = false,
            file,
            File(file, "assets"),
            File(file, "resourcepacks"),
            Proxy.NO_PROXY,
            "Mineral-Bot-Client", HashMultimap.create<Any?, Any?>(),
            "1.7.10"
        )

        instance.setServer(serverIp, serverPort)
        ThreadManager.gameLoopExecutor.execute {
            instance.run()
            InstanceManager.instances[configuration.uuid] = instance
        }

        spawnRecords.add(SpawnRecord(configuration.username, (System.nanoTime() / 1000000) - startTime))
        return WeakReference(instance)
    }

    override fun despawn(uuid: UUID): Boolean {
        val instance = InstanceManager.instances[uuid]
        val running = instance != null && instance.running
        instance?.shutdown()
        if (instance is ConnectedClientInstance)
            instance.channel.onComplete { it.getOrNull()?.close() }

        return running
    }

    override fun despawn(vararg uuids: UUID): BooleanArray {
        val results = BooleanArray(uuids.size)
        for (i in uuids.indices) results[i] = despawn(uuids[i])
        return results
    }

    override fun isFakePlayer(uuid: UUID): Boolean {
        val instances = InstanceManager.instances
        val pendingInstances = InstanceManager.pendingInstances
        return instances.containsKey(uuid) || pendingInstances.containsKey(uuid)
    }

    override fun despawnAll() {
        val instances = InstanceManager.instances
        instances.values.forEach(Consumer { it.shutdown() })
        instances.clear()
    }

    override val fakePlayers: Collection<WeakReference<FakePlayer>>
        get() {
            val fakePlayers =
                ArrayList<WeakReference<FakePlayer>>()

            for (instance in InstanceManager.instances.values) fakePlayers.add(
                WeakReference(instance.fakePlayer)
            )
            return fakePlayers
        }

    companion object {
        fun init() {
            INSTANCE = object : BotImpl() {
                override fun spawn(
                    configuration: BotConfiguration,
                    location: ServerLocation
                ): WeakReference<ClientInstance> {
                    return spawn(configuration, "localhost", 25565)
                }

                override fun cleanup() {
                }
            }
        }
    }
}
