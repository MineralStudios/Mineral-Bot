package net.minecraft.command.server;

import com.mojang.authlib.GameProfile;
import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;

public class CommandWhitelist extends CommandBase {

    public String getCommandName() {
        return "whitelist";
    }

    /**
     * Return the required permission level for this command.
     */
    public int getRequiredPermissionLevel() {
        return 3;
    }

    public String getCommandUsage(ICommandSender p_71518_1_) {
        return "commands.whitelist.usage";
    }

    public void processCommand(ICommandSender p_71515_1_, String[] p_71515_2_) {
        if (p_71515_2_.length >= 1) {
            MinecraftServer var3 = MinecraftServer.getServer();

            if (p_71515_2_[0].equals("on")) {
                var3.getConfigurationManager().setWhiteListEnabled(true);
                func_152373_a(p_71515_1_, this, "commands.whitelist.enabled", new Object[0]);
                return;
            }

            if (p_71515_2_[0].equals("off")) {
                var3.getConfigurationManager().setWhiteListEnabled(false);
                func_152373_a(p_71515_1_, this, "commands.whitelist.disabled", new Object[0]);
                return;
            }

            if (p_71515_2_[0].equals("list")) {
                p_71515_1_.addChatMessage(new ChatComponentTranslation("commands.whitelist.list",
                        new Object[] { Integer.valueOf(var3.getConfigurationManager().func_152598_l().length),
                                Integer.valueOf(var3.getConfigurationManager().getAvailablePlayerDat().length) }));
                String[] var5 = var3.getConfigurationManager().func_152598_l();
                p_71515_1_.addChatMessage(new ChatComponentText(joinNiceString(var5)));
                return;
            }

            GameProfile var4;

            if (p_71515_2_[0].equals("add")) {
                if (p_71515_2_.length < 2) {
                    throw new WrongUsageException("commands.whitelist.add.usage", new Object[0]);
                }

                var4 = var3.func_152358_ax().func_152655_a(p_71515_2_[1]);

                if (var4 == null) {
                    throw new CommandException("commands.whitelist.add.failed", new Object[] { p_71515_2_[1] });
                }

                var3.getConfigurationManager().func_152601_d(var4);
                func_152373_a(p_71515_1_, this, "commands.whitelist.add.success", new Object[] { p_71515_2_[1] });
                return;
            }

            if (p_71515_2_[0].equals("remove")) {
                if (p_71515_2_.length < 2) {
                    throw new WrongUsageException("commands.whitelist.remove.usage", new Object[0]);
                }

                var4 = var3.getConfigurationManager().func_152599_k().func_152706_a(p_71515_2_[1]);

                if (var4 == null) {
                    throw new CommandException("commands.whitelist.remove.failed", new Object[] { p_71515_2_[1] });
                }

                var3.getConfigurationManager().func_152597_c(var4);
                func_152373_a(p_71515_1_, this, "commands.whitelist.remove.success", new Object[] { p_71515_2_[1] });
                return;
            }

            if (p_71515_2_[0].equals("reload")) {
                var3.getConfigurationManager().loadWhiteList();
                func_152373_a(p_71515_1_, this, "commands.whitelist.reloaded", new Object[0]);
                return;
            }
        }

        throw new WrongUsageException("commands.whitelist.usage", new Object[0]);
    }

    /**
     * Adds the strings available in this command to the given list of tab
     * completion options.
     */
    public List addTabCompletionOptions(ICommandSender p_71516_1_, String[] p_71516_2_) {
        if (p_71516_2_.length == 1) {
            return getListOfStringsMatchingLastWord(p_71516_2_,
                    new String[] { "on", "off", "list", "add", "remove", "reload" });
        } else {
            if (p_71516_2_.length == 2) {
                if (p_71516_2_[0].equals("remove")) {
                    return getListOfStringsMatchingLastWord(p_71516_2_,
                            MinecraftServer.getServer().getConfigurationManager().func_152598_l());
                }

                if (p_71516_2_[0].equals("add")) {
                    return getListOfStringsMatchingLastWord(p_71516_2_,
                            MinecraftServer.getServer().func_152358_ax().func_152654_a());
                }
            }

            return null;
        }
    }
}
