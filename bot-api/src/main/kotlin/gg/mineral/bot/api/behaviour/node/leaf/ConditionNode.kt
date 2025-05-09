package gg.mineral.bot.api.behaviour.node.leaf

import gg.mineral.bot.api.behaviour.BTResult
import gg.mineral.bot.api.behaviour.BehaviourTree
import gg.mineral.bot.api.behaviour.node.LeafNode
import gg.mineral.bot.api.event.Event

class ConditionNode(tree: BehaviourTree, val condition: () -> Boolean) :
    LeafNode(tree) {
    override fun tick(): BTResult {
        return if (condition()) BTResult.SUCCESS else BTResult.FAILURE
    }

    override fun <T : Event> event(event: T): BTResult {
        return if (condition()) BTResult.SUCCESS else BTResult.FAILURE
    }

    override fun frame(): BTResult {
        return if (condition()) BTResult.SUCCESS else BTResult.FAILURE
    }
}