package gg.mineral.bot.api.goal

import gg.mineral.bot.api.controls.Key
import gg.mineral.bot.api.controls.Keyboard
import gg.mineral.bot.api.controls.Mouse
import gg.mineral.bot.api.controls.MouseButton
import gg.mineral.bot.api.event.Event
import gg.mineral.bot.api.goal.annotation.KeyboardState
import gg.mineral.bot.api.instance.ClientInstance
import gg.mineral.bot.api.util.angleDifference
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.util.*
import java.util.concurrent.Callable
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.Future
import java.util.function.Consumer


abstract class Goal(protected val clientInstance: ClientInstance) {
    private lateinit var thread: Thread
    private var delayedTasks: Queue<DelayedTask> = ConcurrentLinkedQueue()
    protected val asyncTasks = ConcurrentHashMap<Int, AsyncTickTask<*>>()
    var tickCount = 0

    @JvmRecord
    data class DelayedTask(val runnable: Runnable, val sendTime: Long) {
        fun canSend(): Boolean {
            return timeMillis() >= sendTime
        }
    }

    fun ensureSync(runnable: Runnable) {
        if (Thread.currentThread() == thread) runnable.run()
        else delayedTasks.add(DelayedTask(runnable, 0))
    }

    fun checkExecute(): Boolean {
        if (shouldExecute()) {
            if (this is Sporadic) callStart()
            return true
        }
        return false
    }

    class AsyncTickTask<T : Any>(
        private val future: Future<T>? = null,
        private val onComplete: Consumer<T>
    ) {
        fun checkComplete(): Boolean {
            if (future?.isDone == true) {
                return onComplete.accept(future.get()).let { true }
            }
            return false
        }
    }

    class Tick(val tickNumber: Int, val goal: Goal) {
        private var ended = false
        var finished = false

        fun <T : Any> asyncPrerequisite(
            id: Int,
            name: String,
            condition: Boolean,
            codeBlock: Callable<T>,
            onComplete: Consumer<T>
        ) {
            if (!ended) {
                if (!condition) {
                    executeAsync(id, codeBlock, onComplete)
                    ended = true
                    logger.debug("Prerequisite failed: $name")
                } else logger.debug("Prerequisite passed: $name")
            }
        }

        fun prerequisite(name: String, condition: Boolean, codeBlock: () -> Unit) {
            if (!ended) {
                if (!condition) {
                    codeBlock()
                    ended = true
                    logger.debug("Prerequisite failed: $name")
                } else logger.debug("Prerequisite passed: $name")
            }
        }

        fun finishIf(reason: String, condition: Boolean) {
            if (!ended) {
                if (condition) {
                    ended = true
                    finished = true
                    logger.debug("Finished goal: $reason")
                } else logger.debug("Not finished goal: $reason")
            }
        }

        fun execute(codeBlock: () -> Unit) {
            if (!ended) {
                codeBlock()
            }
        }

        fun <T : Any> executeAsync(id: Int, codeBlock: Callable<T>, onComplete: Consumer<T>) {
            if (!ended) {
                goal.asyncTasks[id]?.let {
                    if (it.checkComplete()) goal.asyncTasks.remove(id)
                    else ended = true
                    return
                } ?: run {
                    ended = true
                }

                val future = goal.clientInstance.asyncExecutor.submit(codeBlock)
                goal.asyncTasks[id] = AsyncTickTask(future, onComplete)
            }
        }
    }


    protected abstract fun shouldExecute(): Boolean

    fun callTick() {
        logger.debug("callTick called of ${this.javaClass.simpleName}")
        /*if (this is Timebound) {
            if (timeMillis() - startTime >= maxDuration) {
                finish()
                return
            }
        }*/
        val tick = Tick(tickCount++, this)
        if (this is Suspendable && this is Sporadic) {
            if (!suspend) onTick(tick)
            else finish()
        } else onTick(tick)

        if (this is Sporadic && tick.finished) finish()
    }

    abstract fun onTick(tick: Tick)

    protected fun finish() {
        if (this is Sporadic) {
            callEnd()
            delayedTasks.clear()
        } else error("Goal must implement Sporadic to call finish()")
    }

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
        if (!::thread.isInitialized) thread = Thread.currentThread()

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

    fun getKeyboardState(clazz: Class<*>): KeyboardState? {
        return keyboardStateCache.getOrPut(clazz) {
            clazz.getAnnotation(KeyboardState::class.java)
        }
    }


    companion object {
        @JvmStatic
        protected val logger: Logger = LogManager.getLogger(Goal::class.java)
        private val keyboardStateCache = mutableMapOf<Class<*>, KeyboardState?>()

        @JvmStatic
        fun timeMillis(): Long {
            return System.nanoTime() / 1000000
        }
    }
}
