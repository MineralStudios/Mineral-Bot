package gg.mineral.bot.engine.plugin.command

import gg.mineral.api.command.Command
import gg.mineral.api.command.CommandExecutor
import gg.mineral.api.entity.living.human.Player
import gg.mineral.api.world.World
import gg.mineral.bot.ai.goal.DrinkPotionGoal
import gg.mineral.bot.ai.goal.EatGappleGoal
import gg.mineral.bot.ai.goal.MeleeCombatGoal
import gg.mineral.bot.ai.goal.ReplaceArmorGoal
import gg.mineral.bot.api.BotAPI
import gg.mineral.bot.api.configuration.BotConfiguration
import gg.mineral.bot.api.entity.living.player.skin.Skins
import gg.mineral.bot.api.math.ServerLocation
import gg.mineral.bot.api.world.ServerWorld

class MineralBotCommand : Command("mineralbot", "") {
    override fun execute(commandExecutor: CommandExecutor, arguments: Array<String?>) {
        val count = arguments[0]?.toIntOrNull() ?: 1

        repeat(count) {
            val instance = BotAPI.INSTANCE.spawn(
                BotConfiguration.builder().username("EasyBot").skin(Skins.MINERAL_GREEN)
                    .horizontalAimSpeed(0.3f)
                    .verticalAimSpeed(0.3f).horizontalAimAccuracy(0.3f).verticalAimAccuracy(0.25f)
                    .horizontalErraticness(0.3f).averageCps(5f).latency(50).sprintResetAccuracy(0.25f)
                    .hitSelectAccuracy(0.0f).build(), object : ServerLocation {
                    override fun getX() = 0.0

                    override fun getY() = 70.0

                    override fun getZ() = 0.0

                    override fun getYaw() = 0.0f

                    override fun getPitch() = 0.0f

                    override fun getWorld(): ServerWorld<World> {
                        return ServerWorld<World> { if (commandExecutor is Player) commandExecutor.world else null }
                    }

                })

            instance.startGoals(
                ReplaceArmorGoal(instance), DrinkPotionGoal(instance),
                EatGappleGoal(instance),
                MeleeCombatGoal(instance)
            )
        }
    }
}