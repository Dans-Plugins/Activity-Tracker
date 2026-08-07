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
    private static final int DEFAULT_COUNT = 10;
    private static final int MAX_COUNT = 100;

    public TopCommand(ActivityRecordService activityRecordService) {
        super(new ArrayList<>(Arrays.asList("top")), new ArrayList<>(Arrays.asList("at.top")));
        this.activityRecordService = activityRecordService;
    }

    @Override
    public boolean execute(CommandSender sender) {
        return displayTopRecords(sender, DEFAULT_COUNT);
    }

    private boolean displayTopRecords(CommandSender sender, int count) {
        ArrayList<ActivityRecord> records = activityRecordService.getTopRecords(count);

        sender.sendMessage("");
        sender.sendMessage(ChatColor.GOLD + "┌─ " + ChatColor.YELLOW + "" + ChatColor.BOLD + "Activity Tracker" +
                          ChatColor.RESET + ChatColor.GOLD + " ─ Top Players");
        
        if (records.isEmpty()) {
            sender.sendMessage(ChatColor.GOLD + "│ " + ChatColor.GRAY + "No activity records found.");
            sender.sendMessage(ChatColor.GOLD + "└─────────────────────────");
            return false;
        }

        // Find the max hours for bar scaling
        double maxHours = 0;
        for (ActivityRecord record : records) {
            if (record != null && record.getTotalHoursSpent() > maxHours) {
                maxHours = record.getTotalHoursSpent();
            }
        }
        
        int rank = 1;
        UUIDChecker uuidChecker = new UUIDChecker();
        for (ActivityRecord record : records) {
            if (record == null) {
                continue;
            }
            
            try {
                String playerName = uuidChecker.findPlayerNameBasedOnUUID(record.getPlayerUUID());
                
                if (playerName == null || playerName.isEmpty()) {
                    playerName = record.getPlayerUUID().toString();
                }

                String bar = createBar(record.getTotalHoursSpent(), maxHours > 0 ? maxHours : 1);
                sender.sendMessage(ChatColor.GOLD + "│ " + ChatColor.AQUA + "#" + rank + " " +
                                  ChatColor.WHITE + playerName + " " +
                                  ChatColor.GREEN + String.format("%.2f", record.getTotalHoursSpent()) + "h " +
                                  ChatColor.DARK_GRAY + bar);
                rank++;
            } catch (Exception e) {
                continue;
            }
        }
        sender.sendMessage(ChatColor.GOLD + "└─────────────────────────");
        return true;
    }

    private String createBar(double value, double max) {
        int barLength = 10;
        int filled = (int) Math.min(barLength, (value / max) * barLength);
        StringBuilder bar = new StringBuilder("[");
        for (int i = 0; i < barLength; i++) {
            bar.append(i < filled ? "\u2588" : "\u2591");
        }
        bar.append("]");
        return bar.toString();
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            return execute(sender);
        }

        int count;
        try {
            count = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "Invalid number of players. Usage: /at top [number]");
            return false;
        }

        if (count <= 0) {
            sender.sendMessage(ChatColor.RED + "The number of players must be a positive number.");
            return false;
        }

        if (count > MAX_COUNT) {
            sender.sendMessage(ChatColor.RED + "You can view at most " + MAX_COUNT + " players at a time.");
            return false;
        }

        return displayTopRecords(sender, count);
    }
}