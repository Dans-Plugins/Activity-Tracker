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
        sender.sendMessage(ChatColor.AQUA + "/at help - View a list of helpful commands.");
        sender.sendMessage(ChatColor.AQUA + "/at info - View your activity record.");
        sender.sendMessage(ChatColor.AQUA + "/at info (playerName) - View a player's activity record.");
        sender.sendMessage(ChatColor.AQUA + "/at list - View the 10 most recent sessions (admin only).");
        sender.sendMessage(ChatColor.AQUA + "/at top - View a list of the most active players on the server.");
        sender.sendMessage(ChatColor.AQUA + "/at stats - View activity stats for the server.");
        sender.sendMessage(ChatColor.AQUA + "/at config - Show or set config options.");
        return true;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        return execute(sender);
    }
}