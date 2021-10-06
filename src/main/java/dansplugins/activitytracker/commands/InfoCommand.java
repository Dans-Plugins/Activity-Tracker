package dansplugins.activitytracker.commands;

import dansplugins.activitytracker.data.PersistentData;
import dansplugins.activitytracker.objects.ActivityRecord;
import dansplugins.activitytracker.utils.UUIDChecker;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.UUID;

public class InfoCommand {

    public boolean execute(CommandSender sender, String[] args) {
        // TODO: implement

        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Usage: /at info (playerName)");
            return false;
        }

        String playerName = args[0];
        UUID playerUUID = UUIDChecker.getInstance().findUUIDBasedOnPlayerName(playerName);
        if (playerUUID == null) {
            sender.sendMessage(ChatColor.RED + "That player wasn't found.");
            return false;
        }

        ActivityRecord record = PersistentData.getInstance().getActivityRecord(playerUUID);
        if (record == null) {
            sender.sendMessage(ChatColor.RED + "That player doesn't have an activity record.");
            return false;
        }

        record.sendInfoToSender(sender);
        return true;
    }

}
