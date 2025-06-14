package gg.mineral.bot.api.behaviour

import gg.mineral.bot.api.behaviour.node.BTNode
import gg.mineral.bot.api.event.Event
import gg.mineral.bot.api.instance.ClientInstance
import java.util.*
import java.util.concurrent.Future

abstract class BehaviourTree(val clientInstance: ClientInstance) : BTNode() {
    private val asyncTickTasks = mutableMapOf<Int, Future<*>>()
    private val asyncFrameTasks = mutableMapOf<Int, Future<*>>()
    private val asyncEventTasks = mutableMapOf<Int, Future<*>>()
    val tickTreeStack = Stack<BTResponse>()
    val frameTreeStack = Stack<BTResponse>()
    val eventTreeStack = Stack<BTResponse>()
    abstract val rootNode: BTNode

    fun asyncTick(taskId: Int, callback: () -> BTResult) {
        asyncTickTasks[taskId] = clientInstance.asyncExecutor.submit { callback() }
    }

    fun asyncFrame(taskId: Int, callback: () -> BTResult) {
        asyncFrameTasks[taskId] = clientInstance.asyncExecutor.submit { callback() }
    }

    fun asyncEvent(taskId: Int, callback: () -> BTResult) {
        asyncEventTasks[taskId] = clientInstance.asyncExecutor.submit { callback() }
    }

    fun asyncTickResult(taskId: Int): BTResult? {
        val future = asyncTickTasks[taskId] ?: return null
        if (!future.isDone) return BTResult.RUNNING
        return asyncTickTasks.remove(taskId)?.get() as BTResult?
    }

    fun asyncFrameResult(taskId: Int): BTResult? {
        val future = asyncFrameTasks[taskId] ?: return null
        if (!future.isDone) return BTResult.RUNNING
        return asyncFrameTasks.remove(taskId)?.get() as BTResult
    }

    fun asyncEventResult(taskId: Int): BTResult? {
        val future = asyncEventTasks[taskId] ?: return null
        if (!future.isDone) return BTResult.RUNNING
        return asyncEventTasks.remove(taskId)?.get() as BTResult
    }

    override fun tick(): BTResult {
        // If stack is empty, start from root
        if (tickTreeStack.empty()) {
            return rootNode.callTick(tickTreeStack)
        }
        
        // Resume from the RUNNING node that was on the stack
        val (node, result) = tickTreeStack.pop()
        
        // If the previous result was RUNNING, try ticking the node again
        if (result == BTResult.RUNNING) {
            return node.callTick(tickTreeStack)
        }
        
        // If the node finished (SUCCESS/FAILURE/SKIP), continue from root
        tickTreeStack.clear()
        return rootNode.callTick(tickTreeStack)
    }

    override fun frame(): BTResult {
        // If stack is empty, start from root
        if (frameTreeStack.empty()) {
            return rootNode.callFrame(frameTreeStack)
        }
        
        // Resume from the RUNNING node that was on the stack
        val (node, result) = frameTreeStack.pop()
        
        // If the previous result was RUNNING, try calling frame on the node again
        if (result == BTResult.RUNNING) {
            return node.callFrame(frameTreeStack)
        }
        
        // If the node finished (SUCCESS/FAILURE/SKIP), continue from root
        frameTreeStack.clear()
        return rootNode.callFrame(frameTreeStack)
    }

    override fun <T : Event> event(event: T): BTResult {
        // If stack is empty, start from root
        if (eventTreeStack.empty()) {
            return rootNode.callEvent(eventTreeStack, event)
        }
        
        // Resume from the RUNNING node that was on the stack
        val (node, result) = eventTreeStack.pop()
        
        // If the previous result was RUNNING, try calling event on the node again
        if (result == BTResult.RUNNING) {
            return node.callEvent(eventTreeStack, event)
        }
        
        // If the node finished (SUCCESS/FAILURE/SKIP), continue from root
        eventTreeStack.clear()
        return rootNode.callEvent(eventTreeStack, event)
    }
}