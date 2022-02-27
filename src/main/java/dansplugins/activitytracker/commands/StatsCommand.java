package dansplugins.activitytracker.commands;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import dansplugins.activitytracker.data.PersistentData;
import preponderous.ponder.minecraft.bukkit.abs.AbstractPluginCommand;

/**
 * @author Daniel McCoy Stephenson
 */
public class StatsCommand extends AbstractPluginCommand {

    public StatsCommand() {
        super(new ArrayList<>(Arrays.asList("stats")), new ArrayList<>(Arrays.asList("at.stats")));
    }

    @Override
    public boolean execute(CommandSender sender) {
        int uniqueLogins = PersistentData.getInstance().getActivityRecords().size();
        int numLogins = PersistentData.getInstance().getTotalNumberOfLogins();

        sender.sendMessage(ChatColor.AQUA + " === Activity Statistics ===");
        sender.sendMessage(ChatColor.AQUA + "Unique Logins: " + uniqueLogins);
        sender.sendMessage(ChatColor.AQUA + "Number of Logins: " + numLogins);
        return true;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        return execute(sender);
    }
}
