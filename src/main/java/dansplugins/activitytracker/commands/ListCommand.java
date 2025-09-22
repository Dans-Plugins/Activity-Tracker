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
        super(new ArrayList<>(Arrays.asList("list")), new ArrayList<>(Arrays.asList("at.admin")));
        this.persistentData = persistentData;
    }

    @Override
    public boolean execute(CommandSender sender) {
        // Get all sessions from all activity records
        List<Session> allSessions = new ArrayList<>();
        for (ActivityRecord record : persistentData.getActivityRecords()) {
            allSessions.addAll(record.getSessions());
        }

        // Sort sessions by login date in descending order (most recent first)
        List<Session> sortedSessions = allSessions.stream()
                .sorted(Comparator.comparing(Session::getLoginDate).reversed())
                .limit(10)
                .collect(Collectors.toList());

        if (sortedSessions.isEmpty()) {
            sender.sendMessage(ChatColor.RED + "No sessions found.");
            return true;
        }

        sender.sendMessage(ChatColor.AQUA + " === 10 Most Recent Sessions ===");
        
        UUIDChecker uuidChecker = new UUIDChecker();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        
        int count = 1;
        for (Session session : sortedSessions) {
            String playerName = uuidChecker.findPlayerNameBasedOnUUID(session.getPlayerUUID());
            if (playerName == null) {
                playerName = "Unknown Player";
            }
            
            String loginTime = session.getLoginDate().format(formatter);
            String status = session.isActive() ? ChatColor.GREEN + "Active" : ChatColor.RED + "Ended";
            
            String sessionInfo = String.format("%d. %s - Login: %s (%s%s%s)", 
                count, playerName, loginTime, status, ChatColor.AQUA, 
                session.isActive() ? "" : String.format(" - Duration: %.1f min", session.getMinutesSpent()));
            
            sender.sendMessage(ChatColor.AQUA + sessionInfo);
            count++;
        }
        
        return true;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        return execute(sender);
    }
}