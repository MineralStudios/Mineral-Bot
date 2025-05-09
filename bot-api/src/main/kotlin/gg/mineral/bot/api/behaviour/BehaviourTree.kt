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
        if (tickTreeStack.empty()) return rootNode.callTick(tickTreeStack)
        val (node, result) = tickTreeStack.pop()
        if (result == BTResult.RUNNING) return node.callTick(tickTreeStack)

        if (clientInstance.configuration.debug)
            println("[TICK] Current Node: ${node.javaClass.typeName} with result $result (stack size: ${tickTreeStack.size}) (tick: ${clientInstance.currentTick})")

        while (!tickTreeStack.empty()) {
            val (currentNode, currentResult) = tickTreeStack.pop()
            val rootNode = tickTreeStack.empty()
            val rootNodeString = if (rootNode) " (root)" else ""
            if (clientInstance.configuration.debug)
                println("\tat ${currentNode.javaClass.typeName} with result $currentResult" + rootNodeString)
        }

        tickTreeStack.clear()
        return rootNode.callTick(tickTreeStack)
    }

    override fun frame(): BTResult {
        if (frameTreeStack.empty()) return rootNode.callFrame(frameTreeStack)
        val (node, result) = frameTreeStack.pop()
        if (result == BTResult.RUNNING) return node.callFrame(frameTreeStack)

        /*  if (clientInstance.configuration.debug)
              println("[FRAME] Current Node: ${node.javaClass.typeName} with result $result (stack size: ${frameTreeStack.size}) (tick: ${clientInstance.currentTick})")

          while (!frameTreeStack.empty()) {
              val (currentNode, currentResult) = frameTreeStack.pop()
              val rootNode = frameTreeStack.empty()
              val rootNodeString = if (rootNode) " (root)" else ""
              if (clientInstance.configuration.debug)
                  println("\tat ${currentNode.javaClass.typeName} with result $currentResult" + rootNodeString)
          }*/

        frameTreeStack.clear()
        return rootNode.callFrame(frameTreeStack)
    }

    override fun <T : Event> event(event: T): BTResult {
        if (eventTreeStack.empty()) return rootNode.callEvent(eventTreeStack, event)
        val (node, result) = eventTreeStack.pop()
        if (result == BTResult.RUNNING) return node.callEvent(eventTreeStack, event)

        if (clientInstance.configuration.debug)
            println("[EVENT] Current Node: ${node.javaClass.typeName} with result $result (stack size: ${eventTreeStack.size}) (tick: ${clientInstance.currentTick}) (event: $event)")

        while (!eventTreeStack.empty()) {
            val (currentNode, currentResult) = eventTreeStack.pop()
            val rootNode = eventTreeStack.empty()
            val rootNodeString = if (rootNode) " (root)" else ""
            if (clientInstance.configuration.debug)
                println("\tat ${currentNode.javaClass.typeName} with result $currentResult" + rootNodeString)
        }

        eventTreeStack.clear()
        return rootNode.callEvent(eventTreeStack, event)
    }
}