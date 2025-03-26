package gg.mineral.bot.api.behaviour.node

import gg.mineral.bot.api.behaviour.BehaviourTree

abstract class DecoratorNode(tree: BehaviourTree) : ChildNode(tree) {
    abstract val child: ChildNode
}