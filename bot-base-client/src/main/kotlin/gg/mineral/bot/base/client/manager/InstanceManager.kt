package gg.mineral.bot.base.client.manager

import gg.mineral.bot.base.client.instance.ClientInstance
import java.util.*
import java.util.concurrent.ConcurrentHashMap

object InstanceManager {
    @JvmStatic
    val instances: ConcurrentHashMap<UUID, ClientInstance> =
        object : ConcurrentHashMap<UUID, ClientInstance>() {
            override fun put(key: UUID, value: ClientInstance): ClientInstance? {
                require(!containsKey(key)) { "Instance with UUID $key already exists" }
                return super.put(key, value)
            }
        }

    @JvmStatic
    val pendingInstances: ConcurrentHashMap<UUID, ClientInstance> =
        object : ConcurrentHashMap<UUID, ClientInstance>() {
            override fun put(key: UUID, value: ClientInstance): ClientInstance? {
                require(!containsKey(key)) { "Instance with UUID $key already exists" }
                return super.put(key, value)
            }
        }

    @JvmStatic
    val isRunning: Boolean
        get() = !instances.isEmpty()
}
