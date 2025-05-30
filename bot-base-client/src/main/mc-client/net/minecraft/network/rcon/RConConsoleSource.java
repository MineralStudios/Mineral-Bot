package net.minecraft.network.rcon;

import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.World;

public class RConConsoleSource implements ICommandSender {
    public static final RConConsoleSource field_70010_a = new RConConsoleSource();
    private StringBuffer field_70009_b = new StringBuffer();

    /**
     * Gets the name of this command sender (usually username, but possibly "Rcon")
     */
    public String getCommandSenderName() {
        return "Rcon";
    }

    public IChatComponent func_145748_c_() {
        return new ChatComponentText(this.getCommandSenderName());
    }

    /**
     * Notifies this sender of some sort of information. This is for messages
     * intended to display to the user. Used
     * for typical output (like "you asked for whether or not this game rule is set,
     * so here's your answer"), warnings
     * (like "I fetched this block for you by ID, but I'd like you to know that
     * every time you do this, I die a little
     * inside"), and errors (like "it's not called iron_pixacke, silly").
     */
    public void addChatMessage(IChatComponent p_145747_1_) {
        this.field_70009_b.append(p_145747_1_.getUnformattedText());
    }

    /**
     * Returns true if the command sender is allowed to use the given command.
     */
    public boolean canCommandSenderUseCommand(int p_70003_1_, String p_70003_2_) {
        return true;
    }

    /**
     * Return the position for this command sender.
     */
    public ChunkCoordinates getPlayerCoordinates() {
        return new ChunkCoordinates(0, 0, 0);
    }

    public World getEntityWorld() {
        return MinecraftServer.getServer().getEntityWorld();
    }
}
