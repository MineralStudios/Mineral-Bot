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

// Dummy tree for bug testing
class TestBehaviourTree : BehaviourTree(clientInstance = object : ClientInstance {
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
 * Tests to identify and reproduce bugs in the behavior tree system
 */
class BehaviourTreeBugTests {
    private val tree = TestBehaviourTree()

    @Test
    fun `RepeatUntilNode should handle RUNNING state without infinite loop`() {
        var callCount = 0
        val childNode = leaf(tree) {
            callCount++
            when (callCount) {
                1 -> BTResult.SUCCESS
                2 -> BTResult.RUNNING  // This should cause RepeatUntilNode to return RUNNING
                else -> BTResult.FAILURE
            }
        }
        
        val repeatUntilNode = repeatUntil(tree, finishState = BTResult.FAILURE, child = childNode)
        
        // This should not hang - RUNNING should be returned immediately
        val result = repeatUntilNode.tick()
        
        assertEquals(BTResult.RUNNING, result)
        assertEquals(2, callCount) // Should have been called twice, then stopped at RUNNING
    }

    @Test
    fun `RepeatUntilNode should eventually reach finish state`() {
        var callCount = 0
        val childNode = leaf(tree) {
            callCount++
            if (callCount < 3) BTResult.SUCCESS else BTResult.FAILURE
        }
        
        val repeatUntilNode = repeatUntil(tree, finishState = BTResult.FAILURE, child = childNode)
        
        val result = repeatUntilNode.tick()
        
        assertEquals(BTResult.FAILURE, result)
        assertEquals(3, callCount)
    }

    @Test
    fun `RepeatUntilNode with SKIP should not infinite loop`() {
        var callCount = 0
        val childNode = leaf(tree) {
            callCount++
            when (callCount) {
                1 -> BTResult.SUCCESS
                2 -> BTResult.SKIP  // This should cause RepeatUntilNode to return SKIP
                else -> BTResult.FAILURE
            }
        }
        
        val repeatUntilNode = repeatUntil(tree, finishState = BTResult.FAILURE, child = childNode)
        
        // This should not hang - SKIP should be returned immediately
        val result = repeatUntilNode.tick()
        
        // Current implementation might return SKIP or continue looping - let's see what happens
        // If it hangs, we know there's a bug
        assertEquals(2, callCount) // Should not continue after SKIP
    }
}