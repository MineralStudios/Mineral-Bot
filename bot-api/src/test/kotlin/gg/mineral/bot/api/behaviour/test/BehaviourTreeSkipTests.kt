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

// Dummy tree for SKIP behavior testing
class SkipTestBehaviourTree : BehaviourTree(clientInstance = object : ClientInstance {
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
 * Tests to verify SKIP behavior handling in composite nodes
 */
class BehaviourTreeSkipTests {
    private val tree = SkipTestBehaviourTree()

    @Test
    fun `SequenceNode should continue after SKIP and stop on FAILURE`() {
        var child2Called = false
        var child3Called = false
        var child4Called = false

        val sequence = sequence(tree) {
            leaf { BTResult.SUCCESS }
            leaf { 
                child2Called = true
                BTResult.SKIP  // This should be treated like SUCCESS - continue
            }
            leaf { 
                child3Called = true
                BTResult.FAILURE  // This should stop the sequence
            }
            leaf { 
                child4Called = true
                BTResult.SUCCESS
            }
        }

        val result = sequence.tick()
        
        assertEquals(BTResult.FAILURE, result)
        assertTrue(child2Called, "Child 2 should have been called")
        assertTrue(child3Called, "Child 3 should have been called")
        assertFalse(child4Called, "Child 4 should NOT have been called (sequence stopped at FAILURE)")
    }

    @Test
    fun `SelectorNode should continue after SKIP and stop on SUCCESS`() {
        var child2Called = false
        var child3Called = false
        var child4Called = false

        val selector = selector(tree) {
            leaf { BTResult.FAILURE }
            leaf { 
                child2Called = true
                BTResult.SKIP  // This should be treated like FAILURE - continue
            }
            leaf { 
                child3Called = true
                BTResult.SUCCESS  // This should stop the selector
            }
            leaf { 
                child4Called = true
                BTResult.SUCCESS
            }
        }

        val result = selector.tick()
        
        assertEquals(BTResult.SUCCESS, result)
        assertTrue(child2Called, "Child 2 should have been called")
        assertTrue(child3Called, "Child 3 should have been called")
        assertFalse(child4Called, "Child 4 should NOT have been called (selector stopped at SUCCESS)")
    }

    @Test
    fun `SequenceNode with all SKIP should return SUCCESS`() {
        val sequence = sequence(tree) {
            leaf { BTResult.SKIP }
            leaf { BTResult.SKIP }
            leaf { BTResult.SKIP }
        }

        val result = sequence.tick()
        assertEquals(BTResult.SUCCESS, result)
    }

    @Test
    fun `SelectorNode with all SKIP should return FAILURE`() {
        val selector = selector(tree) {
            leaf { BTResult.SKIP }
            leaf { BTResult.SKIP }
            leaf { BTResult.SKIP }
        }

        val result = selector.tick()
        assertEquals(BTResult.FAILURE, result)
    }

    @Test
    fun `SequenceNode should return RUNNING immediately when child returns RUNNING`() {
        var child3Called = false

        val sequence = sequence(tree) {
            leaf { BTResult.SUCCESS }
            leaf { BTResult.RUNNING }  // This should stop the sequence immediately
            leaf { 
                child3Called = true
                BTResult.SUCCESS
            }
        }

        val result = sequence.tick()
        
        assertEquals(BTResult.RUNNING, result)
        assertFalse(child3Called, "Child 3 should NOT have been called (sequence stopped at RUNNING)")
    }

    @Test
    fun `SelectorNode should return RUNNING immediately when child returns RUNNING`() {
        var child3Called = false

        val selector = selector(tree) {
            leaf { BTResult.FAILURE }
            leaf { BTResult.RUNNING }  // This should stop the selector immediately
            leaf { 
                child3Called = true
                BTResult.SUCCESS
            }
        }

        val result = selector.tick()
        
        assertEquals(BTResult.RUNNING, result)
        assertFalse(child3Called, "Child 3 should NOT have been called (selector stopped at RUNNING)")
    }
}