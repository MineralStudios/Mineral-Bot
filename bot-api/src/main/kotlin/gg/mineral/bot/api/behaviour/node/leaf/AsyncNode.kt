package gg.mineral.bot.api.behaviour.node.leaf

import gg.mineral.bot.api.behaviour.BTResult
import gg.mineral.bot.api.behaviour.BehaviourTree
import gg.mineral.bot.api.behaviour.node.LeafNode
import gg.mineral.bot.api.event.Event

abstract class AsyncNode(
    override val tree: BehaviourTree,
    private val taskId: Int,
    private val waitForCompletion: Boolean = true
) :
    LeafNode(tree) {
    final override fun tick(): BTResult {
        tree.asyncTickResult(taskId)
            ?.let {
                if (waitForCompletion && it == BTResult.RUNNING || it == BTResult.SUCCESS) return@tick BTResult.SUCCESS
                return@tick it
            }
            ?: run {
                tree.asyncTick(taskId) { processTick() }
                return@tick if (waitForCompletion) BTResult.RUNNING else BTResult.SUCCESS
            }
    }

    final override fun frame(): BTResult {
        tree.asyncFrameResult(taskId)
            ?.let {
                if (waitForCompletion && it == BTResult.RUNNING || it == BTResult.SUCCESS) return@frame BTResult.SUCCESS
                return@frame it
            }
            ?: run {
                tree.asyncFrame(taskId) { processFrame() }
                return@frame if (waitForCompletion) BTResult.RUNNING else BTResult.SUCCESS
            }
    }

    final override fun <T : Event> event(event: T): BTResult {
        tree.asyncEventResult(taskId)
            ?.let {
                if (waitForCompletion && it == BTResult.RUNNING || it == BTResult.SUCCESS) return@event BTResult.SUCCESS
                return@event it
            }
            ?: run {
                tree.asyncEvent(taskId) { processEvent(event) }
                return@event if (waitForCompletion) BTResult.RUNNING else BTResult.SUCCESS
            }
    }

    abstract fun processTick(): BTResult

    abstract fun processFrame(): BTResult

    abstract fun <T : Event> processEvent(event: T): BTResult
}