package gg.mineral.bot.impl.controls

import gg.mineral.bot.api.controls.Keyboard.Log
import gg.mineral.bot.api.event.EventHandler
import gg.mineral.bot.api.event.peripherals.KeyboardKeyEvent
import gg.mineral.bot.api.instance.ClientInstance
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue

open class Keyboard(private val eventHandler: EventHandler) : gg.mineral.bot.api.controls.Keyboard {
    private val keys: Array<Key> = gg.mineral.bot.api.controls.Key.Type.entries.map { Key(it) }.toTypedArray()
    private val logs: Queue<Log> = ConcurrentLinkedQueue()
    private var eventLog: Log? = null
    private var currentLog: Log? = null
    private var iterator: MutableIterator<Log>? = null

    private val scheduledTasks = Object2LongOpenHashMap<Runnable>()

    fun onGameLoop(time: Long) {
        scheduledTasks.keys.removeIf { runnable: Runnable ->
            if (time >= scheduledTasks.getLong(runnable)) {
                runnable.run()
                return@removeIf true
            }
            false
        }

        // --- Begin added logic to replicate LWJGL 2.9.4 mouse grab behavior ---
        // If the eventHandler is a ClientInstance, we can check the mouse.
        if (eventHandler is ClientInstance) {
            // When the mouse is ungrabbed, release all pressed keys.
            if (!eventHandler.mouse.isGrabbed) {
                for (key in keys) {
                    if (key.isPressed) {
                        // Using duration 0 so that no reschedule occurs.
                        unpressKey(0, key.type)
                    }
                }
            }
        }
        // --- End added logic ---
    }

    fun schedule(runnable: Runnable, delay: Long) {
        scheduledTasks.put(runnable, (System.nanoTime() / 1000000) + delay)
    }

    override fun getKey(type: gg.mineral.bot.api.controls.Key.Type): Key? {
        return keys[type.ordinal]
    }

    override fun pressKey(durationMillis: Int, vararg types: gg.mineral.bot.api.controls.Key.Type) {
        for (type in types) {
            val key = getKey(type)

            if (key == null || key.isPressed) continue

            val event = KeyboardKeyEvent(type, true)

            if (eventHandler.callEvent(event)) continue

            logger.debug("Pressing key: {} for {}ms", type, durationMillis)
            key.isPressed = true
            if (currentLog != null) logs.add(currentLog)
            currentLog = Log(type, true)
            if (durationMillis > 0 && durationMillis < Int.MAX_VALUE) schedule(
                { unpressKey(type) },
                durationMillis.toLong()
            )
        }
    }

    override fun unpressKey(durationMillis: Int, vararg types: gg.mineral.bot.api.controls.Key.Type) {
        for (type in types) {
            val key = getKey(type)

            if (key == null || !key.isPressed) continue

            val event = KeyboardKeyEvent(type, false)

            if (eventHandler.callEvent(event)) continue

            logger.debug("Unpressing key: {} for {}ms", type, durationMillis)
            key.isPressed = false
            if (currentLog != null) logs.add(currentLog)
            currentLog = Log(type, false)

            if (durationMillis > 0 && durationMillis < Int.MAX_VALUE) schedule(
                { pressKey(type) },
                durationMillis.toLong()
            )
        }
    }

    override fun next(): Boolean {
        if (currentLog != null) {
            logs.add(currentLog)
            currentLog = null
        }

        if (iterator == null) {
            keysLoop@ for (key in keys) {
                if (key.isPressed) {
                    for ((type) in logs) if (type == key.type) continue@keysLoop
                    logs.add(Log(key.type, true))
                }
            }

            iterator = logs.iterator()
        }

        if (iterator?.hasNext() == true) {
            eventLog = iterator!!.next()
            iterator?.remove()
            return true
        }

        iterator = null

        return false
    }

    override fun getKeyStateChanges(): List<Log> {
        return logs.toList() + listOfNotNull(currentLog)
    }

    override val eventKey: Int
        get() = if (eventLog != null) eventLog!!.type.keyCode else -1

    override val eventKeyType: gg.mineral.bot.api.controls.Key.Type?
        get() = if (eventLog != null) eventLog!!.type else null

    override fun isKeyDown(type: gg.mineral.bot.api.controls.Key.Type): Boolean {
        val key = getKey(type)
        return key != null && key.isPressed
    }

    override val eventKeyState: Boolean
        get() = eventLog?.pressed == true

    override fun stopAll() {
        unpressKey(Int.MAX_VALUE, *gg.mineral.bot.api.controls.Key.Type.entries.toTypedArray())
        scheduledTasks.clear()
    }

    override fun setState(vararg types: gg.mineral.bot.api.controls.Key.Type) {
        stopAll()
        for (type in types) pressKey(type)
    }

    companion object {
        private val logger: Logger = LogManager.getLogger(Keyboard::class.java)
    }
}