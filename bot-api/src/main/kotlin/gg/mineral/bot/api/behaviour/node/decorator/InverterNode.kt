package gg.mineral.bot.api.behaviour.node.decorator

import gg.mineral.bot.api.behaviour.BTResult
import gg.mineral.bot.api.behaviour.BehaviourTree
import gg.mineral.bot.api.behaviour.node.DecoratorNode

abstract class InverterNode(tree: BehaviourTree) : DecoratorNode(tree) {
    final override fun tick() = when (child.callTick()) {
        BTResult.SUCCESS -> BTResult.FAILURE
        BTResult.FAILURE -> BTResult.SUCCESS
        else -> BTResult.RUNNING
    }
}