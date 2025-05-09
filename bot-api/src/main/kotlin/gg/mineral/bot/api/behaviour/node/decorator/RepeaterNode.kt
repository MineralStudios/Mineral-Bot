package gg.mineral.bot.api.behaviour.node.decorator

import gg.mineral.bot.api.behaviour.BTResult
import gg.mineral.bot.api.behaviour.BehaviourTree
import gg.mineral.bot.api.behaviour.node.DecoratorNode
import gg.mineral.bot.api.event.Event

abstract class RepeaterNode(tree: BehaviourTree, private val times: Int = Int.MAX_VALUE) :
    DecoratorNode(tree) {
    final override fun tick(): BTResult {
        var result = child.callTick()
        for (i in 0 until times) result = child.callTick()
        return result
    }

    final override fun frame(): BTResult {
        var result = child.callFrame()
        for (i in 0 until times) result = child.callFrame()
        return result
    }

    final override fun <T : Event> event(event: T): BTResult {
        var result = child.callEvent(event)
        for (i in 0 until times) result = child.callEvent(event)
        return result
    }
}