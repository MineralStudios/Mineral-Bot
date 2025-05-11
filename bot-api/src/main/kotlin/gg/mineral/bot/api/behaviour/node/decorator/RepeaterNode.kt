package gg.mineral.bot.api.behaviour.node.decorator

import gg.mineral.bot.api.behaviour.BTResult
import gg.mineral.bot.api.behaviour.BehaviourTree
import gg.mineral.bot.api.behaviour.node.DecoratorNode
import gg.mineral.bot.api.event.Event

abstract class RepeaterNode(tree: BehaviourTree, private val times: Int = Int.MAX_VALUE) :
    DecoratorNode(tree) {
    final override fun tick(): BTResult {
        var result = child.callTick()
        var i = 1
        while (i < times && result == BTResult.SUCCESS) {
            result = child.callTick()
            i++
        }
        return result
    }

    final override fun frame(): BTResult {
        var result = child.callFrame()
        var i = 1
        while (i < times && result == BTResult.SUCCESS) {
            result = child.callFrame()
            i++
        }
        return result
    }

    final override fun <T : Event> event(event: T): BTResult {
        var result = child.callEvent(event)
        var i = 1
        while (i < times && result == BTResult.SUCCESS) {
            result = child.callEvent(event)
            i++
        }
        return result
    }
}