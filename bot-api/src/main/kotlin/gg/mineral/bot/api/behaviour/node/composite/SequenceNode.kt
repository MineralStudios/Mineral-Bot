package gg.mineral.bot.api.behaviour.node.composite

import gg.mineral.bot.api.behaviour.BTResult
import gg.mineral.bot.api.behaviour.BehaviourTree
import gg.mineral.bot.api.behaviour.node.CompositeNode
import gg.mineral.bot.api.event.Event

abstract class SequenceNode(tree: BehaviourTree) : CompositeNode(tree) {
    final override fun tick(): BTResult {
        for (child in children) {
            val result = child.callTick()
            if (result != BTResult.SUCCESS) {
                return result
            }
        }
        return BTResult.SUCCESS
    }

    final override fun frame(): BTResult {
        for (child in children) {
            val result = child.callFrame()
            if (result != BTResult.SUCCESS) {
                return result
            }
        }
        return BTResult.SUCCESS
    }

    final override fun <T : Event> event(event: T): BTResult {
        for (child in children) {
            val result = child.callEvent(event)
            if (result != BTResult.SUCCESS) {
                return result
            }
        }
        return BTResult.SUCCESS
    }
}