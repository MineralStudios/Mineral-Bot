package gg.mineral.bot.api.behaviour.node.decorator

import gg.mineral.bot.api.behaviour.BTResult
import gg.mineral.bot.api.behaviour.BehaviourTree
import gg.mineral.bot.api.behaviour.node.DecoratorNode

abstract class RepeaterNode(tree: BehaviourTree, private val times: Int = Int.MAX_VALUE) :
    DecoratorNode(tree) {
    final override fun tick(): BTResult {
        var result = child.callTick()
        for (i in 0 until times) result = child.callTick()
        return result
    }
}