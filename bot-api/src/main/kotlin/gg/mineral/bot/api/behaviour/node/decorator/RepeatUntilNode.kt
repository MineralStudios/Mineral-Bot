package gg.mineral.bot.api.behaviour.node.decorator

import gg.mineral.bot.api.behaviour.BTResult
import gg.mineral.bot.api.behaviour.BehaviourTree
import gg.mineral.bot.api.behaviour.node.DecoratorNode

abstract class RepeatUntilNode(
    tree: BehaviourTree,
    private val finishState: BTResult = BTResult.FAILURE
) :
    DecoratorNode(tree) {
    // TODO:  tick until
    final override fun tick(): BTResult {
        var result = child.callTick()
        while (result != finishState) result = child.callTick()
        return result
    }
}