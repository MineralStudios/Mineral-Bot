package gg.mineral.bot.engine.plugin.command

import gg.mineral.api.command.Command
import gg.mineral.api.command.CommandExecutor
import gg.mineral.api.entity.living.human.Player
import gg.mineral.api.world.World
import gg.mineral.bot.ai.goal.*
import gg.mineral.bot.api.BotAPI
import gg.mineral.bot.api.configuration.BotConfiguration
import gg.mineral.bot.api.entity.living.player.skin.Skins
import gg.mineral.bot.api.math.ServerLocation
import gg.mineral.bot.api.world.ServerWorld

class MineralBotCommand : Command("mineralbot", "mineralbot.admin") {
    override fun execute(commandExecutor: CommandExecutor, arguments: Array<String?>) {
        val count = arguments[0]?.toIntOrNull() ?: 1

        repeat(count) {
            if (commandExecutor !is Player) return
            val instance = BotAPI.INSTANCE.spawn(
                BotConfiguration(
                    username = "EasyBot",
                    skin = Skins.MINERAL_GREEN,
                    horizontalAimSpeed = 0.3f,
                    verticalAimSpeed = 0.3f,
                    horizontalAimAccuracy = 0.3f,
                    verticalAimAccuracy = 0.25f,
                    horizontalErraticness = 0.3f,
                    averageCps = 5f,
                    latency = 50,
                    sprintResetAccuracy = 0.25f,
                    hitSelectAccuracy = 0.0f
                ), object : ServerLocation {
                    override val x: Double = 0.0
                    override val y: Double = 70.0
                    override val z: Double = 0.0
                    override val yaw: Float = 0.0f
                    override val pitch: Float = 0.0f
                    override val world: ServerWorld<*> =
                        object : ServerWorld<World> {
                            override val handle = commandExecutor.world
                        }
                })

            instance.startGoals(
                ReplaceArmorGoal(instance), DrinkPotionGoal(instance),
                EatGappleGoal(instance),
                MeleeCombatGoal(instance), ThrowPearlGoal(instance)
            )
        }
    }
}