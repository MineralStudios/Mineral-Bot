package gg.mineral.bot.api.behaviour.node.leaf

import gg.mineral.bot.api.behaviour.BTResult
import gg.mineral.bot.api.behaviour.BehaviourTree
import gg.mineral.bot.api.behaviour.node.LeafNode

abstract class AsyncNode(
    override val tree: BehaviourTree,
    private val taskId: Int,
    private val waitForCompletion: Boolean = true
) :
    LeafNode(tree) {
    final override fun tick(): BTResult {
        tree.asyncResult(taskId)
            ?.let {
                if (waitForCompletion && it == BTResult.RUNNING || it == BTResult.SUCCESS) return@tick BTResult.SUCCESS
                return@tick it
            }
            ?: run {
                tree.async(taskId) { process() }
                return@tick if (waitForCompletion) BTResult.RUNNING else BTResult.SUCCESS
            }
    }

    abstract fun process(): BTResult
}