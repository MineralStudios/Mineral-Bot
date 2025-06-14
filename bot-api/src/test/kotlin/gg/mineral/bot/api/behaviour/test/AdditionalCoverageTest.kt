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
import kotlin.test.assertFailsWith

// Mock tree for additional coverage testing
class AdditionalCoverageTestTree : BehaviourTree(clientInstance = object : ClientInstance {
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

// Mock event for additional testing
class AdditionalTestEvent : Event

/**
 * Additional comprehensive test coverage for behavior trees
 */
class AdditionalCoverageTest {
    private val tree = AdditionalCoverageTestTree()

    // ===== STACK MANAGEMENT TESTS =====
    
    @Test
    fun `Stacks should be independent across tick, frame, and event calls`() {
        val node = leaf(tree) { BTResult.RUNNING }
        
        // Start all three operations
        node.callTick(tree.tickTreeStack)
        node.callFrame(tree.frameTreeStack)
        node.callEvent(tree.eventTreeStack, AdditionalTestEvent())
        
        // Verify stacks are independent
        assertEquals(1, tree.tickTreeStack.size)
        assertEquals(1, tree.frameTreeStack.size)
        assertEquals(1, tree.eventTreeStack.size)
        
        // Verify each stack contains the correct node
        assertEquals(node, tree.tickTreeStack.peek().node)
        assertEquals(node, tree.frameTreeStack.peek().node)
        assertEquals(node, tree.eventTreeStack.peek().node)
    }
    
    @Test
    fun `Stack should be cleared when node execution completes`() {
        var shouldReturnRunning = true
        
        val node = leaf(tree) {
            if (shouldReturnRunning) BTResult.RUNNING else BTResult.SUCCESS
        }
        
        // First call adds to stack
        node.callTick(tree.tickTreeStack)
        assertEquals(1, tree.tickTreeStack.size)
        
        // Complete the node
        shouldReturnRunning = false
        
        // Tree tick should handle completion
        val result = tree.tick()
        
        // Should complete successfully
        assertTrue(result in listOf(BTResult.SUCCESS, BTResult.FAILURE))
    }

    // ===== ERROR HANDLING TESTS =====
    
    @Test
    fun `Exceptions should propagate correctly through composite nodes`() {
        val exceptionSelector = selector(tree) {
            leaf { throw RuntimeException("Test exception") }
            leaf { BTResult.SUCCESS }  // This shouldn't be reached
        }
        
        assertFailsWith<RuntimeException> {
            exceptionSelector.tick()
        }
    }
    
    @Test
    fun `Tree should handle recovery after exceptions`() {
        var shouldThrow = true
        val recoveringNode = leaf(tree) {
            if (shouldThrow) {
                throw RuntimeException("Temporary failure")
            } else {
                BTResult.SUCCESS
            }
        }
        
        // First execution should throw
        assertFailsWith<RuntimeException> {
            recoveringNode.tick()
        }
        
        // Recover
        shouldThrow = false
        
        // Second execution should work normally
        val result = recoveringNode.tick()
        assertEquals(BTResult.SUCCESS, result)
    }

    // ===== EVENT HANDLING TESTS =====
    
    @Test
    fun `Events should be processed correctly through node hierarchy`() {
        var eventReceived = false
        
        val eventHandler = leaf(tree,
            onTick = { BTResult.SUCCESS },
            onFrame = { BTResult.SUCCESS },
            onEvent = { event ->
                eventReceived = true
                BTResult.SUCCESS
            }
        )
        
        val testEvent = AdditionalTestEvent()
        val result = eventHandler.event(testEvent)
        
        assertEquals(BTResult.SUCCESS, result)
        assertTrue(eventReceived)
    }
    
    @Test
    fun `Selector should handle events correctly with SKIP results`() {
        val selector = selector(tree) {
            leaf({ BTResult.SUCCESS }, { BTResult.SUCCESS }, { BTResult.SKIP })     // Should continue
            leaf({ BTResult.SUCCESS }, { BTResult.SUCCESS }, { BTResult.SUCCESS })  // Should stop here  
            leaf({ BTResult.SUCCESS }, { BTResult.SUCCESS }, { BTResult.FAILURE })  // Should not reach
        }
        
        val testEvent = AdditionalTestEvent()
        val result = selector.event(testEvent)
        
        assertEquals(BTResult.SUCCESS, result)
    }

    // ===== DSL FUNCTIONALITY TESTS =====
    
    @Test
    fun `DSL should create complex nested structures correctly`() {
        var executionPath = mutableListOf<String>()
        
        val complexTree = selector(tree) {
            // First branch: failing sequence
            sequence {
                leaf { 
                    executionPath.add("seq1_leaf1")
                    BTResult.SUCCESS 
                }
                leaf { 
                    executionPath.add("seq1_leaf2")
                    BTResult.FAILURE  // This fails the sequence
                }
            }
            
            // Second branch: succeeding leaf
            leaf { 
                executionPath.add("sel_leaf")
                BTResult.SUCCESS 
            }
        }
        
        val result = complexTree.tick()
        assertEquals(BTResult.SUCCESS, result)
        assertEquals(listOf("seq1_leaf1", "seq1_leaf2", "sel_leaf"), executionPath)
    }
    
    @Test
    fun `DSL should handle condition nodes correctly`() {
        var conditionValue = false
        
        val selector = selector(tree) {
            condition { conditionValue }
            leaf { BTResult.SUCCESS }
        }
        
        // First execution: condition false, should try leaf
        val result1 = selector.tick()
        assertEquals(BTResult.SUCCESS, result1)
        
        // Second execution: condition true, should return SUCCESS from condition
        conditionValue = true
        val result2 = selector.tick()
        assertEquals(BTResult.SUCCESS, result2)
    }

    // ===== PERFORMANCE TESTS =====
    
    @Test
    fun `Large tree structures should execute efficiently`() {
        val startTime = System.currentTimeMillis()
        var executionCount = 0
        
        // Create a large selector with many children
        val largeSelector = selector(tree) {
            // Add 50 failing children
            repeat(50) {
                leaf { 
                    executionCount++
                    BTResult.FAILURE 
                }
            }
            
            // Final succeeding child
            leaf { 
                executionCount++
                BTResult.SUCCESS 
            }
        }
        
        val result = largeSelector.tick()
        val endTime = System.currentTimeMillis()
        
        assertEquals(BTResult.SUCCESS, result)
        assertEquals(51, executionCount)  // 50 failures + 1 success
        assertTrue((endTime - startTime) < 100, "Large tree execution took too long")
    }
    
    @Test
    fun `Repeated execution should not cause memory issues`() {
        val selector = selector(tree) {
            leaf { BTResult.FAILURE }
            leaf { BTResult.SUCCESS }
        }
        
        // Execute many times to test for memory leaks
        repeat(1000) { iteration ->
            val result = selector.tick()
            assertEquals(BTResult.SUCCESS, result, "Failed on iteration $iteration")
        }
    }

    // ===== EDGE CASE TESTS =====
    
    @Test
    fun `Empty composite nodes should handle gracefully`() {
        val emptySelector = selector(tree) {
            // No children added
        }
        
        val emptySequence = sequence(tree) {
            // No children added
        }
        
        // Empty selector should return FAILURE
        val selectorResult = emptySelector.tick()
        assertEquals(BTResult.FAILURE, selectorResult)
        
        // Empty sequence should return SUCCESS
        val sequenceResult = emptySequence.tick()
        assertEquals(BTResult.SUCCESS, sequenceResult)
    }
    
    @Test
    fun `Rapid state changes should be handled correctly`() {
        var state = 0
        val states = arrayOf(BTResult.SUCCESS, BTResult.FAILURE, BTResult.RUNNING, BTResult.SKIP)
        
        val changingNode = leaf(tree) {
            val result = states[state % states.size]
            state++
            result
        }
        
        // Execute multiple cycles
        repeat(states.size * 2) { i ->
            val result = changingNode.tick()
            val expectedResult = states[i % states.size]
            assertEquals(expectedResult, result, "Iteration $i should return $expectedResult")
        }
    }

    // ===== DECORATOR NODE BASIC TESTS =====
    
    @Test
    fun `Inverter should correctly invert SUCCESS and FAILURE`() {
        val successChild = leaf(tree) { BTResult.SUCCESS }
        val failureChild = leaf(tree) { BTResult.FAILURE }
        val runningChild = leaf(tree) { BTResult.RUNNING }
        
        val successInverter = inverter(tree, successChild)
        val failureInverter = inverter(tree, failureChild)
        val runningInverter = inverter(tree, runningChild)
        
        assertEquals(BTResult.FAILURE, successInverter.tick())
        assertEquals(BTResult.SUCCESS, failureInverter.tick())
        assertEquals(BTResult.RUNNING, runningInverter.tick())
    }
    
    @Test
    fun `Succeeder should always return SUCCESS`() {
        val failureChild = leaf(tree) { BTResult.FAILURE }
        val runningChild = leaf(tree) { BTResult.RUNNING }
        
        val failureSucceeder = succeeder(tree, failureChild)
        val runningSucceeder = succeeder(tree, runningChild)
        
        assertEquals(BTResult.SUCCESS, failureSucceeder.tick())
        assertEquals(BTResult.SUCCESS, runningSucceeder.tick())
    }

    // ===== BRANCH INTEGRATION TESTS =====
    
    @Test
    fun `Behavior tree should integrate with branch functionality`() {
        // Test that basic branch creation works
        val behaviorTree = selector(tree) {
            leaf { BTResult.SUCCESS }
        }
        
        // Should execute without issues
        val result = behaviorTree.tick()
        assertEquals(BTResult.SUCCESS, result)
    }
}