package gg.mineral.bot.api.behaviour.test

import gg.mineral.bot.api.behaviour.*
import gg.mineral.bot.api.behaviour.node.BTNode
import gg.mineral.bot.api.behaviour.node.ChildNode
import gg.mineral.bot.api.behaviour.node.LeafNode
import gg.mineral.bot.api.behaviour.node.composite.SelectorNode
import gg.mineral.bot.api.behaviour.node.composite.SequenceNode
import gg.mineral.bot.api.behaviour.node.leaf.ConditionNode
import gg.mineral.bot.api.configuration.BotConfiguration
import gg.mineral.bot.api.controls.Keyboard
import gg.mineral.bot.api.controls.Mouse
import gg.mineral.bot.api.entity.living.player.FakePlayer
import gg.mineral.bot.api.event.Event
import gg.mineral.bot.api.instance.ClientInstance
import gg.mineral.bot.api.instance.Session
import gg.mineral.bot.api.screen.Screen
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.ScheduledExecutorService
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertFalse

// A minimal stub for BehaviourTree to use in unit tests
open class DummyBehaviourTree : BehaviourTree(clientInstance = object : ClientInstance {
    override var behaviourTree: BehaviourTree? = null
    override val latency: Int = 0
    override val currentTick: Int = 0
    override val currentScreen: Screen? = null
    override val configuration: BotConfiguration
        get() = TODO("Not yet implemented")
    override val keyboard: Keyboard
        get() = TODO("Not yet implemented")
    override val mouse: Mouse
        get() = TODO("Not yet implemented")
    override val fakePlayer: FakePlayer
        get() = TODO("Not yet implemented")
    override val isRunning: Boolean = true

    override fun timeMillis(): Long = System.currentTimeMillis()

    override val gameLoopExecutor: ScheduledExecutorService
        get() = TODO("Not yet implemented")
    override val asyncExecutor: ExecutorService
        get() = TODO("Not yet implemented")

    override fun schedule(runnable: Runnable, delay: Long): Boolean = true

    override val session: Session
        get() = TODO("Not yet implemented")

    override fun shutdown() {}

    override fun newMouse(): Mouse
        = TODO("Not yet implemented")

    override fun newKeyboard(): Keyboard
        = TODO("Not yet implemented")

    override val displayHeight: Int = 1080
    override val displayWidth: Int = 1920

    override fun <T : Event> callEvent(event: T): Boolean = true
}) {
    override val rootNode: BTNode
        get() = object : SequenceNode(this) {
            override val children = arrayOf<ChildNode>()
        }
}

class BehaviourTreeTest {
    private val tree = DummyBehaviourTree()

    @Test
    fun `sequence builder creates a SequenceNode with correct children`() {
        val sequenceNode = sequence(tree) {
            leaf { BTResult.SUCCESS }
            condition { true }
        }

        val children = sequenceNode.children
        assertEquals(2, children.size)
        assertTrue(children[0] is LeafNode)
        assertTrue(children[1] is ConditionNode)
    }

    @Test
    fun `selector builder creates a SelectorNode with nested Sequence and Selector`() {
        val selectorNode = selector(tree) {
            // first child: a leaf
            leaf { BTResult.SUCCESS }
            // second child: nested selector
            selector {
                condition { false }
                leaf { BTResult.SUCCESS }
            }
        }

        val topLevelChildren = selectorNode.children
        assertEquals(2, topLevelChildren.size)
        assertTrue(topLevelChildren[0] is LeafNode)
        assertTrue(topLevelChildren[1] is SelectorNode)

        val nestedChildren = (topLevelChildren[1] as SelectorNode).children
        assertEquals(2, nestedChildren.size)
        assertTrue(nestedChildren[0] is ConditionNode)
        assertTrue(nestedChildren[1] is LeafNode)
    }

    @Test
    fun `succeeder decorator wraps and always returns SUCCESS`() {
        var called = false
        val innerLeaf = leaf(
            tree
        ) {
            called = true
            BTResult.FAILURE
        }
        val succeederNode = succeeder(tree, innerLeaf)

        val result = succeederNode.tick()
        // even though inner returns FAILURE, succeeder forces SUCCESS
        assertTrue(called)
        assertEquals(BTResult.SUCCESS, result)
    }

    @Test
    fun `inverter decorator inverts SUCCESS and FAILURE`() {
        val innerSuccess = leaf(tree) { BTResult.SUCCESS }
        val inverterNode1 = inverter(tree, innerSuccess)
        assertEquals(BTResult.FAILURE, inverterNode1.tick())

        val innerFailure = leaf(tree) { BTResult.FAILURE }
        val inverterNode2 = inverter(tree, innerFailure)
        assertEquals(BTResult.SUCCESS, inverterNode2.tick())
    }

    @Test
    fun `repeater decorator repeats until limit`() {
        var count = 0
        val leafNode = leaf(tree) {
            count++
            if (count < 3) BTResult.SUCCESS else BTResult.FAILURE
        }
        val repeaterNode = repeater(tree, times = 5, child = leafNode)

        val result = repeaterNode.tick()
        // should stop on third invocation (failure)
        assertEquals(BTResult.FAILURE, result)
        assertEquals(3, count)
    }

    // ========== Additional Working Tests ==========

    @Test
    fun `BTNode callTick updates stack with new response`() {
        val node = leaf(tree) { BTResult.SUCCESS }
        val stack = Stack<BTResponse>()

        val result = node.callTick(stack)

        assertEquals(BTResult.SUCCESS, result)
        assertEquals(1, stack.size)
        assertEquals(node, stack.peek().node)
        assertEquals(BTResult.SUCCESS, stack.peek().result)
    }

    @Test
    fun `BTResponse stores node and result correctly`() {
        val node = leaf(tree) { BTResult.SUCCESS }
        val response = BTResponse(node, BTResult.RUNNING)

        assertEquals(node, response.node)
        assertEquals(BTResult.RUNNING, response.result)

        response.result = BTResult.SUCCESS
        assertEquals(BTResult.SUCCESS, response.result)
    }

    @Test
    fun `SequenceNode returns SUCCESS when all children succeed`() {
        val sequence = sequence(tree) {
            leaf { BTResult.SUCCESS }
            leaf { BTResult.SUCCESS }
            leaf { BTResult.SUCCESS }
        }

        val result = sequence.tick()
        assertEquals(BTResult.SUCCESS, result)
    }

    @Test
    fun `SequenceNode returns FAILURE on first child failure`() {
        var child3Called = false

        val sequence = sequence(tree) {
            leaf { BTResult.SUCCESS }
            leaf { BTResult.FAILURE }
            leaf { 
                child3Called = true
                BTResult.SUCCESS
            }
        }

        val result = sequence.tick()
        assertEquals(BTResult.FAILURE, result)
        assertFalse(child3Called) // Should short-circuit
    }

    @Test
    fun `SelectorNode returns SUCCESS on first child success`() {
        var child3Called = false

        val selector = selector(tree) {
            leaf { BTResult.FAILURE }
            leaf { BTResult.SUCCESS }
            leaf { 
                child3Called = true
                BTResult.SUCCESS
            }
        }

        val result = selector.tick()
        assertEquals(BTResult.SUCCESS, result)
        assertFalse(child3Called) // Should short-circuit
    }

    @Test
    fun `InverterNode handles all BTResult values correctly`() {
        val testCases = mapOf(
            BTResult.SUCCESS to BTResult.FAILURE,
            BTResult.FAILURE to BTResult.SUCCESS,
            BTResult.RUNNING to BTResult.RUNNING,
            BTResult.SKIP to BTResult.SKIP
        )

        testCases.forEach { (input, expected) ->
            val innerNode = leaf(tree) { input }
            val inverterNode = inverter(tree, innerNode)
            assertEquals(expected, inverterNode.tick())
        }
    }

    @Test
    fun `ConditionNode returns SUCCESS for true condition`() {
        val conditionNode = condition(tree) { true }
        assertEquals(BTResult.SUCCESS, conditionNode.tick())
    }

    @Test
    fun `ConditionNode returns FAILURE for false condition`() {
        val conditionNode = condition(tree) { false }
        assertEquals(BTResult.FAILURE, conditionNode.tick())
    }

    @Test
    fun `Simple nested behavior tree integration`() {
        val complexTree = selector(tree) {
            // First branch: sequence that fails
            sequence {
                condition { true }
                condition { false } // This fails the sequence
            }
            // Second branch: working sequence
            sequence {
                condition { true }
                condition { true }
            }
        }

        val result = complexTree.tick()
        assertEquals(BTResult.SUCCESS, result)
    }
}