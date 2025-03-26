package gg.mineral.bot.impl.behaviour.branch

import gg.mineral.bot.api.behaviour.BTResult
import gg.mineral.bot.api.behaviour.BehaviourTree
import gg.mineral.bot.api.behaviour.branch.BTBranch
import gg.mineral.bot.api.behaviour.node.ChildNode
import gg.mineral.bot.api.behaviour.selector
import gg.mineral.bot.api.behaviour.sequence
import gg.mineral.bot.api.controls.Key
import gg.mineral.bot.api.controls.MouseButton
import gg.mineral.bot.api.entity.living.player.ClientPlayer
import gg.mineral.bot.api.inv.item.Item

class EatFoodBranch(tree: BehaviourTree) : BTBranch(tree) {
    private fun foodSlot(): Int {
        val inventory = tree.clientInstance.fakePlayer.inventory
        for (i in 0..8)
            if (inventory.items[i]?.item?.id?.let { Item.Type.FOOD.isType(it) } == true)
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

    private val eatFood = sequence(tree) {
        condition { inventoryClosed() }

        condition {
            clientInstance.fakePlayer.eating
        }

        succeeder(leaf {
            pressButton(MouseButton.Type.RIGHT_CLICK)
            BTResult.SUCCESS
        })
    }

    override val child: ChildNode = selector(tree) {
        eatFood
        sequence {
            condition { clientInstance.fakePlayer.inventory.contains { Item.Type.FOOD.isType(it.item.id) } }
            condition { canSeeEnemy() }
            selector {
                sequence {
                    condition { hotbarContains { Item.Type.FOOD.isType(item.id) } }
                    selector {
                        sequence {
                            condition { inventoryClosed() }
                            succeeder(leaf { aimAtOptimalTarget() })

                            selector {
                                sequence {
                                    condition {
                                        val inventory = clientInstance.fakePlayer.inventory
                                        inventory.heldSlot == foodSlot()
                                    }

                                    leaf {
                                        if (clientInstance.fakePlayer.eating) BTResult.SUCCESS
                                        else {
                                            pressButton(10, MouseButton.Type.RIGHT_CLICK)
                                            BTResult.RUNNING
                                        }
                                    }
                                }

                                leaf {
                                    val potionSlot = foodSlot()

                                    if (potionSlot == -1)
                                        BTResult.FAILURE

                                    val inventory = tree.clientInstance.fakePlayer.inventory
                                    val heldSlot = inventory.heldSlot
                                    if (heldSlot != potionSlot) {
                                        pressKey(10, Key.Type.valueOf("KEY_" + (potionSlot + 1)))
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
                            condition {
                                isHoveringOverIndex { foodSlot() }
                            }

                            leaf { moveToHotbar { Item.Type.FOOD.isType(item.id) } }
                        }
                        leaf { moveCursorTo { foodSlot() } }
                    }
                }

                leaf { openInventory() }
            }
        }
    }
}