package dansplugins.activitytracker.commands;

import dansplugins.activitytracker.managers.ActivityRecordManager;
import dansplugins.activitytracker.objects.ActivityRecord;
import dansplugins.activitytracker.utils.UUIDChecker;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;

public class TopCommand implements ICommand {

    @Override
    public boolean execute(CommandSender sender) {
        ArrayList<ActivityRecord> records = ActivityRecordManager.getInstance().getTopTenRecords();
        for (ActivityRecord record : records) {
            if (record == null) {
                continue;
            }
            String playerName = UUIDChecker.getInstance().findPlayerNameBasedOnUUID(record.getPlayerUUID());
            sender.sendMessage(ChatColor.AQUA + playerName + " - " + String.format("%.2f", record.getHoursSpent()));
        }
        return false;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        // unused
        return false;
    }
}
