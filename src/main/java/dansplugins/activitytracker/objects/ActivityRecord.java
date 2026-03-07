package dansplugins.activitytracker.objects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import dansplugins.activitytracker.exceptions.NoSessionException;
import preponderous.ponder.minecraft.bukkit.tools.UUIDChecker;
import preponderous.ponder.misc.abs.Savable;

/**
 * @author Daniel McCoy Stephenson
 */
public class ActivityRecord implements Savable {
    private UUID playerUUID;
    private ArrayList<Session> sessions = new ArrayList<>();
    private int mostRecentSessionID;
    private double hoursSpent;

    public ActivityRecord(UUID playerUUID, Session session) {
        this.playerUUID = playerUUID;
        sessions.add(session);
        this.mostRecentSessionID = session.getID();
        hoursSpent = 0;
    }

    public ActivityRecord(Map<String, String> data) {
        this.load(data);
    }

    public UUID getPlayerUUID() {
        return playerUUID;
    }

    public ArrayList<Session> getSessions() {
        return sessions;
    }

    public void addSession(Session session) {
        if (session != null) {
            sessions.add(session);
        }
    }

    public Session getMostRecentSession() throws NoSessionException {
        Session session = getSession(mostRecentSessionID);
        if (session == null) {
            // Try to recover by finding the actual most recent session
            if (sessions.size() > 0) {
                session = sessions.get(sessions.size() - 1);
                mostRecentSessionID = session.getID();
                return session;
            }
            throw new NoSessionException("The most recent session was null and no sessions exist for recovery.");
        }
        return session;
    }

    public void setMostRecentSession(Session newSession) {
        mostRecentSessionID = newSession.getID();
    }

    public double getHoursSpentNotIncludingTheCurrentSession() {
        return hoursSpent;
    }

    public double getTotalHoursSpent() {
        try {
            if (getMostRecentSession().isActive()) {
                return getHoursSpentNotIncludingTheCurrentSession() + getMostRecentSession().getMinutesSinceLogin()/60;
            }
            else {
                return getHoursSpentNotIncludingTheCurrentSession();
            }
        }
        catch (NoSessionException e) {
            return 0;
        }
    }

    public void setHoursSpent(double number) {
        if (number < 0) {
            hoursSpent = 0;
        } else {
            hoursSpent = number;
        }
    }

    public Session getSession(int ID) {
        for (Session session : sessions) {
            if (session.getID() == ID) {
                return session;
            }
        }
        return null;
    }

    public void sendInfoToSender(CommandSender sender, int rank, int totalPlayers) {
        UUIDChecker uuidChecker = new UUIDChecker();
        String playerName = uuidChecker.findPlayerNameBasedOnUUID(playerUUID);
        Session mostRecentSession;
        try {
            mostRecentSession = getMostRecentSession();
        }
        catch (NoSessionException e) {
            sender.sendMessage(ChatColor.RED + "The most recent session was null.");
            return;
        }
        double hours = -1;
        boolean online = Bukkit.getPlayer(playerUUID) != null;
        Session firstSession = getFirstSession();

        if (online) {
            try {
                hours = hoursSpent + getMostRecentSession().getMinutesSinceLogin() / 60;
            }
            catch (NoSessionException e) {
                sender.sendMessage(ChatColor.RED + "The most recent session was null.");
                return;
            }
        }
        else {
            hours = hoursSpent;
        }

        // Send empty line for visual separation
        sender.sendMessage("");

        // Header with player name
        sender.sendMessage(ChatColor.GOLD + "┌─ " + ChatColor.YELLOW + "" + ChatColor.BOLD + playerName +
                          ChatColor.RESET + "" + ChatColor.GOLD + " ─ Activity Info");

        // Number of logins
        sender.sendMessage(ChatColor.GOLD + "│ " + ChatColor.GRAY + "Logins:    " +
                          ChatColor.WHITE + sessions.size());

        // Play time
        sender.sendMessage(ChatColor.GOLD + "│ " + ChatColor.GRAY + "Play Time: " +
                          ChatColor.GREEN + String.format("%.2f", hours) + "h");

        // Activity ranking with visual bar
        if (rank > 0) {
            String rankBar = createRankBar(rank, totalPlayers);
            sender.sendMessage(ChatColor.GOLD + "│ " + ChatColor.GRAY + "Ranking:   " +
                              ChatColor.AQUA + rank + "/" + totalPlayers + " " +
                              ChatColor.DARK_GRAY + rankBar);
        }

        // Online/offline status
        if (online) {
            sender.sendMessage(ChatColor.GOLD + "│ " + ChatColor.GRAY + "Status:    " +
                              ChatColor.GREEN + "Online");
            sender.sendMessage(ChatColor.GOLD + "│ " + ChatColor.GRAY + "Session:   " +
                              ChatColor.WHITE + String.format("%.2f", mostRecentSession.getMinutesSinceLogin() / 60) + "h since login");
        }
        else {
            sender.sendMessage(ChatColor.GOLD + "│ " + ChatColor.GRAY + "Status:    " +
                              ChatColor.RED + "Offline");
            sender.sendMessage(ChatColor.GOLD + "│ " + ChatColor.GRAY + "Last Seen: " +
                              ChatColor.WHITE + String.format("%.2f", mostRecentSession.getMinutesSinceLogout() / 60) + "h ago");
        }

        // First recorded login
        if (firstSession != null) {
            sender.sendMessage(ChatColor.GOLD + "│ " + ChatColor.GRAY + "First Login: " +
                              ChatColor.WHITE + firstSession.getLoginDate().toString());
        }

        // Footer
        sender.sendMessage(ChatColor.GOLD + "└─────────────────────────");
    }

    /**
     * Creates a visual bar indicator for ranking position.
     * Higher rank (lower number) shows more filled blocks.
     */
    private String createRankBar(int rank, int totalPlayers) {
        int barLength = 10;
        int filled = (int) Math.round(((double)(totalPlayers - rank + 1) / totalPlayers) * barLength);
        filled = Math.max(0, Math.min(barLength, filled));
        StringBuilder bar = new StringBuilder("[");
        for (int i = 0; i < barLength; i++) {
            bar.append(i < filled ? "█" : "░");
        }
        bar.append("]");
        return bar.toString();
    }

    @Override
    public Map<String, String> save() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        Map<String, String> saveMap = new HashMap<>();
        saveMap.put("playerUUID", gson.toJson(playerUUID));
        saveMap.put("hoursSpent", gson.toJson(hoursSpent));

        saveMap.put("mostRecentSessionID", gson.toJson(mostRecentSessionID));

        return saveMap;
    }

    @Override
    public void load(Map<String, String> data) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        playerUUID = UUID.fromString(gson.fromJson(data.get("playerUUID"), String.class));
        hoursSpent = Double.parseDouble(gson.fromJson(data.get("hoursSpent"), String.class));

        mostRecentSessionID = Integer.parseInt(gson.fromJson(data.getOrDefault("mostRecentSessionID", "-1"), String.class));
    }

    private Session getFirstSession() {
        if (sessions.size() == 0) {
            return null;
        }
        return sessions.get(0);
    }
}