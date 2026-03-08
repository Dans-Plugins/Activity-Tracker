package dansplugins.activitytracker.commands;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import dansplugins.activitytracker.data.PersistentData;
import dansplugins.activitytracker.objects.ActivityRecord;
import dansplugins.activitytracker.objects.Session;
import preponderous.ponder.minecraft.bukkit.abs.AbstractPluginCommand;
import preponderous.ponder.minecraft.bukkit.tools.UUIDChecker;

/**
 * @author Daniel McCoy Stephenson
 */
public class ListCommand extends AbstractPluginCommand {
    private final PersistentData persistentData;

    public ListCommand(PersistentData persistentData) {
        super(new ArrayList<>(Arrays.asList("list")), new ArrayList<>(Arrays.asList("at.list")));
        this.persistentData = persistentData;
    }

    @Override
    public boolean execute(CommandSender sender) {
        // Get all sessions from all activity records
        List<Session> allSessions = new ArrayList<>();
        for (ActivityRecord record : persistentData.getActivityRecords()) {
            if (record != null && record.getSessions() != null) {
                allSessions.addAll(record.getSessions());
            }
        }

        // Sort sessions by login date in descending order (most recent first)
        List<Session> sortedSessions = allSessions.stream()
                .filter(session -> session != null && session.getLoginDate() != null)
                .sorted(Comparator.comparing(Session::getLoginDate).reversed())
                .limit(10)
                .collect(Collectors.toList());

        int displayedCount = sortedSessions.size();

        sender.sendMessage("");
        sender.sendMessage(ChatColor.GOLD + "┌─ " + ChatColor.YELLOW + "" + ChatColor.BOLD + "Activity Tracker" +
                          ChatColor.RESET + ChatColor.GOLD + " ─ Recent Sessions (" + displayedCount + ")");

        if (sortedSessions.isEmpty()) {
            sender.sendMessage(ChatColor.GOLD + "│ " + ChatColor.GRAY + "No sessions found.");
            sender.sendMessage(ChatColor.GOLD + "└─────────────────────────");
            return true;
        }

        UUIDChecker uuidChecker = new UUIDChecker();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        
        int count = 1;
        for (Session session : sortedSessions) {
            String playerName = uuidChecker.findPlayerNameBasedOnUUID(session.getPlayerUUID());
            if (playerName == null) {
                playerName = "Unknown Player";
            }
            
            String loginTime = session.getLoginDate().format(formatter);
            String status = session.isActive() 
                ? ChatColor.GREEN + "Active" 
                : ChatColor.RED + "Ended";
            
            if (session.isActive()) {
                sender.sendMessage(ChatColor.GOLD + "│ " + ChatColor.AQUA + "#" + count + " " +
                                  ChatColor.WHITE + playerName + ChatColor.GRAY + " - " +
                                  ChatColor.WHITE + loginTime + " (" + status + ChatColor.GRAY + ")");
            } else {
                sender.sendMessage(ChatColor.GOLD + "│ " + ChatColor.AQUA + "#" + count + " " +
                                  ChatColor.WHITE + playerName + ChatColor.GRAY + " - " +
                                  ChatColor.WHITE + loginTime + " (" + status + ChatColor.GRAY + 
                                  " - " + ChatColor.WHITE + String.format("%.1f", session.getMinutesSpent()) + " min" + ChatColor.GRAY + ")");
            }
            count++;
        }
        sender.sendMessage(ChatColor.GOLD + "└─────────────────────────");
        
        return true;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        return execute(sender);
    }
}