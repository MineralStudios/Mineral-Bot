package gg.mineral.bot.base.client.math.simulation

import gg.mineral.bot.api.controls.Key
import gg.mineral.bot.api.entity.living.player.ClientPlayer
import gg.mineral.bot.api.event.Event
import gg.mineral.bot.api.event.EventHandler
import gg.mineral.bot.api.math.simulation.PlayerMotionSimulator
import gg.mineral.bot.api.world.ClientWorld
import gg.mineral.bot.base.lwjgl.input.Keyboard
import gg.mineral.bot.base.lwjgl.input.Mouse
import net.minecraft.client.Minecraft
import net.minecraft.client.entity.EntityPlayerSP
import net.minecraft.util.AxisAlignedBB
import net.minecraft.util.MovementInput
import net.minecraft.util.Session
import net.minecraft.world.World

class PlayerMotionSimulator(mc: Minecraft?, player: ClientPlayer, world: ClientWorld = player.world) :
    EntityPlayerSP(
        mc, world as World, Session(
            player.username, player.uuid.toString(),
            "0",
            "legacy"
        ), 0
    ), PlayerMotionSimulator {
    private var millis = 0
    private val initialX: Double
    private val initialY: Double
    private val initialZ: Double
    private val initialMotionX: Double
    private val initialMotionY: Double
    private val initialMotionZ: Double
    private val initialYaw: Float
    private val initialPitch: Float
    private val initialOnGround: Boolean
    private val initialBoundingBox: AxisAlignedBB

    override val keyboard: Keyboard = Keyboard(
        EMPTY_EVENT_HANDLER
    )

    override val mouse: Mouse = Mouse(
        EMPTY_EVENT_HANDLER
    )

    init {
        val oldBoundingBox = player.boundingBox

        this.posX = player.x
        this.posY = player.headY
        this.posZ = player.z
        this.motionX = player.motionX
        this.motionY = player.motionY
        this.motionZ = player.motionZ
        this.rotationYaw = player.yaw
        this.rotationPitch = player.pitch
        this.onGround = player.isOnGround
        this.boundingBox = AxisAlignedBB.getBoundingBox(
            oldBoundingBox.minX,
            oldBoundingBox.minY,
            oldBoundingBox.minZ,
            oldBoundingBox.maxX,
            oldBoundingBox.maxY,
            oldBoundingBox.maxZ
        )

        this.initialX = player.x
        this.initialY = player.headY
        this.initialZ = player.z
        this.initialMotionX = player.motionX
        this.initialMotionY = player.motionY
        this.initialMotionZ = player.motionZ
        this.initialYaw = player.yaw
        this.initialPitch = player.pitch
        this.initialOnGround = player.isOnGround
        this.initialBoundingBox = AxisAlignedBB.getBoundingBox(
            oldBoundingBox.minX,
            oldBoundingBox.minY,
            oldBoundingBox.minZ,
            oldBoundingBox.maxX,
            oldBoundingBox.maxY,
            oldBoundingBox.maxZ
        )

        this.movementInput = object : MovementInput() {
            override fun updatePlayerMoveState() {
                this.moveStrafe = 0.0f
                this.moveForward = 0.0f

                // TODO: fix this not getting called
                if (keyboard.isKeyDown(Key.Type.KEY_W)) ++this.moveForward

                if (keyboard.isKeyDown(Key.Type.KEY_S)) --this.moveForward

                if (keyboard.isKeyDown(Key.Type.KEY_A)) ++this.moveStrafe

                if (keyboard.isKeyDown(Key.Type.KEY_D)) --this.moveStrafe

                this.jump = keyboard.isKeyDown(Key.Type.KEY_SPACE)
                this.sneak = keyboard.isKeyDown(Key.Type.KEY_LSHIFT)

                if (this.sneak) {
                    this.moveStrafe = (moveStrafe.toDouble() * 0.3).toFloat()
                    this.moveForward = (moveForward.toDouble() * 0.3).toFloat()
                }
            }
        }
    }

    override fun execute(millis: Long) {
        while (this.millis < millis) {
            this.runGameLoop()
            this.millis++
        }
    }

    fun runGameLoop() {
        if (millis % 50 == 0) {
            this.onUpdate()
        }

        mc.mouseHelper.mouseXYChange()
        val var132 = 0.5f * 0.6f + 0.2f
        val var141 = var132 * var132 * var132 * 8.0f
        val var15 = mouse.dX.toFloat() * var141
        val var16 = mouse.dY.toFloat() * var141
        val var17: Byte = 1

        this.setAngles(var15, var16 * var17.toFloat())
    }

    override fun reset() {
        this.millis = 0
        this.posX = this.initialX
        this.posY = this.initialY
        this.posZ = this.initialZ
        this.motionX = this.initialMotionX
        this.motionY = this.initialMotionY
        this.motionZ = this.initialMotionZ
        this.rotationYaw = this.initialYaw
        this.rotationPitch = this.initialPitch
        this.onGround = this.initialOnGround
        this.boundingBox = AxisAlignedBB.getBoundingBox(
            initialBoundingBox.minX,
            initialBoundingBox.minY,
            initialBoundingBox.minZ,
            initialBoundingBox.maxX, initialBoundingBox.maxY, initialBoundingBox.maxZ
        )
    }

    companion object {
        private val EMPTY_EVENT_HANDLER: EventHandler = object : EventHandler {
            override fun <T : Event> callEvent(event: T): Boolean {
                return false
            }
        }
    }
}
