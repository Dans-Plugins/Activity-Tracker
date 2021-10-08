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
        sender.sendMessage(ChatColor.AQUA + "=== Most Active Players ===");
        for (ActivityRecord record : records) {
            if (record == null) {
                continue;
            }
            String playerName = UUIDChecker.getInstance().findPlayerNameBasedOnUUID(record.getPlayerUUID());
            int count = 1;
            sender.sendMessage(ChatColor.AQUA + "" + count + ") " + playerName + " - " + String.format("%.2f", record.getHoursSpentNotIncludingTheCurrentSession()) + " hours");
            count++;
        }
        return false;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        // unused
        return false;
    }
}
