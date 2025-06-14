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
import kotlin.test.assertTrue
import kotlin.test.assertFalse

// Mock tree for sequence testing
class SequenceTestBehaviourTree : BehaviourTree(clientInstance = object : ClientInstance {
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
 * Tests to reproduce the potion -> gapple sequence issue
 */
class BehaviourTreeSequenceTests {
    private val tree = SequenceTestBehaviourTree()

    @Test
    fun `Root selector should handle RUNNING state from DrinkPotion then EatGapple correctly`() {
        // Simulate the RootNode behavior sequence
        var drinkPotionState = "READY"  // READY -> DRINKING -> FINISHED
        var eatGappleState = "READY"    // READY -> EATING -> FINISHED
        var mouseButtonPressed = false
        var isUsingItem = false


        // Simulate RootNode as selector
        val rootSelector = selector(tree) {
            // This mimics the RootNode structure
            leaf {
                val result = if (isUsingItem) {
                    BTResult.RUNNING  // Still consuming
                } else if (mouseButtonPressed) {
                    mouseButtonPressed = false  // Unpress button
                    BTResult.SKIP
                } else {
                    BTResult.FAILURE  // Not consuming
                }
                result
            }
            
            leaf {
                val result = when (drinkPotionState) {
                    "READY" -> {
                        drinkPotionState = "DRINKING"
                        mouseButtonPressed = true
                        isUsingItem = true
                        BTResult.RUNNING
                    }
                    "DRINKING" -> {
                        drinkPotionState = "FINISHED"
                        isUsingItem = false
                        BTResult.SUCCESS
                    }
                    else -> BTResult.FAILURE
                }
                result
            }
            
            leaf {
                val result = when (eatGappleState) {
                    "READY" -> {
                        if (drinkPotionState != "FINISHED") {
                            BTResult.FAILURE  // Can't eat while drinking
                        } else {
                            eatGappleState = "EATING"
                            mouseButtonPressed = true
                            isUsingItem = true
                            BTResult.RUNNING
                        }
                    }
                    "EATING" -> {
                        eatGappleState = "FINISHED"
                        isUsingItem = false
                        BTResult.SUCCESS
                    }
                    else -> BTResult.FAILURE
                }
                result
            }
        }

        // First tick: should start drinking potion
        val tick1 = rootSelector.tick()
        assertEquals(BTResult.RUNNING, tick1)
        assertEquals("DRINKING", drinkPotionState)
        assertTrue(isUsingItem)

        // Second tick: finishConsumingNode should handle RUNNING state
        val tick2 = rootSelector.tick()
        assertEquals(BTResult.RUNNING, tick2)
        assertTrue(isUsingItem)

        // Third tick: finish drinking
        isUsingItem = false  // Simulate potion consumption finished
        val tick3 = rootSelector.tick()
        // finishConsumingNode returns SKIP, drinkPotionNode returns SUCCESS
        assertEquals(BTResult.SUCCESS, tick3)  // Potion finished successfully
        assertFalse(mouseButtonPressed)  // Button should be unpressed by finishConsumingNode
        assertEquals("FINISHED", drinkPotionState)

        // Fourth tick: should start eating gapple
        val tick4 = rootSelector.tick()
        // finishConsumingNode returns FAILURE, drinkPotionNode returns FAILURE, eatGappleNode starts
        assertEquals(BTResult.RUNNING, tick4)  // Gapple should start eating
        assertEquals("EATING", eatGappleState)
        assertTrue(isUsingItem)

        // Fifth tick: should handle gapple eating RUNNING state
        val tick5 = rootSelector.tick()
        assertEquals(BTResult.RUNNING, tick5)

        // Sixth tick: finish eating gapple
        isUsingItem = false
        val tick6 = rootSelector.tick()
        assertEquals(BTResult.SUCCESS, tick6)
        assertEquals("FINISHED", eatGappleState)
    }

    @Test
    fun `Sequence should not get stuck when child returns RUNNING then SUCCESS`() {
        var state = "INITIAL"
        
        val sequence = sequence(tree) {
            condition { true }  // Always pass
            leaf {
                when (state) {
                    "INITIAL" -> {
                        state = "RUNNING"
                        BTResult.RUNNING
                    }
                    "RUNNING" -> {
                        state = "SUCCESS"
                        BTResult.SUCCESS
                    }
                    else -> BTResult.SUCCESS
                }
            }
            condition { true }  // Always pass
        }

        // First tick: should return RUNNING
        val tick1 = sequence.tick()
        assertEquals(BTResult.RUNNING, tick1)
        assertEquals("RUNNING", state)

        // Second tick: should complete sequence
        val tick2 = sequence.tick()
        assertEquals(BTResult.SUCCESS, tick2)
        assertEquals("SUCCESS", state)
    }

    @Test
    fun `Selector priority should work correctly after RUNNING state resolves`() {
        var highPriorityReady = false
        var lowPriorityExecuted = false

        val selector = selector(tree) {
            // High priority task
            leaf {
                if (highPriorityReady) BTResult.SUCCESS
                else BTResult.FAILURE
            }
            
            // Low priority task that sets up high priority
            leaf {
                lowPriorityExecuted = true
                highPriorityReady = true
                BTResult.SUCCESS
            }
        }

        // First tick: high priority fails, low priority succeeds
        val tick1 = selector.tick()
        assertEquals(BTResult.SUCCESS, tick1)
        assertTrue(lowPriorityExecuted)
        assertTrue(highPriorityReady)

        // Second tick: high priority should now succeed
        lowPriorityExecuted = false
        val tick2 = selector.tick()
        assertEquals(BTResult.SUCCESS, tick2)
        assertFalse(lowPriorityExecuted)  // Low priority shouldn't execute
    }

    @Test
    fun `Button state should not interfere between different branches`() {
        var branch1ButtonState = false
        var branch2ButtonState = false
        var globalButtonPressed = false
        var branch1Completed = false


        val selector = selector(tree) {
            leaf {
                val result = if (branch1Completed) {
                    BTResult.FAILURE  // Already completed, don't run again
                } else if (!branch1ButtonState) {
                    branch1ButtonState = true
                    globalButtonPressed = true
                    BTResult.RUNNING
                } else {
                    branch1ButtonState = false
                    globalButtonPressed = false
                    branch1Completed = true
                    BTResult.SUCCESS
                }
                result
            }
            
            leaf {
                val result = if (globalButtonPressed) {
                    BTResult.FAILURE  // Can't use button while pressed
                } else {
                    branch2ButtonState = true
                    globalButtonPressed = true
                    BTResult.SUCCESS
                }
                result
            }
        }

        // First tick: branch1 starts and presses button
        val tick1 = selector.tick()
        assertEquals(BTResult.RUNNING, tick1)
        assertTrue(globalButtonPressed)

        // Second tick: branch1 finishes and releases button
        val tick2 = selector.tick()
        assertEquals(BTResult.SUCCESS, tick2)
        assertFalse(globalButtonPressed)

        // Third tick: branch2 should be able to use button now
        val tick3 = selector.tick()
        // branch1 now returns FAILURE (already completed), so selector tries branch2 which succeeds
        assertEquals(BTResult.SUCCESS, tick3)
        assertTrue(branch2ButtonState)
    }

    @Test
    fun `State machine transitions should work correctly in behavior tree`() {
        var currentState = "IDLE"
        val validTransitions = mapOf(
            "IDLE" to listOf("DRINKING"),
            "DRINKING" to listOf("DRINKING_COMPLETE"),
            "DRINKING_COMPLETE" to listOf("EATING"),
            "EATING" to listOf("EATING_COMPLETE"),
            "EATING_COMPLETE" to listOf("IDLE")
        )

        fun canTransition(from: String, to: String): Boolean {
            return validTransitions[from]?.contains(to) ?: false
        }

        val stateMachine = selector(tree) {
            // Drink potion state
            leaf {
                when (currentState) {
                    "IDLE" -> {
                        if (canTransition("IDLE", "DRINKING")) {
                            currentState = "DRINKING"
                            BTResult.RUNNING
                        } else BTResult.FAILURE
                    }
                    "DRINKING" -> {
                        if (canTransition("DRINKING", "DRINKING_COMPLETE")) {
                            currentState = "DRINKING_COMPLETE"
                            BTResult.SUCCESS
                        } else BTResult.RUNNING
                    }
                    else -> BTResult.FAILURE
                }
            }

            // Eat gapple state
            leaf {
                when (currentState) {
                    "DRINKING_COMPLETE" -> {
                        if (canTransition("DRINKING_COMPLETE", "EATING")) {
                            currentState = "EATING"
                            BTResult.RUNNING
                        } else BTResult.FAILURE
                    }
                    "EATING" -> {
                        if (canTransition("EATING", "EATING_COMPLETE")) {
                            currentState = "EATING_COMPLETE"
                            BTResult.SUCCESS
                        } else BTResult.RUNNING
                    }
                    else -> BTResult.FAILURE
                }
            }
        }

        // Test the state transitions
        assertEquals("IDLE", currentState)

        val tick1 = stateMachine.tick()
        assertEquals(BTResult.RUNNING, tick1)
        assertEquals("DRINKING", currentState)

        val tick2 = stateMachine.tick()
        assertEquals(BTResult.SUCCESS, tick2)
        assertEquals("DRINKING_COMPLETE", currentState)

        val tick3 = stateMachine.tick()
        assertEquals(BTResult.RUNNING, tick3)
        assertEquals("EATING", currentState)

        val tick4 = stateMachine.tick()
        assertEquals(BTResult.SUCCESS, tick4)
        assertEquals("EATING_COMPLETE", currentState)
    }
}