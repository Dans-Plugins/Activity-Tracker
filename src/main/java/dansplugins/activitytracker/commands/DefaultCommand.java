package dansplugins.activitytracker.commands;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import dansplugins.activitytracker.ActivityTracker;
import preponderous.ponder.minecraft.bukkit.abs.AbstractPluginCommand;

/**
 * @author Daniel McCoy Stephenson
 */
public class DefaultCommand extends AbstractPluginCommand {
    private static final String PERMISSION = "at.default";

    private final String version;

    public DefaultCommand(ActivityTracker activityTracker) {
        this(activityTracker.getVersion());
    }

    /**
     * Takes the plugin version directly, which lets the command be exercised in tests without
     * the final ActivityTracker class.
     */
    DefaultCommand(String version) {
        super(new ArrayList<>(Arrays.asList("default")), new ArrayList<>(Arrays.asList(PERMISSION)));
        this.version = version;
    }

    @Override
    public boolean execute(CommandSender commandSender) {
        // This command is reached directly from ActivityTracker#onCommand rather than through
        // Ponder's CommandService, so the permission declared above is checked here.
        if (!commandSender.hasPermission(PERMISSION)) {
            commandSender.sendMessage(ChatColor.RED + "You don't have permission to do that.");
            return false;
        }

        commandSender.sendMessage("");
        commandSender.sendMessage(ChatColor.GOLD + "┌─ " + ChatColor.YELLOW + "" + ChatColor.BOLD + "Activity Tracker" +
                                 ChatColor.RESET + ChatColor.GOLD + " ─ v" + version);
        commandSender.sendMessage(ChatColor.GOLD + "│ " + ChatColor.GRAY + "Author: " +
                                 ChatColor.WHITE + "Daniel McCoy Stephenson");
        commandSender.sendMessage(ChatColor.GOLD + "│ " + ChatColor.GRAY + "Wiki:   " +
                                 ChatColor.AQUA + "github.com/Dans-Plugins/Activity-Tracker/wiki");
        commandSender.sendMessage(ChatColor.GOLD + "└─────────────────────────");
        return true;
    }

    @Override
    public boolean execute(CommandSender commandSender, String[] strings) {
        return execute(commandSender);
    }
}