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
import gg.mineral.bot.api.event.Event
import gg.mineral.bot.api.inv.item.Item
import gg.mineral.bot.api.inv.item.ItemStack
import gg.mineral.bot.api.inv.potion.Potion

class DrinkPotionBranch(tree: BehaviourTree) : BTBranch(tree) {
    private fun potionSlot(): Int {
        val inventory = tree.clientInstance.fakePlayer.inventory
        for (i in 0..8)
            if (inventory.items[i]?.let { isValidPotion(it) } == true)
                return i

        return -1
    }

    private fun isValidPotion(potion: Potion): Boolean {
        val fakePlayer = clientInstance.fakePlayer
        // TODO: exclude negative potions
        for (effect in potion.effects) if (fakePlayer.isPotionActive(effect.potionID)) return false
        return !potion.isSplash && potion.effects.isNotEmpty()
    }

    private fun isValidPotion(itemStack: ItemStack): Boolean {
        if (itemStack.item.id != Item.POTION) return false
        val potion = itemStack.potion ?: return false
        return isValidPotion(potion)
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

    private val drinkPot = sequence(tree) {
        condition { inventoryClosed() }

        condition {
            clientInstance.fakePlayer.eating
        }

        succeeder(leaf {
            pressButton(MouseButton.Type.RIGHT_CLICK)
            BTResult.SUCCESS
        })
    }

    override fun <T : Event> event(event: T): Boolean {
        return false
    }

    override val child: ChildNode = selector(tree) {
        drinkPot
        sequence {
            condition { clientInstance.fakePlayer.inventory.containsPotion { isValidPotion(it) } }
            condition { canSeeEnemy() }
            selector {
                sequence {
                    condition { hotbarContains { isValidPotion(this) } }
                    selector {
                        sequence {
                            condition { inventoryClosed() }
                            succeeder(leaf { aimAtOptimalTarget() })

                            selector {
                                sequence {
                                    condition {
                                        val inventory = clientInstance.fakePlayer.inventory
                                        inventory.heldSlot == potionSlot()
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
                                    val potionSlot = potionSlot()

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
                                isHoveringOverIndex { potionSlot() }
                            }

                            leaf { moveToHotbar { isValidPotion(this) } }
                        }
                        leaf { moveCursorTo { potionSlot() } }
                    }
                }

                leaf { openInventory() }
            }
        }
    }
}