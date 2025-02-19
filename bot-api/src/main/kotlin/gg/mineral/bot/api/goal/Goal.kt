package gg.mineral.bot.api.goal

import gg.mineral.bot.api.controls.Key
import gg.mineral.bot.api.controls.Keyboard
import gg.mineral.bot.api.controls.Mouse
import gg.mineral.bot.api.controls.MouseButton
import gg.mineral.bot.api.event.Event
import gg.mineral.bot.api.instance.ClientInstance
import gg.mineral.bot.api.util.MathUtil
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue


abstract class Goal(protected val clientInstance: ClientInstance) : MathUtil {
    protected val logger: Logger =
        LogManager.getLogger(this.javaClass)
    protected var delayedTasks: Queue<DelayedTask> = ConcurrentLinkedQueue()

    @JvmRecord
    data class DelayedTask(val runnable: Runnable, val sendTime: Long) {
        fun canSend(): Boolean {
            return timeMillis() >= sendTime
        }
    }

    abstract fun shouldExecute(): Boolean

    abstract val isExecuting: Boolean

    abstract fun onTick()

    abstract fun onEvent(event: Event): Boolean

    protected abstract fun onGameLoop()

    fun schedule(runnable: Runnable, delay: Int): Boolean {
        if (delay <= 0 && delayedTasks.isEmpty()) {
            runnable.run()
            return true
        }
        delayedTasks.add(DelayedTask(runnable, timeMillis() + delay))
        return false
    }

    private val mouse: Mouse
        get() = clientInstance.mouse

    private val keyboard: Keyboard
        get() = clientInstance.keyboard

    var mouseX: Int
        get() = mouse.x
        set(x) {
            mouse.x = x
        }

    var mouseY: Int
        get() = mouse.y
        set(y) {
            mouse.y = y
        }

    fun setMouseYaw(yaw: Float) {
        val fakePlayer = clientInstance.fakePlayer
        val rotYaw = fakePlayer.yaw
        mouse.changeYaw(angleDifference(rotYaw, yaw))
    }

    fun setMousePitch(pitch: Float) {
        val fakePlayer = clientInstance.fakePlayer
        val rotPitch = fakePlayer.pitch
        mouse.changePitch(angleDifference(rotPitch, pitch))
    }

    fun getButton(type: MouseButton.Type): MouseButton {
        return mouse.getButton(type)!!
    }

    fun getKey(type: Key.Type): Key {
        return keyboard.getKey(type)!!
    }

    fun pressKey(durationMillis: Int, vararg types: Key.Type) {
        keyboard.pressKey(durationMillis, *types)
    }

    fun pressKey(vararg types: Key.Type) {
        keyboard.pressKey(Int.MAX_VALUE, *types)
    }

    fun unpressKey(durationMillis: Int, vararg types: Key.Type) {
        keyboard.unpressKey(durationMillis, *types)
    }

    fun unpressKey(vararg types: Key.Type) {
        keyboard.unpressKey(Int.MAX_VALUE, *types)
    }

    fun pressButton(durationMillis: Int, vararg types: MouseButton.Type) {
        mouse.pressButton(durationMillis, *types)
    }

    fun pressButton(vararg types: MouseButton.Type) {
        mouse.pressButton(*types)
    }

    fun unpressButton(durationMillis: Int, vararg types: MouseButton.Type) {
        mouse.unpressButton(durationMillis, *types)
    }

    fun unpressButton(vararg types: MouseButton.Type) {
        mouse.unpressButton(*types)
    }

    fun stopAll() {
        mouse.stopAll()
        keyboard.stopAll()
    }

    fun mouseStopAll() {
        mouse.stopAll()
    }

    fun keyboardStopAll() {
        keyboard.stopAll()
    }

    fun callGameLoop() {
        this.onGameLoop()
        while (!delayedTasks.isEmpty()) {
            val task = delayedTasks.peek()
            if (task.canSend()) {
                task.runnable.run()
                delayedTasks.poll()
                continue
            }

            break
        }
    }

    companion object {
        @JvmStatic
        protected fun timeMillis(): Long {
            return System.nanoTime() / 1000000
        }
    }
}
