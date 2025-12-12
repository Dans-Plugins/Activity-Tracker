package dansplugins.activitytracker.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import dansplugins.activitytracker.data.PersistentData;
import dansplugins.activitytracker.objects.ActivityRecord;
import dansplugins.activitytracker.services.ActivityRecordService;
import preponderous.ponder.minecraft.bukkit.abs.AbstractPluginCommand;
import preponderous.ponder.minecraft.bukkit.tools.UUIDChecker;

/**
 * Command to display average daily activity
 * @author Daniel McCoy Stephenson
 */
public class AverageCommand extends AbstractPluginCommand {
    private final PersistentData persistentData;
    private final ActivityRecordService activityRecordService;
    private static final int DEFAULT_DAYS = 7;

    public AverageCommand(PersistentData persistentData, ActivityRecordService activityRecordService) {
        super(new ArrayList<>(Arrays.asList("average")), new ArrayList<>(Arrays.asList("at.average")));
        this.persistentData = persistentData;
        this.activityRecordService = activityRecordService;
    }

    @Override
    public boolean execute(CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Usage: /at average [playerName] [days]");
            return false;
        }
        Player player = (Player) sender;
        return displayAverageActivity(sender, player.getUniqueId(), DEFAULT_DAYS);
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            return execute(sender);
        }

        // Parse arguments
        String playerName = args[0];
        int days = DEFAULT_DAYS;

        // Check if second argument is a number (days)
        if (args.length > 1) {
            try {
                days = Integer.parseInt(args[1]);
                if (days <= 0) {
                    sender.sendMessage(ChatColor.RED + "Days must be a positive number.");
                    return false;
                }
            } catch (NumberFormatException e) {
                sender.sendMessage(ChatColor.RED + "Invalid number of days. Using default: " + DEFAULT_DAYS);
                days = DEFAULT_DAYS;
            }
        }

        // Get player UUID
        UUIDChecker uuidChecker = new UUIDChecker();
        UUID playerUUID = uuidChecker.findUUIDBasedOnPlayerName(playerName);
        if (playerUUID == null) {
            sender.sendMessage(ChatColor.RED + "That player wasn't found.");
            return false;
        }

        return displayAverageActivity(sender, playerUUID, days);
    }

    private boolean displayAverageActivity(CommandSender sender, UUID playerUUID, int days) {
        ActivityRecord record = persistentData.getActivityRecord(playerUUID);
        if (record == null) {
            sender.sendMessage(ChatColor.RED + "That player doesn't have an activity record.");
            return false;
        }

        UUIDChecker uuidChecker = new UUIDChecker();
        String playerName = uuidChecker.findPlayerNameBasedOnUUID(playerUUID);
        if (playerName == null) {
            playerName = playerUUID.toString();
        }

        double averageHoursPerDay = activityRecordService.calculateAverageDailyActivity(record, days);
        double totalHours = activityRecordService.calculateTotalHoursInPeriod(record, days);

        sender.sendMessage(ChatColor.AQUA + "=================================");
        sender.sendMessage(ChatColor.AQUA + "Average Daily Activity for " + playerName);
        sender.sendMessage(ChatColor.AQUA + "=================================");
        sender.sendMessage(ChatColor.AQUA + "Period: Last " + days + " day" + (days > 1 ? "s" : ""));
        sender.sendMessage(ChatColor.AQUA + "Total Hours in Period: " + String.format("%.2f", totalHours));
        sender.sendMessage(ChatColor.AQUA + "Average Hours Per Day: " + String.format("%.2f", averageHoursPerDay));
        sender.sendMessage(ChatColor.AQUA + "=================================");
        return true;
    }
}
