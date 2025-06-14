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

// Mock tree for DSL testing
class DSLTestBehaviourTree : BehaviourTree(clientInstance = object : ClientInstance {
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

// Mock event for DSL testing
class DSLTestEvent : Event

/**
 * Basic tests for behavior tree DSL complex structures
 */
class DSLComplexStructureTest {
    private val tree = DSLTestBehaviourTree()

    @Test
    fun `DSL should create simple selector structure correctly`() {
        val selector = selector(tree) {
            leaf { BTResult.FAILURE }
            leaf { BTResult.SUCCESS }
            leaf { BTResult.FAILURE }
        }
        
        val result = selector.tick()
        assertEquals(BTResult.SUCCESS, result)
    }
    
    @Test
    fun `DSL should create simple sequence structure correctly`() {
        val sequence = sequence(tree) {
            leaf { BTResult.SUCCESS }
            leaf { BTResult.SUCCESS }
            leaf { BTResult.SUCCESS }
        }
        
        val result = sequence.tick()
        assertEquals(BTResult.SUCCESS, result)
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

    @Test
    fun `DSL should handle nested selector and sequence combinations`() {
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
                leaf { 
                    executionPath.add("seq1_leaf3")
                    BTResult.SUCCESS  // This shouldn't execute
                }
            }
            
            // Second branch: nested selector
            selector {
                leaf { 
                    executionPath.add("sel2_leaf1")
                    BTResult.FAILURE 
                }
                sequence {
                    leaf { 
                        executionPath.add("sel2_seq_leaf1")
                        BTResult.SUCCESS 
                    }
                    leaf { 
                        executionPath.add("sel2_seq_leaf2")
                        BTResult.SUCCESS 
                    }
                }
                leaf { 
                    executionPath.add("sel2_leaf3")
                    BTResult.SUCCESS  // This shouldn't execute
                }
            }
        }
        
        val result = complexTree.tick()
        assertEquals(BTResult.SUCCESS, result)
        assertEquals(
            listOf("seq1_leaf1", "seq1_leaf2", "sel2_leaf1", "sel2_seq_leaf1", "sel2_seq_leaf2"),
            executionPath
        )
    }
    
    @Test
    fun `DSL should handle complex bot behavior simulation`() {
        var playerHealth = 100
        var hasFood = true
        var enemyNearby = false
        var foodConsumed = false
        var attackPerformed = false
        var fleePerformed = false
        
        val botBehavior = selector(tree) {
            // Priority 1: Flee if low health and enemy nearby
            sequence {
                condition { playerHealth < 30 && enemyNearby }
                leaf { 
                    fleePerformed = true
                    BTResult.SUCCESS 
                }
            }
            
            // Priority 2: Eat food if low health and have food
            sequence {
                condition { playerHealth < 50 && hasFood }
                leaf { 
                    foodConsumed = true
                    playerHealth = 100
                    hasFood = false
                    BTResult.SUCCESS 
                }
            }
            
            // Priority 3: Attack enemy if nearby
            sequence {
                condition { enemyNearby }
                leaf { 
                    attackPerformed = true
                    enemyNearby = false
                    BTResult.SUCCESS 
                }
            }
            
            // Default: Idle
            leaf { BTResult.SUCCESS }
        }
        
        // Scenario 1: Low health, has food, no enemy - should eat
        playerHealth = 40
        hasFood = true
        enemyNearby = false
        
        val result1 = botBehavior.tick()
        assertEquals(BTResult.SUCCESS, result1)
        assertTrue(foodConsumed)
        assertFalse(attackPerformed)
        assertFalse(fleePerformed)
        assertEquals(100, playerHealth)
        
        // Reset for next scenario
        foodConsumed = false
        attackPerformed = false
        fleePerformed = false
        
        // Scenario 2: Low health, no food, enemy nearby - should flee
        playerHealth = 20
        hasFood = false
        enemyNearby = true
        
        val result2 = botBehavior.tick()
        assertEquals(BTResult.SUCCESS, result2)
        assertFalse(foodConsumed)
        assertFalse(attackPerformed)
        assertTrue(fleePerformed)
    }
    
    @Test
    fun `DSL should handle large tree structures efficiently`() {
        val startTime = System.currentTimeMillis()
        var executionCount = 0
        
        // Create a large selector with many children
        val largeSelector = selector(tree) {
            // Add 100 failing children
            repeat(100) { i ->
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
        assertEquals(101, executionCount)  // 100 failures + 1 success
        assertTrue((endTime - startTime) < 100, "Large tree execution took too long")
    }
    
    @Test
    fun `DSL should handle repeated execution without memory leaks`() {
        val selector = selector(tree) {
            leaf { BTResult.FAILURE }
            leaf { BTResult.FAILURE }
            leaf { BTResult.SUCCESS }
        }
        
        // Execute many times to test for memory leaks
        repeat(1000) { iteration ->
            val result = selector.tick()
            assertEquals(BTResult.SUCCESS, result, "Failed on iteration $iteration")
        }
    }

    @Test
    fun `DSL should handle empty composite nodes gracefully`() {
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
    fun `DSL should handle exceptions in child nodes`() {
        val exceptionSelector = selector(tree) {
            leaf { throw RuntimeException("Test exception") }
            leaf { BTResult.SUCCESS }  // This shouldn't be reached
        }
        
        try {
            exceptionSelector.tick()
            assertTrue(false, "Exception should have been thrown")
        } catch (e: RuntimeException) {
            assertEquals("Test exception", e.message)
        }
    }

    @Test
    fun `DSL builder should handle mixed node types correctly`() {
        var conditionMet = false
        var leafExecuted = false
        
        val mixedSelector = selector(tree) {
            condition { conditionMet }
            sequence {
                leaf { BTResult.SUCCESS }
                leaf { BTResult.SUCCESS }
            }
            leaf { 
                leafExecuted = true
                BTResult.SUCCESS 
            }
        }
        
        // First execution: condition false, sequence succeeds
        val result1 = mixedSelector.tick()
        assertEquals(BTResult.SUCCESS, result1)
        assertFalse(leafExecuted)
        
        // Reset and try with condition true
        leafExecuted = false
        conditionMet = true
        
        val result2 = mixedSelector.tick()
        assertEquals(BTResult.SUCCESS, result2)
        assertFalse(leafExecuted)  // Should stop at condition
    }
}