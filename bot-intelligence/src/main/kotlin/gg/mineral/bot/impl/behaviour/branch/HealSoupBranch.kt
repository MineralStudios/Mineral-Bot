package gg.mineral.bot.impl.behaviour.branch

import gg.mineral.bot.api.behaviour.BTResult
import gg.mineral.bot.api.behaviour.BehaviourTree
import gg.mineral.bot.api.behaviour.branch.BTBranch
import gg.mineral.bot.api.behaviour.node.ChildNode
import gg.mineral.bot.api.behaviour.sequence
import gg.mineral.bot.api.controls.Key
import gg.mineral.bot.api.controls.MouseButton
import gg.mineral.bot.api.inv.item.Item

class HealSoupBranch(tree: BehaviourTree) : BTBranch(tree) {

    private fun soupSlot(): Int {
        val inventory = tree.clientInstance.fakePlayer.inventory
        for (i in 0..8)
            if (inventory.items[i]?.item?.id?.let { it == Item.MUSHROOM_STEW } == true)
                return i

        return -1
    }

    override val child: ChildNode = sequence(tree) {
        condition { clientInstance.fakePlayer.inventory.contains(Item.MUSHROOM_STEW) }
        condition { clientInstance.fakePlayer.health < 10 }
        selector {
            sequence {
                condition { hotbarContains { item.id == Item.MUSHROOM_STEW } }
                selector {
                    sequence {
                        condition { inventoryClosed() }
                        succeeder(leaf { aimAtOptimalTarget() })

                        selector {
                            sequence {
                                condition {
                                    val inventory = clientInstance.fakePlayer.inventory
                                    inventory.heldSlot == soupSlot()
                                }

                                leaf {
                                    val inventory = tree.clientInstance.fakePlayer.inventory

                                    if (inventory.heldItemStack?.item?.id?.let { it != Item.MUSHROOM_STEW } == true) BTResult.SUCCESS
                                    else {
                                        pressButton(10, MouseButton.Type.RIGHT_CLICK)
                                        BTResult.RUNNING
                                    }
                                }
                            }

                            leaf {
                                val soupSlot = soupSlot()

                                if (soupSlot == -1)
                                    BTResult.FAILURE

                                val inventory = tree.clientInstance.fakePlayer.inventory
                                val heldSlot = inventory.heldSlot
                                if (heldSlot != soupSlot) {
                                    pressKey(10, Key.Type.valueOf("KEY_" + (soupSlot + 1)))
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
                            isHoveringOverIndex { soupSlot() }
                        }

                        leaf { moveToHotbar { item.id == Item.MUSHROOM_STEW } }
                    }
                    leaf { moveCursorTo { soupSlot() } }
                }
            }

            leaf { openInventory() }
        }
    }
}