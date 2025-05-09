package gg.mineral.api.behaviour.test

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
import java.util.concurrent.ExecutorService
import java.util.concurrent.ScheduledExecutorService
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

// A minimal stub for BehaviourTree to use in unit tests
class DummyBehaviourTree : BehaviourTree(clientInstance = object : ClientInstance {
    override var behaviourTree: BehaviourTree?
        get() = TODO("Not yet implemented")
        set(value) {}
    override val latency: Int
        get() = TODO("Not yet implemented")
    override val currentTick: Int
        get() = TODO("Not yet implemented")
    override val currentScreen: Screen?
        get() = TODO("Not yet implemented")
    override val configuration: BotConfiguration
        get() = TODO("Not yet implemented")
    override val keyboard: Keyboard
        get() = TODO("Not yet implemented")
    override val mouse: Mouse
        get() = TODO("Not yet implemented")
    override val fakePlayer: FakePlayer
        get() = TODO("Not yet implemented")
    override val isRunning: Boolean
        get() = TODO("Not yet implemented")

    override fun timeMillis(): Long {
        TODO("Not yet implemented")
    }

    override val gameLoopExecutor: ScheduledExecutorService
        get() = TODO("Not yet implemented")
    override val asyncExecutor: ExecutorService
        get() = TODO("Not yet implemented")

    override fun schedule(runnable: Runnable, delay: Long): Boolean {
        TODO("Not yet implemented")
    }

    override val session: Session
        get() = TODO("Not yet implemented")

    override fun shutdown() {
        TODO("Not yet implemented")
    }

    override fun newMouse(): Mouse {
        TODO("Not yet implemented")
    }

    override fun newKeyboard(): Keyboard {
        TODO("Not yet implemented")
    }

    override val displayHeight: Int
        get() = TODO("Not yet implemented")
    override val displayWidth: Int
        get() = TODO("Not yet implemented")

    // No-op: stub implementation
    override fun <T : Event> callEvent(event: T): Boolean {
        TODO("Not yet implemented")
    }
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

    @Test
    fun `repeatUntil decorator stops on finishState`() {
        var count = 0
        val leafNode = leaf(tree) {
            count++
            if (count < 4) BTResult.SUCCESS else BTResult.FAILURE
        }
        val repeatUntilNode = repeatUntil(tree, finishState = BTResult.FAILURE, child = leafNode)

        val result = repeatUntilNode.tick()
        // should stop when leaf returns FAILURE on count==4
        assertEquals(BTResult.FAILURE, result)
        assertEquals(4, count)
    }
}
