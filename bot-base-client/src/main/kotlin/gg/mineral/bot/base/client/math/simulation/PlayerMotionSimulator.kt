package gg.mineral.bot.base.client.math.simulation

import gg.mineral.bot.api.controls.Key
import gg.mineral.bot.api.entity.living.player.ClientPlayer
import gg.mineral.bot.api.event.Event
import gg.mineral.bot.api.event.EventHandler
import gg.mineral.bot.api.math.simulation.PlayerMotionSimulator
import gg.mineral.bot.api.world.ClientWorld
import gg.mineral.bot.base.lwjgl.input.Keyboard
import gg.mineral.bot.base.lwjgl.input.Mouse
import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.client.Minecraft
import net.minecraft.client.entity.EntityPlayerSP
import net.minecraft.crash.CrashReport
import net.minecraft.entity.Entity
import net.minecraft.init.Blocks
import net.minecraft.util.*
import net.minecraft.world.World

class PlayerMotionSimulator(mc: Minecraft, player: ClientPlayer, world: ClientWorld = player.world) :
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

    private fun runGameLoop() {
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

    override fun moveEntity(p_70091_1_: Double, p_70091_3_: Double, p_70091_5_: Double) {
        val boundingBox = this.boundingBox as AxisAlignedBB
        var p_70091_1_ = p_70091_1_
        var p_70091_3_ = p_70091_3_
        var p_70091_5_ = p_70091_5_
        if (this.noClip) {
            boundingBox.offset(p_70091_1_, p_70091_3_, p_70091_5_)
            this.posX = (boundingBox.minX + boundingBox.maxX) / 2.0
            this.posY = boundingBox.minY + yOffset.toDouble() - ySize.toDouble()
            this.posZ = (boundingBox.minZ + boundingBox.maxZ) / 2.0
        } else {
            worldObj.theProfiler.startSection("move")
            this.ySize *= 0.4f
            val var7 = this.posX
            val var9 = this.posY
            val var11 = this.posZ

            if (this.isInWeb) {
                this.isInWeb = false
                p_70091_1_ *= 0.25
                p_70091_3_ *= 0.05000000074505806
                p_70091_5_ *= 0.25
                this.motionX = 0.0
                this.motionY = 0.0
                this.motionZ = 0.0
            }

            var var13 = p_70091_1_
            val var15 = p_70091_3_
            var var17 = p_70091_5_
            val var19 = boundingBox.copy()
            val var20 = this.onGround && this.isSneaking

            if (var20) {
                val var21 = 0.05
                while (p_70091_1_ != 0.0 && getCollidingBoundingBoxes(
                        this,
                        boundingBox.getOffsetBoundingBox(p_70091_1_, -1.0, 0.0)
                    )
                        .isEmpty()
                ) {
                    if (p_70091_1_ < var21 && p_70091_1_ >= -var21) {
                        p_70091_1_ = 0.0
                    } else if (p_70091_1_ > 0.0) {
                        p_70091_1_ -= var21
                    } else {
                        p_70091_1_ += var21
                    }
                    var13 = p_70091_1_
                }

                while (p_70091_5_ != 0.0 && getCollidingBoundingBoxes(
                        this,
                        boundingBox.getOffsetBoundingBox(0.0, -1.0, p_70091_5_)
                    )
                        .isEmpty()
                ) {
                    if (p_70091_5_ < var21 && p_70091_5_ >= -var21) {
                        p_70091_5_ = 0.0
                    } else if (p_70091_5_ > 0.0) {
                        p_70091_5_ -= var21
                    } else {
                        p_70091_5_ += var21
                    }
                    var17 = p_70091_5_
                }

                while (p_70091_1_ != 0.0 && p_70091_5_ != 0.0 && getCollidingBoundingBoxes(
                        this,
                        boundingBox.getOffsetBoundingBox(p_70091_1_, -1.0, p_70091_5_)
                    )
                        .isEmpty()
                ) {
                    if (p_70091_1_ < var21 && p_70091_1_ >= -var21) {
                        p_70091_1_ = 0.0
                    } else if (p_70091_1_ > 0.0) {
                        p_70091_1_ -= var21
                    } else {
                        p_70091_1_ += var21
                    }

                    if (p_70091_5_ < var21 && p_70091_5_ >= -var21) {
                        p_70091_5_ = 0.0
                    } else if (p_70091_5_ > 0.0) {
                        p_70091_5_ -= var21
                    } else {
                        p_70091_5_ += var21
                    }

                    var13 = p_70091_1_
                    var17 = p_70091_5_
                }
            }

            var var36: List<*> = getCollidingBoundingBoxes(
                this,
                boundingBox.addCoord(p_70091_1_, p_70091_3_, p_70091_5_)
            )

            for (var22 in var36.indices) {
                p_70091_3_ = (var36[var22] as AxisAlignedBB).calculateYOffset(boundingBox, p_70091_3_)
            }

            boundingBox.offset(0.0, p_70091_3_, 0.0)

            if (!this.field_70135_K && var15 != p_70091_3_) {
                p_70091_5_ = 0.0
                p_70091_3_ = 0.0
                p_70091_1_ = 0.0
            }

            val var37 = this.onGround || var15 != p_70091_3_ && var15 < 0.0

            var var23 = 0
            while (var23 < var36.size) {
                p_70091_1_ = (var36[var23] as AxisAlignedBB).calculateXOffset(boundingBox, p_70091_1_)
                ++var23
            }

            boundingBox.offset(p_70091_1_, 0.0, 0.0)

            if (!this.field_70135_K && var13 != p_70091_1_) {
                p_70091_5_ = 0.0
                p_70091_3_ = 0.0
                p_70091_1_ = 0.0
            }

            var23 = 0
            while (var23 < var36.size) {
                p_70091_5_ = (var36[var23] as AxisAlignedBB).calculateZOffset(boundingBox, p_70091_5_)
                ++var23
            }

            boundingBox.offset(0.0, 0.0, p_70091_5_)

            if (!this.field_70135_K && var17 != p_70091_5_) {
                p_70091_5_ = 0.0
                p_70091_3_ = 0.0
                p_70091_1_ = 0.0
            }

            var var25: Double
            var var27: Double
            var var30: Int
            var var38: Double

            if (this.stepHeight > 0.0f && var37 && (var20 || this.ySize < 0.05f)
                && (var13 != p_70091_1_ || var17 != p_70091_5_)
            ) {
                var38 = p_70091_1_
                var25 = p_70091_3_
                var27 = p_70091_5_
                p_70091_1_ = var13
                p_70091_3_ = stepHeight.toDouble()
                p_70091_5_ = var17
                val var29 = boundingBox.copy()
                boundingBox.setBB(var19)
                var36 = getCollidingBoundingBoxes(
                    this,
                    boundingBox.addCoord(var13, p_70091_3_, var17)
                )

                var30 = 0
                while (var30 < var36.size) {
                    p_70091_3_ = var36[var30].calculateYOffset(boundingBox, p_70091_3_)
                    ++var30
                }

                boundingBox.offset(0.0, p_70091_3_, 0.0)

                if (!this.field_70135_K && var15 != p_70091_3_) {
                    p_70091_5_ = 0.0
                    p_70091_3_ = 0.0
                    p_70091_1_ = 0.0
                }

                var30 = 0
                while (var30 < var36.size) {
                    p_70091_1_ = var36[var30].calculateXOffset(boundingBox, p_70091_1_)
                    ++var30
                }

                boundingBox.offset(p_70091_1_, 0.0, 0.0)

                if (!this.field_70135_K && var13 != p_70091_1_) {
                    p_70091_5_ = 0.0
                    p_70091_3_ = 0.0
                    p_70091_1_ = 0.0
                }

                var30 = 0
                while (var30 < var36.size) {
                    p_70091_5_ = var36[var30].calculateZOffset(boundingBox, p_70091_5_)
                    ++var30
                }

                boundingBox.offset(0.0, 0.0, p_70091_5_)

                if (!this.field_70135_K && var17 != p_70091_5_) {
                    p_70091_5_ = 0.0
                    p_70091_3_ = 0.0
                    p_70091_1_ = 0.0
                }

                if (!this.field_70135_K && var15 != p_70091_3_) {
                    p_70091_5_ = 0.0
                    p_70091_3_ = 0.0
                    p_70091_1_ = 0.0
                } else {
                    p_70091_3_ = -stepHeight.toDouble()

                    var30 = 0
                    while (var30 < var36.size) {
                        p_70091_3_ = var36[var30].calculateYOffset(boundingBox, p_70091_3_)
                        ++var30
                    }

                    boundingBox.offset(0.0, p_70091_3_, 0.0)
                }

                if (var38 * var38 + var27 * var27 >= p_70091_1_ * p_70091_1_ + p_70091_5_ * p_70091_5_) {
                    p_70091_1_ = var38
                    p_70091_3_ = var25
                    p_70091_5_ = var27
                    boundingBox.setBB(var29)
                }
            }

            worldObj.theProfiler.endSection()
            worldObj.theProfiler.startSection("rest")
            this.posX = (boundingBox.minX + boundingBox.maxX) / 2.0
            this.posY = boundingBox.minY + yOffset.toDouble() - ySize.toDouble()
            this.posZ = (boundingBox.minZ + boundingBox.maxZ) / 2.0
            this.isCollidedHorizontally = var13 != p_70091_1_ || var17 != p_70091_5_
            this.isCollidedVertically = var15 != p_70091_3_
            this.onGround = var15 != p_70091_3_ && var15 < 0.0
            this.isCollided = this.isCollidedHorizontally || this.isCollidedVertically
            this.updateFallState(p_70091_3_, this.onGround)

            if (var13 != p_70091_1_) {
                this.motionX = 0.0
            }

            if (var15 != p_70091_3_) {
                this.motionY = 0.0
            }

            if (var17 != p_70091_5_) {
                this.motionZ = 0.0
            }

            var38 = this.posX - var7
            var25 = this.posY - var9
            var27 = this.posZ - var11

            if (this.canTriggerWalking() && !var20 && this.ridingEntity == null) {
                val var39 = MathHelper.floor_double(this.posX)
                var30 = MathHelper.floor_double(this.posY - 0.20000000298023224 - yOffset.toDouble())
                val var31 = MathHelper.floor_double(this.posZ)
                var var32 = worldObj.getBlock(var39, var30, var31)
                val var33 = worldObj.getBlock(var39, var30 - 1, var31).renderType

                if (var33 == 11 || var33 == 32 || var33 == 21) {
                    var32 = worldObj.getBlock(var39, var30 - 1, var31)
                }

                if (var32 !== Blocks.ladder) {
                    var25 = 0.0
                }

                this.distanceWalkedModified =
                    (distanceWalkedModified.toDouble() + MathHelper.sqrt_double(var38 * var38 + var27 * var27)
                        .toDouble() * 0.6).toFloat()
                this.distanceWalkedOnStepModified =
                    (distanceWalkedOnStepModified.toDouble() + MathHelper.sqrt_double(var38 * var38 + var25 * var25 + var27 * var27)
                        .toDouble() * 0.6).toFloat()

                if (this.distanceWalkedOnStepModified > nextStepDistance.toFloat()
                    && var32.material !== Material.air
                ) {
                    this.nextStepDistance = distanceWalkedOnStepModified.toInt() + 1

                    if (this.isInWater) {
                        var var34 =
                            (MathHelper.sqrt_double(this.motionX * this.motionX * 0.20000000298023224 + this.motionY * this.motionY + this.motionZ * this.motionZ * 0.20000000298023224)
                                    * 0.35f)

                        if (var34 > 1.0f) {
                            var34 = 1.0f
                        }

                        this.playSound(
                            this.swimSound, var34,
                            1.0f + (rand.nextFloat() - rand.nextFloat()) * 0.4f
                        )
                    }

                    this.func_145780_a(var39, var30, var31, var32)
                    var32.onEntityWalking(this.worldObj, var39, var30, var31, this)
                }
            }

            try {
                this.func_145775_I()
            } catch (var35: Throwable) {
                val var41 = CrashReport.makeCrashReport(var35, "Checking entity block collision")
                val var42 = var41.makeCategory("Entity being checked for collision")
                this.addEntityCrashInfo(var42)
                throw ReportedException(var41)
            }

            val var40 = this.isWet

            if (worldObj.func_147470_e(boundingBox.contract(0.001, 0.001, 0.001))) {
                this.dealFireDamage(1)

                if (!var40) {
                    ++this.fire

                    if (this.fire == 0) {
                        this.setFire(8)
                    }
                }
            } else if (this.fire <= 0) {
                this.fire = -this.fireResistance
            }

            if (var40 && this.fire > 0) {
                this.playSound("random.fizz", 0.7f, 1.6f + (rand.nextFloat() - rand.nextFloat()) * 0.4f)
                this.fire = -this.fireResistance
            }

            worldObj.theProfiler.endSection()
        }
    }

    fun getCollidingBoundingBoxes(p_72945_1_: Entity, p_72945_2_: AxisAlignedBB): List<AxisAlignedBB> {
        val collidingBoundingBoxes = mutableListOf<AxisAlignedBB>()
        val var3 = MathHelper.floor_double(p_72945_2_.minX)
        val var4 = MathHelper.floor_double(p_72945_2_.maxX + 1.0)
        val var5 = MathHelper.floor_double(p_72945_2_.minY)
        val var6 = MathHelper.floor_double(p_72945_2_.maxY + 1.0)
        val var7 = MathHelper.floor_double(p_72945_2_.minZ)
        val var8 = MathHelper.floor_double(p_72945_2_.maxZ + 1.0)

        for (var9 in var3..<var4) {
            for (var10 in var7..<var8) {
                if (worldObj.blockExists(var9, 64, var10)) {
                    for (var11 in var5 - 1..<var6) {

                        val var12: Block =
                            if (var9 >= -30000000 && var9 < 30000000 && var10 >= -30000000 && var10 < 30000000) {
                                worldObj.getBlock(var9, var11, var10)
                            } else {
                                Blocks.stone
                            }

                        var12.addCollisionBoxesToList(
                            worldObj, var9, var11, var10, p_72945_2_, collidingBoundingBoxes,
                            p_72945_1_
                        )
                    }
                }
            }
        }

        val var14 = 0.25
        val var15: List<*> =
            worldObj.getEntitiesWithinAABBExcludingEntity(p_72945_1_, p_72945_2_.expand(var14, var14, var14))

        for (var16 in var15.indices) {
            var var13 = (var15[var16] as Entity).collidingBoundingBox

            if (var13 != null && var13.intersectsWith(p_72945_2_)) {
                collidingBoundingBoxes.add(var13)
            }

            var13 = p_72945_1_.getCollisionBox(var15[var16] as Entity?)

            if (var13 != null && var13.intersectsWith(p_72945_2_)) {
                collidingBoundingBoxes.add(var13)
            }
        }

        return collidingBoundingBoxes
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
