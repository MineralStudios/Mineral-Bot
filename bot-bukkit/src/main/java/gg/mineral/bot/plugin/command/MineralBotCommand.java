package gg.mineral.bot.plugin.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class MineralBotCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            // TODO: list commands
            return false;
        }

        // TODO: gui for managing logs, settings, etc.
        // TODO: item for selecting which bot to debug
        return true;
    }

}
