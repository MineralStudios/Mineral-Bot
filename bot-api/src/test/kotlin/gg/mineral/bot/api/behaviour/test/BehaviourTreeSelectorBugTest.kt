package gg.mineral.bot.api.behaviour.test

import gg.mineral.bot.api.behaviour.*
import gg.mineral.bot.api.behaviour.node.BTNode
import gg.mineral.bot.api.behaviour.node.ChildNode
import gg.mineral.bot.api.behaviour.node.composite.SequenceNode
import gg.mineral.bot.api.configuration.BotConfiguration
import gg.mineral.bot.api.controls.Keyboard
import gg.mineral.bot.api.controls.Mouse
import gg.mineral.bot.api.entity.living.player.FakePlayer
import gg.mineral.bot.api.event.Event
import gg.mineral.bot.api.instance.ClientInstance
import gg.mineral.bot.api.instance.Session
import gg.mineral.bot.api.screen.Screen
import java.util.concurrent.ExecutorService
import java.util.concurrent.ScheduledExecutorService
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

// Test tree for selector bug testing
class SelectorBugTestTree : BehaviourTree(clientInstance = object : ClientInstance {
    override var behaviourTree: BehaviourTree? = null
    override val latency: Int = 0
    override val currentTick: Int = 0
    override val currentScreen: Screen? = null
    override val configuration: BotConfiguration
        get() = TODO("Not implemented")
    override val keyboard: Keyboard
        get() = TODO("Not implemented")
    override val mouse: Mouse
        get() = TODO("Not implemented")
    override val fakePlayer: FakePlayer
        get() = TODO("Not implemented")
    override val isRunning: Boolean = true
    override fun timeMillis(): Long = System.currentTimeMillis()
    override val gameLoopExecutor: ScheduledExecutorService
        get() = TODO("Not implemented")
    override val asyncExecutor: ExecutorService
        get() = TODO("Not implemented")
    override fun schedule(runnable: Runnable, delay: Long): Boolean = true
    override val session: Session
        get() = TODO("Not implemented")
    override fun shutdown() {}
    override fun newMouse(): Mouse = TODO("Not implemented")
    override fun newKeyboard(): Keyboard = TODO("Not implemented")
    override val displayHeight: Int = 1080
    override val displayWidth: Int = 1920
    override fun <T : Event> callEvent(event: T): Boolean = true
}) {
    override val rootNode: BTNode
        get() = object : SequenceNode(this) {
            override val children = arrayOf<ChildNode>()
        }
}

/**
 * Test to identify the specific selector behavior bug causing potion->gapple issues
 */
class BehaviourTreeSelectorBugTest {
    private val tree = SelectorBugTestTree()

    @Test
    fun `Selector should correctly handle RUNNING state from first child`() {
        var firstChildState = "READY"
        var secondChildCalled = false

        val selector = selector(tree) {
            // First child: starts with FAILURE, then RUNNING, then SUCCESS
            leaf {
                when (firstChildState) {
                    "READY" -> {
                        firstChildState = "RUNNING"
                        BTResult.RUNNING
                    }
                    "RUNNING" -> {
                        firstChildState = "SUCCESS"
                        BTResult.SUCCESS
                    }
                    else -> BTResult.SUCCESS
                }
            }

            // Second child: should NEVER be called when first child is RUNNING
            leaf {
                secondChildCalled = true
                BTResult.SUCCESS
            }
        }

        // First tick: first child should return RUNNING
        val tick1 = selector.tick()
        assertEquals(BTResult.RUNNING, tick1)
        assertEquals("RUNNING", firstChildState)
        assertEquals(false, secondChildCalled) // Second child should NOT be called

        // Reset for next test
        secondChildCalled = false

        // Second tick: first child should return SUCCESS
        val tick2 = selector.tick()
        assertEquals(BTResult.SUCCESS, tick2)
        assertEquals("SUCCESS", firstChildState)
        assertEquals(false, secondChildCalled) // Second child should STILL not be called
    }

    @Test
    fun `Selector should continue to next child only when current child returns FAILURE or SKIP`() {
        var callOrder = mutableListOf<String>()

        val selector = selector(tree) {
            leaf {
                callOrder.add("Child1")
                BTResult.FAILURE  // This should cause selector to try next child
            }

            leaf {
                callOrder.add("Child2")
                BTResult.RUNNING  // This should stop selector and return RUNNING
            }

            leaf {
                callOrder.add("Child3")
                BTResult.SUCCESS  // This should NEVER be called
            }
        }

        val result = selector.tick()
        
        assertEquals(BTResult.RUNNING, result)
        assertEquals(listOf("Child1", "Child2"), callOrder)
        // Child3 should never be called because Child2 returned RUNNING
    }

    @Test
    fun `Selector execution should be deterministic and not depend on previous state`() {
        var child1Result = BTResult.FAILURE
        var child2CallCount = 0

        val selector = selector(tree) {
            leaf { child1Result }
            leaf {
                child2CallCount++
                BTResult.SUCCESS
            }
        }

        // First execution: child1 fails, child2 should be called
        val tick1 = selector.tick()
        assertEquals(BTResult.SUCCESS, tick1)
        assertEquals(1, child2CallCount)

        // Change child1 to succeed
        child1Result = BTResult.SUCCESS

        // Second execution: child1 succeeds, child2 should NOT be called
        val tick2 = selector.tick()
        assertEquals(BTResult.SUCCESS, tick2)
        assertEquals(1, child2CallCount) // Should still be 1, not 2
    }

    @Test
    fun `Recreate the exact potion-gapple scenario that causes the bug`() {
        var usingItem = false
        var buttonPressed = false
        var potionDrunk = false
        var gappleEaten = false

        // This mimics the exact RootNode structure that's causing the issue
        val rootSelector = selector(tree) {
            // "If already consuming, finish consuming" node (lines 41-48 in RootNode)
            leaf {
                if (usingItem) {
                    BTResult.RUNNING  // Still consuming
                } else if (buttonPressed) {
                    buttonPressed = false  // Unpress button
                    BTResult.SKIP
                } else {
                    BTResult.FAILURE  // Not consuming
                }
            }

            // DrinkPotionBranch simulation
            leaf {
                if (potionDrunk) {
                    BTResult.FAILURE  // Already drunk potion
                } else if (!usingItem) {  // Only start if not already using an item
                    potionDrunk = true
                    buttonPressed = true
                    usingItem = true
                    BTResult.RUNNING  // Start drinking
                } else {
                    BTResult.FAILURE  // Already using an item
                }
            }

            // EatGappleBranch simulation  
            leaf {
                if (gappleEaten) {
                    BTResult.FAILURE  // Already eaten gapple
                } else if (potionDrunk && !usingItem) {  // Only if potion drunk and not using item
                    gappleEaten = true
                    buttonPressed = true
                    usingItem = true
                    BTResult.RUNNING  // Start eating
                } else {
                    BTResult.FAILURE  // Can't eat yet (either no potion or still using item)
                }
            }
        }

        // Tick 1: Should start drinking potion
        val tick1 = rootSelector.tick()
        assertEquals(BTResult.RUNNING, tick1)

        // Tick 2: Should continue drinking (finishConsuming should return RUNNING)
        val tick2 = rootSelector.tick()
        assertEquals(BTResult.RUNNING, tick2)

        // Tick 3: Finish drinking potion and start eating gapple
        usingItem = false  // Potion finished
        val tick3 = rootSelector.tick()
        // The selector continues after SKIP and finds the gapple branch
        assertEquals(BTResult.RUNNING, tick3)  // Gapple should start

        // Tick 4: Continue eating gapple
        val tick4 = rootSelector.tick()
        assertEquals(BTResult.RUNNING, tick4)  // Should continue eating gapple

        // Tick 5: After gapple finishes, all actions are complete
        usingItem = false  // Gapple finished
        val tick5 = rootSelector.tick()
        // All children return FAILURE or SKIP, so selector returns FAILURE
        // This is correct - no more actions available
        assertEquals(BTResult.FAILURE, tick5)  
        
        // The sequence completed successfully: potion → gapple → done
        assertTrue(potionDrunk, "Potion should have been consumed")
        assertTrue(gappleEaten, "Gapple should have been consumed")
        assertFalse(buttonPressed, "Button should have been unpressed")
        assertFalse(usingItem, "Should not be using any item")
    }
}