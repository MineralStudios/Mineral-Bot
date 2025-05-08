package gg.mineral.bot.impl.behaviour.branch

import gg.mineral.bot.api.behaviour.BTResult
import gg.mineral.bot.api.behaviour.BehaviourTree
import gg.mineral.bot.api.behaviour.branch.BTBranch
import gg.mineral.bot.api.behaviour.node.ChildNode
import gg.mineral.bot.api.behaviour.sequence
import gg.mineral.bot.api.controls.Key
import gg.mineral.bot.api.controls.MouseButton
import gg.mineral.bot.api.event.Event
import gg.mineral.bot.api.inv.Inventory
import gg.mineral.bot.api.inv.item.Item
import gg.mineral.bot.api.inv.item.ItemStack

class ReplaceArmorBranch(tree: BehaviourTree, val type: Item.Type, armorPiece: Inventory.() -> ItemStack?) :
    BTBranch(tree) {

    private fun armorSlot(): Int {
        val inventory = tree.clientInstance.fakePlayer.inventory
        for (i in 0..8)
            if (inventory.items[i]?.item?.id?.let { type.isType(it) } == true)
                return i

        return -1
    }

    override val child: ChildNode = sequence(tree) {
        condition { clientInstance.fakePlayer.inventory.let { it.armorPiece() == null && it.contains(type) } }
        selector {
            sequence {
                condition { hotbarContains { type.isType(item.id) } }
                selector {
                    sequence {
                        condition { inventoryClosed() }
                        succeeder(leaf { aimAtOptimalTarget() })

                        selector {
                            sequence {
                                condition {
                                    val inventory = clientInstance.fakePlayer.inventory
                                    inventory.heldSlot == armorSlot()
                                }

                                leaf {
                                    val inventory = tree.clientInstance.fakePlayer.inventory

                                    if (inventory.armorPiece() != null) BTResult.SUCCESS
                                    else {
                                        pressButton(10, MouseButton.Type.RIGHT_CLICK)
                                        BTResult.RUNNING
                                    }
                                }
                            }

                            leaf {
                                val armorSlot = armorSlot()

                                if (armorSlot == -1)
                                    BTResult.FAILURE

                                val inventory = tree.clientInstance.fakePlayer.inventory
                                val heldSlot = inventory.heldSlot
                                if (heldSlot != armorSlot) {
                                    pressKey(10, Key.Type.valueOf("KEY_" + (armorSlot + 1)))
                                    BTResult.RUNNING
                                }

                                BTResult.SUCCESS
                            }
                        }
                    }

                    leaf { closeInventory() } // Close inventory
                }
            }

            sequence {
                condition { inventoryOpen() }

                selector {
                    sequence {
                        condition {
                            isHoveringOverIndex { armorSlot() }
                        }

                        leaf { moveToHotbar { type.isType(item.id) } }
                    }

                    leaf { moveCursorTo { armorSlot() } }
                }
            }

            leaf { openInventory() }
        }
    }

    override fun <T : Event> event(event: T) {
        TODO("Not yet implemented")
    }
}