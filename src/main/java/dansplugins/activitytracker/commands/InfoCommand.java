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
 * @author Daniel McCoy Stephenson
 */
public class InfoCommand extends AbstractPluginCommand {
    private final PersistentData persistentData;
    private final ActivityRecordService activityRecordService;

    public InfoCommand(PersistentData persistentData, ActivityRecordService activityRecordService) {
        super(new ArrayList<>(Arrays.asList("info")), new ArrayList<>(Arrays.asList("at.info")));
        this.persistentData = persistentData;
        this.activityRecordService = activityRecordService;
    }

    @Override
    public boolean execute(CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Usage: /at info (playerName)");
            return false;
        }
        Player player = (Player) sender;
        ActivityRecord record = persistentData.getActivityRecord(player.getUniqueId());
        if (record == null) {
            sender.sendMessage(ChatColor.RED + "You don't have an activity record.");
            return false;
        }

        record.sendInfoToSender(sender, activityRecordService);
        return true;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        String playerName = args[0];
        UUIDChecker uuidChecker = new UUIDChecker();
        UUID playerUUID = uuidChecker.findUUIDBasedOnPlayerName(playerName);
        if (playerUUID == null) {
            sender.sendMessage(ChatColor.RED + "That player wasn't found.");
            return false;
        }

        ActivityRecord record = persistentData.getActivityRecord(playerUUID);
        if (record == null) {
            sender.sendMessage(ChatColor.RED + "That player doesn't have an activity record.");
            return false;
        }

        record.sendInfoToSender(sender, activityRecordService);
        return true;
    }
}