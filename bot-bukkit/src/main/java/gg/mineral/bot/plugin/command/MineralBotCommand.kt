package gg.mineral.bot.plugin.command

import org.apache.logging.log4j.Level
import org.apache.logging.log4j.core.config.Configurator
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

class MineralBotCommand : CommandExecutor {
    private var debugEnabled = false

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (!sender.hasPermission("mineralbot.admin")) {
            return false
        }

        if (args.isEmpty()) {
            // TODO: list commands
            return false
        }

        if (args[0].equals("debug", ignoreCase = true)) {
            if (debugEnabled) {
                Configurator.setLevel("gg.mineral.bot", Level.INFO)
                debugEnabled = false
                sender.sendMessage("MineralBot debug mode disabled.")
            } else {
                Configurator.setLevel("gg.mineral.bot", Level.DEBUG)
                debugEnabled = true
                sender.sendMessage("MineralBot debug mode enabled.")
            }
        }

        // TODO: gui for managing logs, settings, etc.
        // TODO: item for selecting which bot to debug
        return true
    }
}
