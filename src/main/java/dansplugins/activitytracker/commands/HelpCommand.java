package dansplugins.activitytracker.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class HelpCommand implements ICommand {

    @Override
    public boolean execute(CommandSender sender) {
        sender.sendMessage(ChatColor.AQUA + "/at help - View a list of helpful commands.");
        sender.sendMessage(ChatColor.AQUA + "/at info (playerName) - View a player's activity record.");
        sender.sendMessage(ChatColor.AQUA + "/at top - View a list of the most active players on the server.");
        return true;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        // unused
        return false;
    }

}
