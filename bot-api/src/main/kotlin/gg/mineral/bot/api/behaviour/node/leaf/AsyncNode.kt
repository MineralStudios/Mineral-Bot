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
                // If we have a result from the async task
                if (waitForCompletion) {
                    // Wait for completion: return actual result
                    return@tick it
                } else {
                    // Don't wait for completion: return SUCCESS unless task failed
                    return@tick if (it == BTResult.FAILURE) BTResult.FAILURE else BTResult.SUCCESS
                }
            }
            ?: run {
                // No async task running, start one
                tree.asyncTick(taskId) { processTick() }
                return@tick if (waitForCompletion) BTResult.RUNNING else BTResult.SUCCESS
            }
    }

    final override fun frame(): BTResult {
        tree.asyncFrameResult(taskId)
            ?.let {
                // If we have a result from the async task
                if (waitForCompletion) {
                    // Wait for completion: return actual result
                    return@frame it
                } else {
                    // Don't wait for completion: return SUCCESS unless task failed
                    return@frame if (it == BTResult.FAILURE) BTResult.FAILURE else BTResult.SUCCESS
                }
            }
            ?: run {
                // No async task running, start one
                tree.asyncFrame(taskId) { processFrame() }
                return@frame if (waitForCompletion) BTResult.RUNNING else BTResult.SUCCESS
            }
    }

    final override fun <T : Event> event(event: T): BTResult {
        tree.asyncEventResult(taskId)
            ?.let {
                // If we have a result from the async task
                if (waitForCompletion) {
                    // Wait for completion: return actual result
                    return@event it
                } else {
                    // Don't wait for completion: return SUCCESS unless task failed
                    return@event if (it == BTResult.FAILURE) BTResult.FAILURE else BTResult.SUCCESS
                }
            }
            ?: run {
                // No async task running, start one
                tree.asyncEvent(taskId) { processEvent(event) }
                return@event if (waitForCompletion) BTResult.RUNNING else BTResult.SUCCESS
            }
    }

    abstract fun processTick(): BTResult

    abstract fun processFrame(): BTResult

    abstract fun <T : Event> processEvent(event: T): BTResult
}