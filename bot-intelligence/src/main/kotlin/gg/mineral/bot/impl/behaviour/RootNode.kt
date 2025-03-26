package gg.mineral.bot.impl.behaviour

import gg.mineral.bot.api.behaviour.BehaviourTree
import gg.mineral.bot.api.behaviour.node.ChildNode
import gg.mineral.bot.api.behaviour.node.composite.SelectorNode
import gg.mineral.bot.api.inv.item.Item
import gg.mineral.bot.impl.behaviour.branch.*

class RootNode(tree: BehaviourTree) : SelectorNode(tree) {
    override val children: Array<ChildNode> = arrayOf(
        /*ReplaceArmorGoal(it),
        HealSoupGoal(it),
        ThrowHealthPotGoal(it),
        DrinkPotionGoal(it),
        EatGappleGoal(it),
        EatFoodGoal(it),
        ThrowDebuffPotGoal(it),
        ThrowPearlGoal(it),
        DropEmptyBowlGoal(it),
        MeleeCombatGoal(it)*/

        // TODO: Safe walk (don't walk into danger)

        // Replace armor
        ReplaceArmorBranch(tree, Item.Type.HELMET) { helmet },
        ReplaceArmorBranch(tree, Item.Type.BOOTS) { boots },
        ReplaceArmorBranch(tree, Item.Type.LEGGINGS) { leggings },
        ReplaceArmorBranch(tree, Item.Type.CHESTPLATE) { chestplate },

        // Heal soup
        HealSoupBranch(tree),

        // Throw health pot
        ThrowHealthPotBranch(tree),

        // Drink potion
        DrinkPotionBranch(tree),

        // Eat gapple
        EatGappleBranch(tree),

        // TODO: Eat food
        EatFoodBranch(tree),

        // TODO: Throw debuff pot
        ThrowDebuffPotBranch(tree),

        // TODO: Throw pearl
        ThrowPearlBranch(tree),

        // TODO: Drop empty bowl
        DropEmptyBowlBranch(tree),

        // TODO: Melee combat
        MeleeCombatBranch(tree)
    )
}