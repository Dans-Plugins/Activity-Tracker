package dansplugins.activitytracker.commands;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import preponderous.ponder.minecraft.bukkit.abs.AbstractPluginCommand;

/**
 * @author Daniel McCoy Stephenson
 */
public class HelpCommand extends AbstractPluginCommand {

    public HelpCommand() {
        super(new ArrayList<>(Arrays.asList("help")), new ArrayList<>(Arrays.asList("at.help")));
    }

    @Override
    public boolean execute(CommandSender sender) {
        sender.sendMessage("");
        sender.sendMessage(ChatColor.GOLD + "┌─ " + ChatColor.YELLOW + "" + ChatColor.BOLD + "Activity Tracker" +
                          ChatColor.RESET + ChatColor.GOLD + " ─ Commands");
        sender.sendMessage(ChatColor.GOLD + "│ " + ChatColor.AQUA + "/at help " + ChatColor.GRAY + "- View a list of helpful commands.");
        sender.sendMessage(ChatColor.GOLD + "│ " + ChatColor.AQUA + "/at info " + ChatColor.GRAY + "- View your activity record.");
        sender.sendMessage(ChatColor.GOLD + "│ " + ChatColor.AQUA + "/at info (playerName) " + ChatColor.GRAY + "- View a player's activity record.");
        sender.sendMessage(ChatColor.GOLD + "│ " + ChatColor.AQUA + "/at list " + ChatColor.GRAY + "- View the 10 most recent sessions (admin only).");
        sender.sendMessage(ChatColor.GOLD + "│ " + ChatColor.AQUA + "/at top " + ChatColor.GRAY + "- View the most active players.");
        sender.sendMessage(ChatColor.GOLD + "│ " + ChatColor.AQUA + "/at stats " + ChatColor.GRAY + "- View activity stats for the server.");
        sender.sendMessage(ChatColor.GOLD + "│ " + ChatColor.AQUA + "/at average [player] [days] " + ChatColor.GRAY + "- Avg daily activity (default: 7 days).");
        sender.sendMessage(ChatColor.GOLD + "│ " + ChatColor.AQUA + "/at config " + ChatColor.GRAY + "- Show or set config options.");
        sender.sendMessage(ChatColor.GOLD + "└─────────────────────────");
        return true;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        return execute(sender);
    }
}