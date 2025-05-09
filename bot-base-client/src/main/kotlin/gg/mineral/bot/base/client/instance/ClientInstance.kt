package gg.mineral.bot.base.client.instance

import com.google.common.collect.Multimap
import gg.mineral.bot.api.behaviour.BTResult
import gg.mineral.bot.api.behaviour.BehaviourTree
import gg.mineral.bot.api.configuration.BotConfiguration
import gg.mineral.bot.api.controls.Keyboard
import gg.mineral.bot.api.controls.Mouse
import gg.mineral.bot.api.entity.ClientEntity
import gg.mineral.bot.api.entity.living.player.FakePlayer
import gg.mineral.bot.api.event.Event
import gg.mineral.bot.api.instance.ClientInstance
import gg.mineral.bot.api.inv.Inventory
import gg.mineral.bot.api.inv.InventoryContainer
import gg.mineral.bot.api.inv.Slot
import gg.mineral.bot.api.inv.item.Item
import gg.mineral.bot.api.inv.item.ItemStack
import gg.mineral.bot.api.math.BoundingBox
import gg.mineral.bot.api.math.simulation.PlayerMotionSimulator
import gg.mineral.bot.api.screen.Screen
import gg.mineral.bot.api.world.ClientWorld
import gg.mineral.bot.api.world.block.Block
import gg.mineral.bot.base.client.manager.InstanceManager
import gg.mineral.bot.impl.thread.ThreadManager
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.multiplayer.WorldClient
import net.minecraft.util.Session
import org.apache.logging.log4j.LogManager
import java.io.File
import java.net.Proxy
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.ExecutorService
import java.util.concurrent.ScheduledExecutorService

open class ClientInstance(
    override val configuration: BotConfiguration,
    width: Int,
    height: Int,
    fullscreen: Boolean,
    demo: Boolean,
    gameDir: File,
    assetsDir: File,
    resourcePackDir: File,
    proxy: Proxy,
    version: String,
    userProperties: Multimap<*, *>,
    assetIndex: String
) : Minecraft(
    Session(configuration.fullUsername, configuration.uuid.toString(), "0", "legacy"),
    width,
    height,
    fullscreen,
    demo,
    gameDir,
    assetsDir,
    resourcePackDir,
    proxy,
    version,
    userProperties,
    assetIndex
), ClientInstance {

    // Active behaviour tree.
    override var behaviourTree: BehaviourTree? = null

    // Delayed tasks queue.
    private val delayedTasks = ConcurrentLinkedQueue<DelayedTask>()

    override var latency: Int = 0

    override var currentTick: Int = 0

    override val keyboard: Keyboard
        get() = super.keyboard
    override val mouse: Mouse
        get() = super.mouse

    init {
        mainThread = null
    }

    /**
     * Internal data class to track delayed tasks.
     */
    internal data class DelayedTask(val runnable: Runnable, val sendTime: Long) {
        fun canSend(currentTime: Long): Boolean = currentTime >= sendTime
    }

    /**
     * Schedules a task to run after a delay. If called on the main thread with zero delay and no queued tasks,
     * the task executes immediately.
     */
    fun scheduleTask(runnable: Runnable, delay: Long): Boolean {
        val currentTime = getSystemTime()
        if (isMainThread() && delay <= 0 && delayedTasks.isEmpty()) {
            runnable.run()
            return true
        }
        delayedTasks.add(DelayedTask(runnable, currentTime + delay))
        return false
    }

    /**
     * Returns true if the current thread is the main game thread.
     */
    override fun isMainThread(): Boolean = Thread.currentThread().name.contains("GameLoop")

    override val gameLoopExecutor: ScheduledExecutorService
        get() = ThreadManager.gameLoopExecutor

    override val asyncExecutor: ExecutorService
        get() = ThreadManager.asyncExecutor

    override fun runGameLoop() {
        if (!running) return

        behaviourTree?.frame()

        val currentTime = getSystemTime()
        while (delayedTasks.isNotEmpty()) {
            val task = delayedTasks.peek()
            if (task.canSend(currentTime)) {
                task.runnable.run()
                delayedTasks.poll()
            } else break
        }

        // Call game-loop updates for keyboard and mouse.
        super.keyboard.onGameLoop(getSystemTime())
        super.mouse.onGameLoop(getSystemTime())

        super.runGameLoop()
    }

    override fun schedule(runnable: Runnable, delay: Long): Boolean = scheduleTask(runnable, delay)
    override val session: gg.mineral.bot.api.instance.Session
        get() = super.getSession()

    override fun <T : Event> callEvent(event: T): Boolean {
        return behaviourTree?.event(event) == BTResult.FAILURE
    }

    override fun runTick() {
        super.runTick()
        currentTick++

        // Update latency using a Gaussian distribution from the fake player's random.
        val fp = fakePlayer
        latency = fp.random.nextGaussian(
            configuration.latency.toDouble(),
            configuration.latencyDeviation.toDouble()
        ).toInt()

        behaviourTree?.tick()
    }

    override fun shutdownMinecraftApplet() {
        if (InstanceManager.instances.remove(configuration.uuid) != null)
            logger.debug("Removed instance: {}", configuration.uuid)
        if (InstanceManager.pendingInstances.remove(configuration.uuid) != null)
            logger.debug("Removed pending instance: {}", configuration.uuid)
        this.behaviourTree = null
        running = false
        logger.debug("Stopping!")
        try {
            loadWorld(null as WorldClient?)
        } catch (e: Throwable) {
            // Ignore any errors during shutdown.
        }
        mcSoundHandler?.func_147685_d()
    }

    override val isRunning: Boolean
        get() = running

    override fun timeMillis(): Long = getSystemTime()

    override var currentScreen: Screen?
        get() = super.currentScreen
        set(value) {
            super.currentScreen = value as GuiScreen?
        }

    override val fakePlayer: FakePlayer
        get() {
            val player = thePlayer
            if (player is FakePlayer) {
                return player
            }
            // If the player is not an instance of FakePlayer, return a default FakePlayer.
            return object : FakePlayer {
                override val lastReportedX: Double = 0.0
                override val lastReportedY: Double = 0.0
                override val lastReportedZ: Double = 0.0
                override val inventory: Inventory = object : Inventory {
                    override val heldItemStack: ItemStack? = null
                    override val heldSlot: Int = 0

                    override fun getItemStackAt(slot: Int): ItemStack? {
                        return null
                    }

                    override val helmet = null
                    override val chestplate = null
                    override val leggings = null
                    override val boots = null

                    override fun findSlot(item: Item): Int {
                        return -1
                    }

                    override fun findSlot(id: Int): Int {
                        return -1
                    }

                    override val items: Array<ItemStack?>
                        get() = emptyArray()

                }
                override val inventoryContainer = object : InventoryContainer {
                    override fun getSlot(inventory: Inventory, slot: Int): Slot? {
                        return null
                    }
                }

                override val eyeHeight: Float = 0f
                override val username = configuration.fullUsername
                override val hunger = 20f
                override val eating: Boolean = false
                override val headY = 0.0
                override val activePotionEffectIds = intArrayOf()
                override fun isPotionActive(potionId: Int): Boolean = false
                override val health = 0f
                override val uuid = configuration.uuid
                override val collidingBoundingBox: BoundingBox? get() = null
                override val entityId = 0
                override val x = 0.0
                override val y = 0.0
                override val z = 0.0
                override val yaw = 0f
                override val pitch = 0f
                override val isOnGround = false
                override val lastX = 0.0
                override val lastY = 0.0
                override val lastZ = 0.0
                override var motionX = 0.0
                override var motionY = 0.0
                override var motionZ = 0.0
                override val world: ClientWorld
                    get() = object : ClientWorld {
                        override val entities: Collection<ClientEntity> = emptyList()

                        override fun getEntityByID(entityId: Int): ClientEntity? {
                            return null
                        }

                        override fun getBlockAt(x: Int, y: Int, z: Int): Block {
                            return object : Block {
                                override val id: Int = 0

                                override fun getCollisionBoundingBox(
                                    world: ClientWorld,
                                    xTile: Int,
                                    yTile: Int,
                                    zTile: Int
                                ): BoundingBox? {
                                    return null
                                }
                            }
                        }

                        override fun getBlockAt(x: Double, y: Double, z: Double): Block {
                            return object : Block {
                                override val id: Int = 0

                                override fun getCollisionBoundingBox(
                                    world: ClientWorld,
                                    xTile: Int,
                                    yTile: Int,
                                    zTile: Int
                                ): BoundingBox? {
                                    return null
                                }
                            }
                        }

                    }
                override val random: Random get() = Random()
                override var boundingBox: BoundingBox = object : BoundingBox {
                    override var minX: Double = 0.0
                    override var minY: Double = 0.0
                    override var minZ: Double = 0.0
                    override var maxX: Double = 0.0
                    override var maxY: Double = 0.0
                    override var maxZ: Double = 0.0
                }

                override val isSprinting = false
                override val clientInstance = this@ClientInstance
                override fun motionSimulator(world: ClientWorld): PlayerMotionSimulator {
                    return gg.mineral.bot.base.client.math.simulation.PlayerMotionSimulator(
                        this@ClientInstance,
                        this,
                        world
                    )
                }
            }
        }

    override fun newMouse(): Mouse = gg.mineral.bot.lwjgl.input.Mouse(this)

    override fun newKeyboard(): Keyboard = gg.mineral.bot.lwjgl.input.Keyboard(this)

    override var displayHeight: Int
        get() = super.displayHeight
        set(value) {
            super.displayHeight = value
        }

    override var displayWidth: Int
        get() = super.displayWidth
        set(value) {
            super.displayWidth = value
        }

    companion object {
        private val logger = LogManager.getLogger(ClientInstance::class.java)
    }
}
