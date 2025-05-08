package gg.mineral.bot.api.behaviour

import gg.mineral.bot.api.behaviour.node.BTNode
import gg.mineral.bot.api.instance.ClientInstance
import java.util.*
import java.util.concurrent.Future

abstract class BehaviourTree(val clientInstance: ClientInstance) : BTNode() {
    private val asyncTasks = mutableMapOf<Int, Future<*>>()
    val treeStack = Stack<BTResponse>()
    abstract val rootNode: BTNode

    fun async(taskId: Int, callback: () -> BTResult) {
        asyncTasks[taskId] = clientInstance.asyncExecutor.submit { callback() }
    }

    fun asyncResult(taskId: Int): BTResult? {
        val future = asyncTasks[taskId] ?: return null
        if (!future.isDone) return BTResult.RUNNING
        return asyncTasks.remove(taskId)?.get() as BTResult
    }

    override fun tick(): BTResult {
        val (node, result) = treeStack.pop()
        if (result == BTResult.RUNNING) return node.callTick(treeStack)

        if (clientInstance.configuration.debug)
            println("Current Node: ${node.javaClass.typeName} with result $result (stack size: ${treeStack.size}) (tick: ${clientInstance.currentTick})")

        while (!treeStack.empty()) {
            val (currentNode, currentResult) = treeStack.pop()
            val rootNode = treeStack.empty()
            val rootNodeString = if (rootNode) " (root)" else ""
            if (clientInstance.configuration.debug)
                println("\tat ${currentNode.javaClass.typeName} with result $currentResult" + rootNodeString)
        }

        treeStack.clear()
        return rootNode.callTick(treeStack)
    }
}