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

// Test tree for stack bug testing
class StackBugTestTree : BehaviourTree(clientInstance = object : ClientInstance {
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
 * Test to identify the exact stack/state management bug
 */
class BehaviourTreeStackBugTest {
    private val tree = StackBugTestTree()

    @Test
    fun `Identify the exact cause of the potion-gapple bug`() {
        var step = 0
        var nodesCalled = mutableListOf<String>()

        val rootSelector = selector(tree) {
            // Node 1: "Finish consuming" simulation
            leaf {
                nodesCalled.add("FinishConsuming")
                when (step) {
                    0 -> BTResult.FAILURE  // Not consuming initially
                    1, 2 -> BTResult.RUNNING  // Consuming potion
                    3 -> BTResult.SKIP  // Finish consuming, unpress button
                    4 -> BTResult.FAILURE  // Not consuming, ready for next action
                    5 -> BTResult.RUNNING  // Consuming gapple
                    6 -> BTResult.SKIP  // Finish consuming gapple
                    else -> BTResult.FAILURE
                }
            }

            // Node 2: "Drink potion" simulation  
            leaf {
                nodesCalled.add("DrinkPotion")
                when (step) {
                    0 -> {
                        step = 1
                        BTResult.RUNNING  // Start drinking
                    }
                    1, 2 -> BTResult.RUNNING  // Continue drinking
                    else -> BTResult.FAILURE  // Already drunk
                }
            }

            // Node 3: "Eat gapple" simulation
            leaf {
                nodesCalled.add("EatGapple") 
                when (step) {
                    4 -> {
                        step = 5
                        BTResult.RUNNING  // Start eating
                    }
                    5 -> BTResult.RUNNING  // Continue eating
                    else -> BTResult.FAILURE  // Can't eat yet
                }
            }
        }

        println("=== Testing Stack State Bug ===")

        // Step 0: Initial state - should start drinking potion
        nodesCalled.clear()
        val tick0 = rootSelector.tick()
        println("Step $step: $tick0, nodes called: $nodesCalled")
        assertEquals(BTResult.RUNNING, tick0)
        assertEquals(listOf("FinishConsuming", "DrinkPotion"), nodesCalled)

        // Step 1: Drinking potion - finish consuming should handle it
        nodesCalled.clear()
        val tick1 = rootSelector.tick()
        println("Step $step: $tick1, nodes called: $nodesCalled")
        assertEquals(BTResult.RUNNING, tick1)
        assertEquals(listOf("FinishConsuming"), nodesCalled)

        // Step 2: Still drinking potion
        nodesCalled.clear()
        val tick2 = rootSelector.tick()
        println("Step $step: $tick2, nodes called: $nodesCalled")
        assertEquals(BTResult.RUNNING, tick2)
        assertEquals(listOf("FinishConsuming"), nodesCalled)

        // Step 3: Finish drinking potion
        step = 3
        nodesCalled.clear()
        val tick3 = rootSelector.tick()
        println("Step $step: $tick3, nodes called: $nodesCalled")
        // FinishConsuming returns SKIP, but selector continues to other children
        // All children return FAILURE, so selector returns FAILURE
        assertEquals(BTResult.FAILURE, tick3)
        assertEquals(listOf("FinishConsuming", "DrinkPotion", "EatGapple"), nodesCalled)

        // Step 4: Should try to eat gapple now
        step = 4
        nodesCalled.clear()
        val tick4 = rootSelector.tick()
        println("Step $step: $tick4, nodes called: $nodesCalled")
        
        // THIS IS THE CRITICAL TEST - if this fails, we've found the bug
        assertEquals(BTResult.RUNNING, tick4)
        // The selector should call FinishConsuming first (returns FAILURE), 
        // then DrinkPotion (returns FAILURE), then EatGapple (returns RUNNING)
        assertEquals(listOf("FinishConsuming", "DrinkPotion", "EatGapple"), nodesCalled)
    }

    @Test
    fun `Test BehaviourTree stack clearing behavior`() {
        // This tests whether the BehaviourTree properly clears stacks between ticks
        
        val node1 = leaf(tree) { BTResult.RUNNING }
        val node2 = leaf(tree) { BTResult.SUCCESS }
        
        // Manually call nodes to populate stack
        node1.callTick(tree.tickTreeStack)
        node2.callTick(tree.tickTreeStack)
        
        println("Stack size after manual calls: ${tree.tickTreeStack.size}")
        
        // Now call the tree's tick method - this should clear the stack
        val rootResult = tree.tick()
        println("Root result: $rootResult")
        println("Stack size after tree tick: ${tree.tickTreeStack.size}")
        
        // The stack should be properly managed
        // If it's not cleared correctly, it could cause state issues
    }

    @Test
    fun `Test selector behavior with changing child results`() {
        var child1Result = BTResult.FAILURE
        var child2Result = BTResult.FAILURE
        var child3Result = BTResult.SUCCESS
        
        val selector = selector(tree) {
            leaf { child1Result }
            leaf { child2Result }
            leaf { child3Result }
        }

        // First tick: should return SUCCESS (from child3)
        val tick1 = selector.tick()
        assertEquals(BTResult.SUCCESS, tick1)

        // Change child1 to return RUNNING
        child1Result = BTResult.RUNNING

        // Second tick: should return RUNNING (from child1)
        val tick2 = selector.tick()
        assertEquals(BTResult.RUNNING, tick2)

        // Change child1 back to FAILURE, child2 to RUNNING
        child1Result = BTResult.FAILURE
        child2Result = BTResult.RUNNING

        // Third tick: should return RUNNING (from child2)
        val tick3 = selector.tick()
        assertEquals(BTResult.RUNNING, tick3)
    }
}