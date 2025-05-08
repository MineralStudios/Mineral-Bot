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

fun leaf(tree: BehaviourTree, action: () -> BTResult): LeafNode =
    object : LeafNode(tree) {
        override fun tick(): BTResult = action()
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
    process: () -> BTResult
): AsyncNode =
    object : AsyncNode(tree, taskId, waitForCompletion) {
        override fun process() = process()
    }

class CompositeBuilder(val tree: BehaviourTree) {
    private val children = mutableListOf<ChildNode>()

    fun leaf(action: () -> BTResult): LeafNode {
        val node = leaf(tree, action)
        children.add(node)
        return node
    }

    fun sequence(init: CompositeBuilder.() -> Unit): SequenceNode {
        val node = sequence(tree, init)
        children.add(node)
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
        val node = selector(tree, init)
        children.add(node)
        return node
    }

    fun async(taskId: Int, waitForCompletion: Boolean = true, process: () -> BTResult): AsyncNode {
        val node = async(tree, taskId, waitForCompletion, process)
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
