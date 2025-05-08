package gg.mineral.bot.impl.behaviour.branch

import gg.mineral.bot.api.behaviour.BTResult
import gg.mineral.bot.api.behaviour.BehaviourTree
import gg.mineral.bot.api.behaviour.branch.BTBranch
import gg.mineral.bot.api.behaviour.node.ChildNode
import gg.mineral.bot.api.behaviour.selector
import gg.mineral.bot.api.behaviour.sequence
import gg.mineral.bot.api.controls.MouseButton
import gg.mineral.bot.api.entity.living.player.ClientPlayer
import gg.mineral.bot.api.event.Event
import gg.mineral.bot.api.inv.item.Item
import gg.mineral.bot.api.math.trajectory.Trajectory
import gg.mineral.bot.api.math.trajectory.throwable.EnderPearlTrajectory
import gg.mineral.bot.api.util.dsl.floor
import gg.mineral.bot.api.util.dsl.sqrt
import gg.mineral.bot.api.world.ClientWorld
import gg.mineral.bot.api.world.block.Block
import kotlin.math.abs
import kotlin.math.atan2

class ThrowPearlBranch(tree: BehaviourTree) : BTBranch(tree) {

    // Tick of the last thrown pearl – used for a cooldown
    private var lastPearledTick = 0

    // Returns the hotbar index (0..8) where an ender pearl is located.
    private fun pearlSlot(): Int {
        val inventory = tree.clientInstance.fakePlayer.inventory
        for (i in 0..8) {
            if (inventory.items[i]?.item?.id == Item.ENDER_PEARL) return i
        }
        return -1
    }

    // Returns the closest enemy (ignoring friends) or null if none exist.
    private fun closestEnemy(): ClientPlayer? {
        val fakePlayer = tree.clientInstance.fakePlayer
        val world = fakePlayer.world
        return world.entities.filterIsInstance<ClientPlayer>()
            .filter { !tree.clientInstance.configuration.friendlyUUIDs.contains(it.uuid) }
            .minByOrNull { fakePlayer.distance3DTo(it) }
    }

    // Computes throw angles similar to your goal system.
    private fun getAngles(player: ClientPlayer, entity: ClientPlayer): FloatArray {
        val xDelta: Double = (entity.x - entity.lastX) * 0.4
        val zDelta: Double = (entity.z - entity.lastZ) * 0.4
        var d: Double = player.distance3DTo(entity)
        d -= d % 0.8
        val xMulti = d / 0.8 * xDelta * if (entity.isSprinting) 1.25 else 1.0
        val zMulti = d / 0.8 * zDelta * if (entity.isSprinting) 1.25 else 1.0
        val x = entity.x + xMulti - player.x
        val z = entity.z + zMulti - player.z
        val y = (player.y + player.eyeHeight) - (entity.y + entity.eyeHeight)
        val dist = player.distance3DTo(entity)
        val yaw = Math.toDegrees(atan2(z, x)).toFloat() - 90.0f
        val d1 = sqrt(x * x + z * z)
        val pitch = -(atan2(y, d1) * 180.0 / Math.PI).toFloat() + dist.toFloat() * 0.11f
        return floatArrayOf(yaw, -pitch)
    }

    // Returns true if the given position collides with a block.
    private fun hasHitBlock(world: ClientWorld?, x: Double, y: Double, z: Double): Boolean {
        val xTile = floor(x)
        val yTile = floor(y)
        val zTile = floor(z)
        val block = world?.getBlockAt(xTile, yTile, zTile)
        if (world != null && block != null && block.id != Block.AIR) {
            val collisionBoundingBox = block.getCollisionBoundingBox(world, xTile, yTile, zTile)
            return collisionBoundingBox?.isVecInside(x, y, z) == true
        }
        return false
    }

    override val child: ChildNode = selector(tree) {
        sequence(tree) {
            // Ensure we’re past the pearl cooldown.
            condition {
                tree.clientInstance.currentTick - lastPearledTick >= 20 * tree.clientInstance.configuration.pearlCooldown
            }
            // An enemy must be visible.
            condition { closestEnemy() != null }
            // We need to have at least one ender pearl.
            condition {
                tree.clientInstance.fakePlayer.inventory.contains { it.item.id == Item.ENDER_PEARL }
            }

            selector(tree) {
                // If an ender pearl is in the hotbar, then aim and throw.
                sequence(tree) {
                    condition { hotbarContains { item.id == Item.ENDER_PEARL } }
                    selector(tree) {
                        sequence(tree) {
                            condition { inventoryClosed() }
                            succeeder(leaf {
                                // Aim toward the enemy using our simple getAngles helper.
                                val enemy = closestEnemy() ?: return@leaf BTResult.FAILURE
                                val angles = getAngles(tree.clientInstance.fakePlayer, enemy)
                                setMouseYaw(angles[0])
                                setMousePitch(angles[1])
                                BTResult.SUCCESS
                            })
                            // Verify that the throw trajectory is valid.
                            async(1, waitForCompletion = false) {
                                val fakePlayer = tree.clientInstance.fakePlayer
                                val world = fakePlayer.world
                                val enemy = closestEnemy() ?: return@async BTResult.FAILURE
                                // Compute a target based on enemy position and movement.
                                val targetX = enemy.x + (enemy.x - enemy.lastX)
                                val targetY = enemy.y + (enemy.y - enemy.lastY)
                                val targetZ = enemy.z + (enemy.z - enemy.lastZ)
                                val collisionFunction = { x: Double, y: Double, z: Double ->
                                    abs(x - targetX) < 4 &&
                                            abs(y - targetY) < 1 &&
                                            abs(z - targetZ) < 4 &&
                                            hasHitBlock(world, x, y, z)
                                }
                                val trajectory = EnderPearlTrajectory(
                                    world,
                                    fakePlayer.x,
                                    fakePlayer.y + fakePlayer.eyeHeight,
                                    fakePlayer.z,
                                    fakePlayer.yaw,
                                    fakePlayer.pitch,
                                    collisionFunction
                                )
                                if (trajectory.compute(100) == Trajectory.Result.VALID) BTResult.SUCCESS
                                else BTResult.FAILURE
                            }
                            // If all is valid, throw the pearl.
                            leaf {
                                lastPearledTick = tree.clientInstance.currentTick
                                pressButton(10, MouseButton.Type.RIGHT_CLICK)
                                BTResult.SUCCESS
                            }
                        }
                        // Fallback: if inventory is open, try closing it.
                        leaf { closeInventory() }
                    }
                }
                // Otherwise, if the pearl isn’t in the hotbar, move it there.
                sequence(tree) {
                    condition { inventoryOpen() }
                    selector(tree) {
                        sequence(tree) {
                            condition { isHoveringOverIndex { pearlSlot() } }
                            leaf { moveToHotbar { item.id == Item.ENDER_PEARL } }
                        }
                        leaf { moveCursorTo { pearlSlot() } }
                    }
                    leaf { openInventory() }
                }
            }
        }
    }

    override fun <T : Event> event(event: T) {
        TODO("Not yet implemented")
    }
}
