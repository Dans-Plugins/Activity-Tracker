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
    private final ActivityTracker activityTracker;

    public DefaultCommand(ActivityTracker activityTracker) {
        super(new ArrayList<>(Arrays.asList("default")), new ArrayList<>(Arrays.asList("at.default")));
        this.activityTracker = activityTracker;
    }

    @Override
    public boolean execute(CommandSender commandSender) {
        commandSender.sendMessage(ChatColor.AQUA + "Activity Tracker " + activityTracker.getVersion());
        commandSender.sendMessage(ChatColor.AQUA + "Developed by: Daniel McCoy Stephenson");
        commandSender.sendMessage(ChatColor.AQUA + "Wiki: https://github.com/Dans-Plugins/Activity-Tracker/wiki");
        return true;
    }

    @Override
    public boolean execute(CommandSender commandSender, String[] strings) {
        return execute(commandSender);
    }
}