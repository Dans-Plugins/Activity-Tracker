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
        
        if (records.isEmpty()) {
            sender.sendMessage(ChatColor.YELLOW + "No activity records found.");
            return false;
        }
        
        int count = 1;
        for (ActivityRecord record : records) {
            if (record == null) {
                continue;
            }
            
            try {
                UUIDChecker uuidChecker = new UUIDChecker();
                String playerName = uuidChecker.findPlayerNameBasedOnUUID(record.getPlayerUUID());
                
                if (playerName == null || playerName.isEmpty()) {
                    playerName = record.getPlayerUUID().toString();
                }

                sender.sendMessage(ChatColor.AQUA + "" + count + ") " + playerName + " - " + String.format("%.2f", record.getTotalHoursSpent()) + " hours");
                count++;
            } catch (Exception e) {
                System.err.println("ERROR: Failed to process activity record for UUID " + (record != null ? record.getPlayerUUID() : "unknown") + ": " + e.getMessage());
                System.err.println("ERROR: Failed to process activity record for UUID " + (record != null ? record.getPlayerUUID() : "unknown") + ": " + e.getMessage());
                continue;
            }
        }
        return false;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        return execute(sender);
    }
}