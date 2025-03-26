package gg.mineral.bot.api.behaviour.node.decorator

import gg.mineral.bot.api.behaviour.BTResult
import gg.mineral.bot.api.behaviour.BehaviourTree
import gg.mineral.bot.api.behaviour.node.DecoratorNode

abstract class SucceederNode(tree: BehaviourTree) : DecoratorNode(tree) {
    final override fun tick() = BTResult.SUCCESS.apply { child.callTick() }
}