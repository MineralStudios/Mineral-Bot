package gg.mineral.bot.api.behaviour.branch

import gg.mineral.bot.api.behaviour.BTResult
import gg.mineral.bot.api.behaviour.BehaviourTree
import gg.mineral.bot.api.behaviour.node.DecoratorNode
import gg.mineral.bot.api.controls.Key
import gg.mineral.bot.api.entity.ClientEntity
import gg.mineral.bot.api.entity.living.player.ClientPlayer
import gg.mineral.bot.api.event.Event
import gg.mineral.bot.api.inv.item.ItemStack
import gg.mineral.bot.api.screen.type.ContainerScreen
import gg.mineral.bot.api.util.dsl.computeOptimalYawAndPitch
import kotlin.math.abs

abstract class BTBranch(tree: BehaviourTree) : DecoratorNode(tree) {
    override fun tick(): BTResult {
        return child.callTick()
    }

    abstract override fun <T : Event> event(event: T): Boolean

    // ─── NEW AIMING LOGIC ──────────────────────────────────────────────
    /**
     * Returns the optimal target (an enemy player) within the search range.
     * The criteria here is similar to the melee combat goal – the closest enemy
     * that is not marked as friendly.
     */
    private fun getOptimalTarget(): ClientPlayer? {
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

    /**
     * Updates the bot's aim so that it points at the optimal target.
     * This is called every tick while the goal is active.
     */
    protected fun aimAtOptimalTarget(): BTResult {
        val target = getOptimalTarget() ?: return BTResult.FAILURE
        return aimAt(target)
    }

    protected fun aimAt(entity: ClientEntity): BTResult {
        val fakePlayer = clientInstance.fakePlayer
        val optimalAngles = computeOptimalYawAndPitch(fakePlayer, entity)
        setMouseYaw(optimalAngles[1])
        setMousePitch(optimalAngles[0])
        return BTResult.SUCCESS
    }
    // ────────────────────────────────────────────────────────────────────

    protected fun inventoryClosed(): Boolean {
        val state = tree.clientInstance.currentScreen is ContainerScreen
        val opening =
            tree.clientInstance.keyboard.getKeyStateChanges()
                .any { !state && it.type == Key.Type.KEY_E && it.pressed }

        return !state && !opening
    }

    protected fun closeInventory(): BTResult {
        val state = tree.clientInstance.currentScreen is ContainerScreen
        val closing =
            tree.clientInstance.keyboard.getKeyStateChanges()
                .any { state && it.type == Key.Type.KEY_ESCAPE && it.pressed }

        if (state) {
            if (!closing)
                pressKey(10, Key.Type.KEY_ESCAPE)
            return BTResult.RUNNING
        }

        return BTResult.SUCCESS
    }

    protected fun openInventory(): BTResult {
        val state = tree.clientInstance.currentScreen is ContainerScreen
        val opening =
            tree.clientInstance.keyboard.getKeyStateChanges().any { !state && it.type == Key.Type.KEY_E && it.pressed }

        if (!state) {
            if (!opening)
                pressKey(10, Key.Type.KEY_E)
            return BTResult.RUNNING
        }

        return BTResult.SUCCESS
    }


    protected fun inventoryOpen(): Boolean {
        val state = tree.clientInstance.currentScreen is ContainerScreen
        val closing =
            tree.clientInstance.keyboard.getKeyStateChanges()
                .any { state && it.type == Key.Type.KEY_ESCAPE && it.pressed }

        return state && !closing
    }


    protected fun isHoveringOverIndex(index: () -> Int): Boolean {
        val fakePlayer = clientInstance.fakePlayer

        val screen = clientInstance.currentScreen

        val inventoryContainer = fakePlayer.inventoryContainer

        val inventory = fakePlayer.inventory

        val indexVal = index()

        if (indexVal < 0) return false

        val slot = inventoryContainer.getSlot(inventory, indexVal) ?: return false

        if (screen is ContainerScreen) {
            val guiSlotX = screen.getSlotXScaled(slot, clientInstance.displayWidth)
            val guiSlotY = screen.getSlotYScaled(slot, clientInstance.displayHeight)

            val currentX = clientInstance.mouse.x
            val currentY = clientInstance.mouse.y

            if (abs(currentX - guiSlotX) <= 1 && abs(currentY - guiSlotY) <= 1) return true
        }

        return false
    }

    protected fun moveCursorTo(index: () -> Int): BTResult {
        val fakePlayer = clientInstance.fakePlayer

        val screen = clientInstance.currentScreen

        val inventoryContainer = fakePlayer.inventoryContainer

        val inventory = fakePlayer.inventory

        val indexVal = index()

        if (indexVal < 0) return BTResult.FAILURE

        val slot = inventoryContainer.getSlot(inventory, indexVal) ?: return BTResult.FAILURE

        if (screen is ContainerScreen) {
            val guiSlotX = screen.getSlotXScaled(slot, clientInstance.displayWidth)
            val guiSlotY = screen.getSlotYScaled(slot, clientInstance.displayHeight)

            val currentX = clientInstance.mouse.x
            val currentY = clientInstance.mouse.y

            if (abs(currentX - guiSlotX) <= 1 && abs(currentY - guiSlotY) <= 1)
                return BTResult.SUCCESS

            clientInstance.mouse.setCursorPosition(guiSlotX, guiSlotY)
            return BTResult.RUNNING
        }

        return BTResult.FAILURE
    }

    protected fun moveToHotbar(selected: ItemStack.() -> Boolean): BTResult {
        val moving =
            tree.clientInstance.keyboard.getKeyStateChanges()
                .any { it.type == Key.Type.KEY_9 && it.pressed }

        val inventory = tree.clientInstance.fakePlayer.inventory
        var inHotbar = false
        for (i in 0..8)
            if (inventory.items[i]?.selected() == true) {
                inHotbar = true
                break
            }

        if (!inHotbar) {
            if (!moving) pressKey(10, Key.Type.KEY_9)
            return BTResult.RUNNING
        }

        return BTResult.SUCCESS
    }

    protected fun hotbarContains(selected: ItemStack.() -> Boolean): Boolean {
        val inventory = tree.clientInstance.fakePlayer.inventory
        for (i in 0..8)
            if (inventory.items[i]?.selected() == true)
                return true

        return false
    }
}