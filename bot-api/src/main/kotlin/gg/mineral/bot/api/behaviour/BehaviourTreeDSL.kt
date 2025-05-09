package gg.mineral.bot.api.behaviour

import gg.mineral.bot.api.behaviour.node.ChildNode
import gg.mineral.bot.api.behaviour.node.LeafNode
import gg.mineral.bot.api.behaviour.node.composite.SelectorNode
import gg.mineral.bot.api.behaviour.node.composite.SequenceNode
import gg.mineral.bot.api.behaviour.node.decorator.InverterNode
import gg.mineral.bot.api.behaviour.node.decorator.RepeatUntilNode
import gg.mineral.bot.api.behaviour.node.decorator.RepeaterNode
import gg.mineral.bot.api.behaviour.node.decorator.SucceederNode
import gg.mineral.bot.api.behaviour.node.leaf.AsyncNode
import gg.mineral.bot.api.behaviour.node.leaf.ConditionNode
import gg.mineral.bot.api.event.Event

fun sequence(tree: BehaviourTree, init: CompositeBuilder.() -> Unit): SequenceNode {
    val builder = CompositeBuilder(tree)
    builder.init()
    return builder.buildSequence()
}

fun selector(tree: BehaviourTree, init: CompositeBuilder.() -> Unit): SelectorNode {
    val builder = CompositeBuilder(tree)
    builder.init()
    return builder.buildSelector()
}

fun leaf(
    tree: BehaviourTree,
    onTick: () -> BTResult
) = leaf(tree, onTick, { BTResult.SUCCESS }, { BTResult.SUCCESS })

fun leaf(
    tree: BehaviourTree,
    onTick: () -> BTResult,
    onFrame: () -> BTResult = { BTResult.SUCCESS },
    onEvent: (Event) -> BTResult = { BTResult.SUCCESS }
): LeafNode =
    object : LeafNode(tree) {
        override fun tick(): BTResult = onTick()

        override fun frame(): BTResult = onFrame()

        override fun <T : Event> event(event: T): BTResult = onEvent(event)
    }

fun inverter(tree: BehaviourTree, child: ChildNode): InverterNode =
    object : InverterNode(tree) {
        override val child = child
    }

fun repeater(tree: BehaviourTree, times: Int = Int.MAX_VALUE, child: ChildNode): RepeaterNode =
    object : RepeaterNode(tree, times) {
        override val child = child
    }

fun repeatUntil(tree: BehaviourTree, finishState: BTResult = BTResult.FAILURE, child: ChildNode): RepeatUntilNode =
    object : RepeatUntilNode(tree, finishState) {
        override val child = child
    }

fun succeeder(tree: BehaviourTree, child: ChildNode): SucceederNode =
    object : SucceederNode(tree) {
        override val child = child
    }

fun condition(tree: BehaviourTree, predicate: () -> Boolean): ConditionNode = ConditionNode(tree, predicate)

fun async(
    tree: BehaviourTree,
    taskId: Int,
    waitForCompletion: Boolean = true,
    processTick: () -> BTResult,
    processFrame: () -> BTResult = { BTResult.SUCCESS },
    processEvent: (Event) -> BTResult = { BTResult.SUCCESS }
): AsyncNode =
    object : AsyncNode(tree, taskId, waitForCompletion) {
        override fun processTick() = processTick()

        override fun processFrame() = processFrame()

        override fun <T : Event> processEvent(event: T) = processEvent(event)
    }

class CompositeBuilder(val tree: BehaviourTree) {
    private val children = mutableListOf<ChildNode>()

    fun leaf(
        onTick: () -> BTResult
    ): LeafNode =
        leaf(tree, onTick)

    fun leaf(
        onTick: () -> BTResult,
        onFrame: () -> BTResult = { BTResult.SUCCESS },
        onEvent: (Event) -> BTResult = { BTResult.SUCCESS }
    ): LeafNode {
        val node = leaf(tree, onTick, onFrame, onEvent)
        children.add(node)
        return node
    }

    fun sequence(init: CompositeBuilder.() -> Unit): SequenceNode {
        val childBuilder = CompositeBuilder(tree)
        childBuilder.init()
        val node = object : SequenceNode(tree) {
            override val children = childBuilder.children.toTypedArray()
        }
        this.children.add(node)
        return node
    }

    fun condition(predicate: () -> Boolean): ConditionNode {
        val node = condition(tree, predicate)
        children.add(node)
        return node
    }

    fun succeeder(child: ChildNode): SucceederNode {
        val node = succeeder(tree, child)
        children.add(node)
        return node
    }

    fun selector(init: CompositeBuilder.() -> Unit): SelectorNode {
        val childBuilder = CompositeBuilder(tree)
        childBuilder.init()
        val node = object : SelectorNode(tree) {
            override val children = childBuilder.children.toTypedArray()
        }
        this.children.add(node)
        return node
    }

    fun async(
        taskId: Int, waitForCompletion: Boolean = true, onTick: () -> BTResult
    ) = async(tree, taskId, waitForCompletion, onTick)

    fun async(
        taskId: Int, waitForCompletion: Boolean = true, onTick: () -> BTResult,
        onFrame: () -> BTResult = { BTResult.SUCCESS },
        onEvent: (Event) -> BTResult = { BTResult.SUCCESS }
    ): AsyncNode {
        val node = async(tree, taskId, waitForCompletion, onTick, onFrame, onEvent)
        children.add(node)
        return node
    }

    fun buildSequence(): SequenceNode =
        object : SequenceNode(tree) {
            override val children: Array<ChildNode> = this@CompositeBuilder.children.toTypedArray()
        }

    fun buildSelector(): SelectorNode =
        object : SelectorNode(tree) {
            override val children: Array<ChildNode> = this@CompositeBuilder.children.toTypedArray()
        }
}
