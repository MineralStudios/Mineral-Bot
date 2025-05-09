package gg.mineral.bot.impl.behaviour.branch

import gg.mineral.bot.api.behaviour.BTResult
import gg.mineral.bot.api.behaviour.BehaviourTree
import gg.mineral.bot.api.behaviour.branch.BTBranch
import gg.mineral.bot.api.behaviour.node.ChildNode
import gg.mineral.bot.api.behaviour.selector
import gg.mineral.bot.api.controls.Key
import gg.mineral.bot.api.inv.item.Item
import gg.mineral.bot.api.screen.type.ContainerScreen

class DropEmptyBowlBranch(tree: BehaviourTree) : BTBranch(tree) {

    /**
     * Returns the hotbar slot (0..8) that holds a bowl with count <= 1.
     * Returns -1 if no such bowl exists.
     */
    private fun bowlSlot(): Int {
        val inventory = tree.clientInstance.fakePlayer.inventory
        for (i in 0..8) {
            val itemStack = inventory.items[i] ?: continue
            if (itemStack.item.id == Item.BOWL && itemStack.count <= 1) {
                return i
            }
        }
        return -1
    }

    override val child: ChildNode = selector(tree) {
        // If the bowl is no longer in the hotbar (i.e. it was dropped), finish immediately.
        sequence {
            condition {
                bowlSlot() != -1
            }

            sequence {
                // Optionally start a movement action (pressing W + LCONTROL) once.
                succeeder(leaf {
                    pressKey(Key.Type.KEY_W, Key.Type.KEY_LCONTROL)
                    BTResult.SUCCESS
                })
                // Continuously aim at the optimal target.
                succeeder(leaf {
                    aimAtOptimalTarget()
                    BTResult.SUCCESS
                })
                // Ensure the inventory is closed; if not, press ESCAPE.
                selector {
                    condition { tree.clientInstance.currentScreen !is ContainerScreen }
                    leaf {
                        pressKey(10, Key.Type.KEY_ESCAPE)
                        BTResult.SUCCESS
                    }
                }
                // Switch to the bowl slot if it isn’t already selected.
                selector {
                    condition {
                        val slot = bowlSlot()
                        tree.clientInstance.fakePlayer.inventory.heldSlot == slot
                    }
                    leaf {
                        val slot = bowlSlot()
                        if (slot != -1) {
                            pressKey(10, Key.Type.valueOf("KEY_" + (slot + 1)))
                            BTResult.RUNNING
                        } else BTResult.FAILURE
                    }
                }
                // Verify that the bowl is still in hand; if not, this branch is done.
                condition {
                    tree.clientInstance.fakePlayer.inventory.heldItemStack?.item?.id == Item.BOWL
                }
                // Finally, drop the bowl.
                leaf {
                    pressKey(10, Key.Type.KEY_Q)
                    BTResult.SUCCESS
                }
            }
        }
    }
}
