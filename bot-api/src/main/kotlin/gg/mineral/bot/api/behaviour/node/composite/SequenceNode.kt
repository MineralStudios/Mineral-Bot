package gg.mineral.bot.api.behaviour.node.composite

import gg.mineral.bot.api.behaviour.BTResult
import gg.mineral.bot.api.behaviour.BehaviourTree
import gg.mineral.bot.api.behaviour.node.CompositeNode

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
}