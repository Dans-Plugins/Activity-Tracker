package dansplugins.activitytracker.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class HelpCommand {

    public boolean execute(CommandSender sender) {
        sender.sendMessage(ChatColor.AQUA + "/at help - View a list of helpful commands.");
        sender.sendMessage(ChatColor.AQUA + "/at info (playerName) - View a player's activity record.");
        return true;
    }

}
