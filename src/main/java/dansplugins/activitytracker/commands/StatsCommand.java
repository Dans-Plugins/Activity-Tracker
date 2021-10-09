package dansplugins.activitytracker.commands;

import dansplugins.activitytracker.data.PersistentData;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class StatsCommand implements ICommand {

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
        // unused
        return false;
    }
}
