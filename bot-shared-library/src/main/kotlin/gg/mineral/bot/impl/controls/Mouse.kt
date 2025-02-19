package gg.mineral.bot.impl.controls

import gg.mineral.bot.api.event.EventHandler
import gg.mineral.bot.api.event.peripherals.MouseButtonEvent
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue

open class Mouse(private val eventHandler: EventHandler) : gg.mineral.bot.api.controls.Mouse {
    private val mouseButtons: Array<MouseButton> =
        gg.mineral.bot.api.controls.MouseButton.Type.entries.map { MouseButton(it) }.toTypedArray()

    override var dWheel: Int = 0

    override var x: Int = 0
        set(value) {
            this.dX = value - field
            field = value
            currentLog = currentLog?.let {
                Log(
                    it.type,
                    it.pressed,
                    value,
                    y,
                    dX,
                    dY,
                    dWheel
                )
            } ?: Log(
                gg.mineral.bot.api.controls.MouseButton.Type.UNKNOWN,
                false,
                value,
                y,
                dX,
                dY,
                dWheel
            )
        }

    override var y: Int = 0
        set(value) {
            this.dY = value - field
            field = value
            currentLog = currentLog?.let {
                Log(
                    it.type,
                    it.pressed,
                    x,
                    value,
                    dX,
                    dY,
                    dWheel
                )
            } ?: Log(
                gg.mineral.bot.api.controls.MouseButton.Type.UNKNOWN,
                false,
                x,
                value,
                dX,
                dY,
                dWheel
            )
        }

    override var dX: Int = 0
        get() {
            val result = field
            field = 0
            return result
        }

    override var dY: Int = 0
        get() {
            val result = field
            field = 0
            return result
        }
    private val logs: Queue<Log> = ConcurrentLinkedQueue()
    private var eventLog: Log? = null
    private var currentLog: Log? = null
    private var iterator: MutableIterator<Log>? = null
    override var isGrabbed = true

    private val scheduledTasks = Object2LongOpenHashMap<Runnable>()

    fun onGameLoop(time: Long) {
        scheduledTasks.keys.removeIf { runnable: Runnable ->
            if (time >= scheduledTasks.getLong(runnable)) {
                runnable.run()
                return@removeIf true
            }
            false
        }
    }

    private fun schedule(runnable: Runnable, delay: Long) {
        scheduledTasks.put(runnable, (System.nanoTime() / 1000000) + delay)
    }

    override fun getButton(type: gg.mineral.bot.api.controls.MouseButton.Type): MouseButton {
        return mouseButtons[type.ordinal]
    }

    override fun pressButton(durationMillis: Int, vararg types: gg.mineral.bot.api.controls.MouseButton.Type) {
        for (type in types) {
            val button = getButton(type)

            if (button.isPressed) continue

            val event = MouseButtonEvent(type, true)

            if (eventHandler.callEvent(event)) continue

            logger.debug("Pressing button: {}", type)
            button.isPressed = true
            if (currentLog != null) logs.add(currentLog)
            currentLog = Log(
                type, true, x, y, dX, dY,
                dWheel
            )
            if (durationMillis > 0 && durationMillis < Int.MAX_VALUE) schedule(
                { unpressButton(type) },
                durationMillis.toLong()
            )
        }
    }

    override fun unpressButton(durationMillis: Int, vararg types: gg.mineral.bot.api.controls.MouseButton.Type) {
        for (type in types) {
            val button = getButton(type)

            if (!button.isPressed) continue

            val event = MouseButtonEvent(type, false)

            if (eventHandler.callEvent(event)) continue

            logger.debug("Unpressing button: {}", type)
            button.isPressed = false
            if (currentLog != null) logs.add(currentLog)
            currentLog = Log(type, false, x, y, dX, dY, dWheel)
        }
    }

    override fun next(): Boolean {
        if (currentLog != null) {
            logs.add(currentLog)
            currentLog = null
        }

        if (iterator == null) {
            buttonsLoop@ for (button in mouseButtons) {
                if (button.isPressed) {
                    for ((type) in logs) if (type == button.type) continue@buttonsLoop

                    logs.add(Log(button.type, true, x, y, dX, dY, dWheel))
                }
            }

            iterator = logs.iterator()
        }

        if (iterator?.hasNext() == true) {
            eventLog = iterator?.next()
            iterator?.remove()
            return true
        }

        iterator = null

        return false
    }

    override val eventButtonType: gg.mineral.bot.api.controls.MouseButton.Type?
        get() = eventLog?.type

    open fun isButtonDown(i: Int): Boolean {
        val type: gg.mineral.bot.api.controls.MouseButton.Type =
            gg.mineral.bot.api.controls.MouseButton.Type.fromKeyCode(i)
        return isButtonDown(type)
    }

    open fun isButtonDown(type: gg.mineral.bot.api.controls.MouseButton.Type): Boolean {
        return getButton(type).isPressed
    }

    override val eventButton: Int
        get() = eventLog?.type?.keyCode ?: -1

    @JvmRecord
    data class Log(
        val type: gg.mineral.bot.api.controls.MouseButton.Type,
        val pressed: Boolean,
        val x: Int,
        val y: Int,
        val dX: Int,
        val dY: Int,
        val dWheel: Int
    )

    override fun setCursorPosition(x: Int, y: Int) {
        this.x = x
        this.y = y
    }

    open val eventDWheel: Int
        get() = eventLog?.dWheel ?: 0

    val eventX: Int
        get() = eventLog?.x ?: x

    val eventY: Int
        get() = eventLog?.y ?: y

    open val eventButtonState: Boolean
        get() = eventLog?.pressed == true

    override fun stopAll() {
        unpressButton(*gg.mineral.bot.api.controls.MouseButton.Type.entries.toTypedArray())
        scheduledTasks.clear()
    }

    override fun changeYaw(dYaw: Float) {
        val defaultMouseSense = 0.5f
        val sensitivity = defaultMouseSense * 0.6f + 0.2f
        val deltaX = dYaw / (sensitivity * sensitivity * sensitivity * 8.0f)
        this.dX = (deltaX / 0.15).toInt()
    }

    override fun changePitch(dPitch: Float) {
        val defaultMouseSense = 0.5f
        val sensitivity = defaultMouseSense * 0.6f + 0.2f
        // TODO: inverted
        // val inverted = this.mc.gameSettings.invertMouse ? -1 : 1;
        val deltaY = -dPitch / (sensitivity * sensitivity * sensitivity * 8.0f /* * inverted */
                )
        this.dY = (deltaY / 0.15).toInt()
    }

    companion object {
        private val logger: Logger = LogManager.getLogger(Mouse::class.java)
    }
}
