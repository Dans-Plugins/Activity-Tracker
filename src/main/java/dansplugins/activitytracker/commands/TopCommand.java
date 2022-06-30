package dansplugins.activitytracker.commands;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import dansplugins.activitytracker.objects.ActivityRecord;
import dansplugins.activitytracker.services.ActivityRecordService;
import preponderous.ponder.minecraft.bukkit.abs.AbstractPluginCommand;
import preponderous.ponder.minecraft.bukkit.tools.UUIDChecker;

/**
 * @author Daniel McCoy Stephenson
 */
public class TopCommand extends AbstractPluginCommand {
    private final ActivityRecordService activityRecordService;

    public TopCommand(ActivityRecordService activityRecordService) {
        super(new ArrayList<>(Arrays.asList("top")), new ArrayList<>(Arrays.asList("at.top")));
        this.activityRecordService = activityRecordService;
    }

    @Override
    public boolean execute(CommandSender sender) {
        ArrayList<ActivityRecord> records = activityRecordService.getTopTenRecords();
        sender.sendMessage(ChatColor.AQUA + "=== Most Active Players ===");
        int count = 1;
        for (ActivityRecord record : records) {
            if (record == null) {
                continue;
            }
            UUIDChecker uuidChecker = new UUIDChecker();
            String playerName = uuidChecker.findPlayerNameBasedOnUUID(record.getPlayerUUID());

            sender.sendMessage(ChatColor.AQUA + "" + count + ") " + playerName + " - " + String.format("%.2f", record.getTotalHoursSpent()) + " hours");
            count++;
        }
        return false;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        return execute(sender);
    }
}