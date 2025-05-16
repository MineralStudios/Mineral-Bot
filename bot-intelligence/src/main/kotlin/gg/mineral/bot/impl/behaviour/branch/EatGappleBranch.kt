package gg.mineral.bot.impl.behaviour.branch

import gg.mineral.bot.api.behaviour.BTResult
import gg.mineral.bot.api.behaviour.BehaviourTree
import gg.mineral.bot.api.behaviour.branch.BTBranch
import gg.mineral.bot.api.behaviour.node.ChildNode
import gg.mineral.bot.api.behaviour.selector
import gg.mineral.bot.api.controls.Key
import gg.mineral.bot.api.controls.MouseButton
import gg.mineral.bot.api.entity.living.player.ClientPlayer
import gg.mineral.bot.api.inv.item.Item

class EatGappleBranch(tree: BehaviourTree) : BTBranch(tree) {
    private fun gappleSlot(): Int {
        val inventory = tree.clientInstance.fakePlayer.inventory
        for (i in 0..35)
            if (inventory.items[i]?.item?.id?.let { it == Item.GOLDEN_APPLE } == true)
                return i

        return -1
    }

    private fun canSeeEnemy(): Boolean {
        val fakePlayer = clientInstance.fakePlayer
        val world = fakePlayer.world

        val canSeeEnemy = world.entities
            .any {
                it is ClientPlayer
                        && !clientInstance.configuration.friendlyUUIDs.contains(it.uuid)
            }
        return canSeeEnemy
    }

    override val child: ChildNode = selector(tree) {
        sequence {
            condition { canSeeEnemy() }
            condition { clientInstance.fakePlayer.inventory.contains { it.item.id == Item.GOLDEN_APPLE } }
            selector {
                sequence {
                    condition { hotbarContains { item.id == Item.GOLDEN_APPLE } }
                    selector {
                        sequence {
                            condition { inventoryClosed() }
                            succeeder(leaf { aimAwayFromOptimalTarget() })

                            selector {
                                sequence {
                                    condition {
                                        val inventory = clientInstance.fakePlayer.inventory
                                        inventory.heldSlot == gappleSlot()
                                    }

                                    leaf {
                                        if (clientInstance.fakePlayer.usingItem) BTResult.SUCCESS
                                        else {
                                            pressButton(MouseButton.Type.RIGHT_CLICK)
                                            BTResult.RUNNING
                                        }
                                    }
                                }

                                leaf {
                                    val gappleSlot = gappleSlot()

                                    if (gappleSlot == -1)
                                        BTResult.FAILURE

                                    val inventory = tree.clientInstance.fakePlayer.inventory
                                    val heldSlot = inventory.heldSlot
                                    if (heldSlot != gappleSlot) {
                                        pressKey(10, Key.Type.valueOf("KEY_" + (gappleSlot + 1)))
                                        val using = clientInstance.fakePlayer.usingItem
                                        val rmbDown = getButton(MouseButton.Type.RIGHT_CLICK).isPressed
                                        println("Held slot: $heldSlot, Gapple slot: $gappleSlot, Using: $using, RMB down: $rmbDown")
                                        Thread.dumpStack()
                                        BTResult.RUNNING
                                    } else BTResult.SUCCESS
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
                            condition {
                                isHoveringOverIndex { gappleSlot() }
                            }

                            leaf { moveToHotbar { item.id == Item.GOLDEN_APPLE } }
                        }
                        leaf { moveCursorTo { gappleSlot() } }
                    }
                }

                leaf { openInventory() }
            }
        }
    }
}