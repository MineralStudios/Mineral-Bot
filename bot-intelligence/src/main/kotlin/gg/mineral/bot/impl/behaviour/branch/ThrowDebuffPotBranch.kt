package gg.mineral.bot.impl.behaviour.branch

import gg.mineral.bot.api.behaviour.BTResult
import gg.mineral.bot.api.behaviour.BehaviourTree
import gg.mineral.bot.api.behaviour.branch.BTBranch
import gg.mineral.bot.api.behaviour.node.ChildNode
import gg.mineral.bot.api.behaviour.selector
import gg.mineral.bot.api.controls.Key
import gg.mineral.bot.api.controls.MouseButton
import gg.mineral.bot.api.entity.effect.PotionEffectType
import gg.mineral.bot.api.entity.living.ClientLivingEntity
import gg.mineral.bot.api.entity.living.player.ClientPlayer
import gg.mineral.bot.api.entity.living.player.FakePlayer
import gg.mineral.bot.api.entity.throwable.ClientPotion
import gg.mineral.bot.api.inv.item.Item
import gg.mineral.bot.api.inv.item.ItemStack
import gg.mineral.bot.api.math.trajectory.Trajectory
import gg.mineral.bot.api.math.trajectory.throwable.SplashPotionTrajectory
import gg.mineral.bot.api.util.dsl.fastArcTan
import gg.mineral.bot.api.util.dsl.floor
import gg.mineral.bot.api.util.dsl.sqrt
import gg.mineral.bot.api.util.dsl.vectorForRotation
import gg.mineral.bot.api.world.ClientWorld
import gg.mineral.bot.api.world.block.Block
import org.apache.commons.math3.analysis.UnivariateFunction
import org.apache.commons.math3.optim.MaxEval
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType
import org.apache.commons.math3.optim.univariate.BrentOptimizer
import org.apache.commons.math3.optim.univariate.SearchInterval
import org.apache.commons.math3.optim.univariate.UnivariateObjectiveFunction

class ThrowDebuffPotBranch(tree: BehaviourTree) : BTBranch(tree) {
    private fun effects(): Set<Int> {
        val closestEnemy = closestEnemy() ?: return emptySet()
        val debuffEffects = setOf(
            PotionEffectType.HARM.id,
            PotionEffectType.POISON.id,
            PotionEffectType.WEAKNESS.id,
            PotionEffectType.SLOW.id,
            PotionEffectType.BLINDNESS.id,
            PotionEffectType.HUNGER.id,
            PotionEffectType.SLOW_DIGGING.id
        )

        return debuffEffects.subtract(closestEnemy.activePotionEffectIds.toSet())
    }

    private fun isDebuffPot(itemStack: ItemStack): Boolean {
        val item = itemStack.item
        val effects = effects()
        return item.id == Item.POTION && itemStack.potion?.effects?.any {
            effects.contains(it.potionID)
        } == true
    }

    private fun debuffPotSlot(): Int {
        val inventory = tree.clientInstance.fakePlayer.inventory
        for (i in 0..8)
            if (inventory.items[i]?.let { isDebuffPot(it) } == true)
                return i

        return -1
    }

    private fun angleAwayFromEnemies(): Float {
        val fakePlayer = clientInstance.fakePlayer
        val world = fakePlayer.world

        val enemy = world.entities
            .minByOrNull {
                if (it is ClientLivingEntity && !clientInstance.configuration.friendlyUUIDs.contains(it.uuid))
                    it.distance3DTo(fakePlayer)
                else Double.MAX_VALUE
            } ?: return fakePlayer.yaw
        val x: Double = enemy.x - fakePlayer.x
        val z: Double = enemy.z - fakePlayer.z

        var yaw = Math.toDegrees(-fastArcTan(x / z)).toFloat()
        if (z < 0.0 && x < 0.0) yaw = (90.0 + Math.toDegrees(fastArcTan(z / x))).toFloat()
        else if (z < 0.0 && x > 0.0) yaw = (-90.0 + Math.toDegrees(fastArcTan(z / x))).toFloat()
        return yaw + 180.0f
    }

    private fun distanceFromEnemies(): Double {
        val fakePlayer = clientInstance.fakePlayer
        val world = fakePlayer.world

        return world.entities
            .minOfOrNull {
                if (it is ClientLivingEntity && !clientInstance.configuration.friendlyUUIDs.contains(it.uuid))
                    it.distance2DTo(fakePlayer.x, fakePlayer.z)
                else Double.MAX_VALUE
            } ?: Double.MAX_VALUE
    }

    private fun hasHitBlock(world: ClientWorld?, x: Double, y: Double, z: Double): Boolean {
        val xTile = floor(x)
        val yTile = floor(y)
        val zTile = floor(z)
        val block = world?.getBlockAt(xTile, yTile, zTile)

        if (world != null && block != null && block.id != Block.AIR) return block.getCollisionBoundingBox(
            world,
            xTile,
            yTile,
            zTile
        )?.isVecInside(x, y, z) ?: false

        return false
    }

    private fun minimizePitch(
        fakePlayer: FakePlayer,
        closestEnemy: ClientPlayer?,
        valueFunction: (SplashPotionTrajectory) -> Double
    ): Float {
        closestEnemy ?: return fakePlayer.pitch
        val objective = UnivariateFunction { pitch ->
            val simulator = fakePlayer.motionSimulator().apply {
                setMouseYaw(fakePlayer.yaw)
                keyboard.setState(Key.Type.KEY_S)
            }

            val enemySimulator = closestEnemy.motionSimulator()
            val trajectory = object : SplashPotionTrajectory(
                fakePlayer.world,
                fakePlayer.x,
                fakePlayer.y + fakePlayer.eyeHeight,
                fakePlayer.z,
                fakePlayer.yaw,
                pitch.toFloat(), { x, y, z ->
                    hasHitBlock(fakePlayer.world, x, y, z) && enemySimulator.distance3DToSq(x, y, z).let {
                        if (it < 16.0) 1.0 - sqrt(it) / 4.0 > 0.5
                        else false
                    }
                            && simulator.distance3DToSq(x, y, z)
                        .let { if (it < 16.0) 1.0 - sqrt(it) / 4.0 <= 0.01 else true }
                }
            ) {
                override fun tick(): Trajectory.Result {
                    enemySimulator.execute(50)
                    simulator.execute(50)
                    return super.tick()
                }
            }
            if (trajectory.compute(100) === Trajectory.Result.VALID)
                valueFunction.invoke(trajectory)
            else
                Double.MAX_VALUE
        }

        val optimizer = BrentOptimizer(1e-10, 1e-14)

        val result = optimizer.optimize(
            MaxEval(180),
            UnivariateObjectiveFunction(objective),
            GoalType.MINIMIZE,
            SearchInterval(-90.0, 90.0)
        )

        return result.value.toFloat()
    }

    private fun closestEnemy(): ClientPlayer? {
        val fakePlayer = clientInstance.fakePlayer
        val world = fakePlayer.world
        val targetSearchRange = clientInstance.configuration.targetSearchRange
        var bestTarget: ClientPlayer? = null
        var closestDistance = Double.MAX_VALUE

        for (entity in world.entities) {
            if (entity is ClientPlayer &&
                !clientInstance.configuration.friendlyUUIDs.contains(entity.uuid)
            ) {
                val distance = fakePlayer.distance3DTo(entity)
                if (distance <= targetSearchRange && distance < closestDistance) {
                    bestTarget = entity
                    closestDistance = distance
                }
            }
        }
        return bestTarget
    }

    private fun isAtWall(): Boolean {
        val fakePlayer = clientInstance.fakePlayer
        val world = fakePlayer.world

        val posX = fakePlayer.x
        val posY = fakePlayer.y + fakePlayer.eyeHeight
        val posZ = fakePlayer.z
        val yaw = fakePlayer.yaw
        val pitch = 0f

        val checkDistance = 1.0
        val dir = vectorForRotation(pitch, yaw)  // Assumes this helper exists.
        val checkX = posX + dir[0] * checkDistance
        val checkY = posY + dir[1] * checkDistance
        val checkZ = posZ + dir[2] * checkDistance

        val block = world.getBlockAt(checkX, checkY, checkZ)
        return block.id != Block.AIR
    }

    private fun distanceAwayFromEnemies(): Double {
        val fakePlayer = clientInstance.fakePlayer
        val world = fakePlayer.world

        return world.entities
            .minOfOrNull {
                if (it is ClientLivingEntity && !clientInstance.configuration.friendlyUUIDs.contains(it.uuid))
                    it.distance2DTo(fakePlayer.x, fakePlayer.z)
                else Double.MAX_VALUE
            } ?: Double.MAX_VALUE
    }

    override val child: ChildNode = selector(tree) {
        sequence {
            condition {
                val closestEnemy = closestEnemy() ?: return@condition false
                closestEnemy.distance3DToSq(clientInstance.fakePlayer) in 25.0..64.0
            }
            condition {
                clientInstance.fakePlayer.inventory.contains {
                    isDebuffPot(it)
                }
            }
            condition {
                val world = clientInstance.fakePlayer.world
                !world.entities.any { it is ClientPotion }
            }

            selector {
                sequence {
                    condition { hotbarContains { isDebuffPot(this) } }
                    selector {
                        sequence {
                            condition { inventoryClosed() }

                            succeeder(leaf {
                                setMouseYaw(angleAwayFromEnemies())
                                BTResult.SUCCESS
                            })
                            async(0, waitForCompletion = false) {
                                setMousePitch(
                                    minimizePitch(
                                        clientInstance.fakePlayer,
                                        closestEnemy()
                                    ) { it.airTimeTicks.toDouble() })
                                BTResult.SUCCESS
                            }

                            selector {
                                sequence {
                                    condition {
                                        val inventory = clientInstance.fakePlayer.inventory
                                        inventory.heldSlot == debuffPotSlot()
                                    }

                                    selector {
                                        sequence {
                                            condition {
                                                val distanceCondition = distanceAwayFromEnemies() > 3.6
                                                clientInstance.fakePlayer.let { fakePlayer ->
                                                    val simulator = fakePlayer.motionSimulator().apply {
                                                        keyboard.setState(Key.Type.KEY_S)
                                                        setMouseYaw(fakePlayer.yaw)
                                                    }
                                                    val closestEnemy = closestEnemy()
                                                    val enemySimulator = closestEnemy?.motionSimulator()
                                                    val trajectory = object : SplashPotionTrajectory(
                                                        fakePlayer.world,
                                                        fakePlayer.x,
                                                        fakePlayer.y + fakePlayer.eyeHeight,
                                                        fakePlayer.z,
                                                        fakePlayer.yaw,
                                                        fakePlayer.pitch, { x, y, z ->
                                                            hasHitBlock(fakePlayer.world, x, y, z) &&
                                                                    enemySimulator?.distance3DToSq(x, y, z)?.let {
                                                                        if (it < 16.0) 1.0 - sqrt(it) / 4.0 > 0.5
                                                                        else false
                                                                    } == true
                                                                    && simulator.distance3DToSq(x, y, z)
                                                                .let { if (it < 16.0) 1.0 - sqrt(it) / 4.0 <= 0.01 else true }
                                                        }
                                                    ) {
                                                        override fun tick(): Trajectory.Result {
                                                            enemySimulator?.execute(50)
                                                            simulator.execute(50)
                                                            return super.tick()
                                                        }
                                                    }
                                                    (trajectory.compute(100) == Trajectory.Result.VALID && (isAtWall() || distanceCondition)) || clientInstance.fakePlayer.health <= 6f
                                                }
                                            }

                                            leaf {
                                                val world = clientInstance.fakePlayer.world
                                                val potion =
                                                    world.entities.filter { it is ClientPotion && it.potionDurability == 16421 }
                                                        .minByOrNull {
                                                            it.distance2DTo(
                                                                clientInstance.fakePlayer.x,
                                                                clientInstance.fakePlayer.z
                                                            )
                                                        }

                                                val distance =
                                                    potion?.distance2DTo(
                                                        clientInstance.fakePlayer.x,
                                                        clientInstance.fakePlayer.z
                                                    ) ?: Double.MAX_VALUE

                                                if (distance < 1.0) BTResult.SUCCESS
                                                else {
                                                    pressButton(10, MouseButton.Type.RIGHT_CLICK)
                                                    BTResult.RUNNING
                                                }
                                            }
                                        }
                                    }
                                }

                                leaf {
                                    val healthPotSlot = debuffPotSlot()

                                    if (healthPotSlot == -1)
                                        BTResult.FAILURE

                                    val inventory = tree.clientInstance.fakePlayer.inventory
                                    val heldSlot = inventory.heldSlot
                                    if (heldSlot != healthPotSlot) {
                                        pressKey(10, Key.Type.valueOf("KEY_" + (healthPotSlot + 1)))
                                        BTResult.RUNNING
                                    }

                                    BTResult.SUCCESS
                                }
                            }
                        }

                        leaf { closeInventory() }
                    }
                }

                sequence {
                    condition { inventoryOpen() }

                    selector {
                        sequence {
                            condition { isHoveringOverIndex { debuffPotSlot() } }
                            leaf { moveToHotbar { isDebuffPot(this) } }
                        }
                        leaf { moveCursorTo { debuffPotSlot() } }
                    }
                }

                leaf { openInventory() }
            }
        }
    }
}