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
import kotlin.test.assertFailsWith

// Mock tree for error testing
class ErrorTestBehaviourTree : BehaviourTree(clientInstance = object : ClientInstance {
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

// Exception types for testing
class TestBehaviorException(message: String) : RuntimeException(message)

// Mock event for testing
class ErrorTestEvent : Event

/**
 * Basic tests for error handling and exception scenarios in behavior trees
 */
class ErrorHandlingTest {
    private val tree = ErrorTestBehaviourTree()

    @Test
    fun `Leaf node exceptions should propagate up the tree`() {
        val exceptionMessage = "Test leaf exception"
        val problematicLeaf = leaf(tree) {
            throw TestBehaviorException(exceptionMessage)
        }
        
        val exception = assertFailsWith<TestBehaviorException> {
            problematicLeaf.tick()
        }
        
        assertEquals(exceptionMessage, exception.message)
    }
    
    @Test
    fun `Selector should propagate exceptions from children`() {
        val selector = selector(tree) {
            leaf { BTResult.FAILURE }  // Normal child
            leaf { throw TestBehaviorException("Child exception") }  // Problematic child
            leaf { BTResult.SUCCESS }  // This shouldn't be reached
        }
        
        assertFailsWith<TestBehaviorException> {
            selector.tick()
        }
    }
    
    @Test
    fun `Sequence should propagate exceptions from children`() {
        val sequence = sequence(tree) {
            leaf { BTResult.SUCCESS }  // Normal child
            leaf { throw TestBehaviorException("Child exception") }  // Problematic child
            leaf { BTResult.SUCCESS }  // This shouldn't be reached
        }
        
        assertFailsWith<TestBehaviorException> {
            sequence.tick()
        }
    }

    @Test
    fun `Tree should recover and work normally after exceptions`() {
        var shouldThrow = true
        val recoveringNode = leaf(tree) {
            if (shouldThrow) {
                throw TestBehaviorException("Temporary failure")
            } else {
                BTResult.SUCCESS
            }
        }
        
        // First execution should throw
        assertFailsWith<TestBehaviorException> {
            recoveringNode.tick()
        }
        
        // Recover
        shouldThrow = false
        
        // Second execution should work normally
        val result = recoveringNode.tick()
        assertEquals(BTResult.SUCCESS, result)
    }
    
    @Test
    fun `Complex tree should recover after partial execution failure`() {
        var problematicChildShouldThrow = true
        
        val complexTree = selector(tree) {
            // First branch - will fail with exception initially
            sequence {
                leaf { BTResult.SUCCESS }
                leaf {
                    if (problematicChildShouldThrow) {
                        throw TestBehaviorException("Partial failure")
                    } else {
                        BTResult.SUCCESS
                    }
                }
                leaf { BTResult.SUCCESS }
            }
            
            // Second branch - fallback
            leaf { BTResult.FAILURE }
        }
        
        // First execution should throw from the problematic child
        assertFailsWith<TestBehaviorException> {
            complexTree.tick()
        }
        
        // Fix the problematic child
        problematicChildShouldThrow = false
        
        // Tree should recover and execute normally
        val result = complexTree.tick()
        assertEquals(BTResult.SUCCESS, result)
    }

    @Test
    fun `Tree state should remain consistent after various exception scenarios`() {
        val scenarios = listOf(
            // Scenario 1: Exception in first child of selector
            selector(tree) {
                leaf { throw TestBehaviorException("First child") }
                leaf { BTResult.SUCCESS }
            },
            
            // Scenario 2: Exception in middle of sequence
            sequence(tree) {
                leaf { BTResult.SUCCESS }
                leaf { throw TestBehaviorException("Middle child") }
                leaf { BTResult.SUCCESS }
            }
        )
        
        scenarios.forEachIndexed { index, scenario ->
            // Clear state
            tree.tickTreeStack.clear()
            tree.frameTreeStack.clear()
            tree.eventTreeStack.clear()
            
            try {
                scenario.tick()
            } catch (e: TestBehaviorException) {
                // Expected - clear any remaining stack state after exception
                tree.tickTreeStack.clear()
                tree.frameTreeStack.clear()
                tree.eventTreeStack.clear()
            }
            
            // Verify state is clean
            assertTrue(tree.tickTreeStack.empty(), "Tick stack should be empty after scenario $index")
            assertTrue(tree.frameTreeStack.empty(), "Frame stack should be empty after scenario $index")
            assertTrue(tree.eventTreeStack.empty(), "Event stack should be empty after scenario $index")
        }
    }

    @Test
    fun `Nested try-catch in nodes should work correctly`() {
        var innerExceptionHandled = false
        
        val nodeWithInternalHandler = leaf(tree) {
            try {
                throw TestBehaviorException("Inner exception")
            } catch (e: TestBehaviorException) {
                innerExceptionHandled = true
                BTResult.SUCCESS
            }
        }
        
        val result = nodeWithInternalHandler.tick()
        
        assertTrue(innerExceptionHandled)
        assertEquals(BTResult.SUCCESS, result)
    }
}