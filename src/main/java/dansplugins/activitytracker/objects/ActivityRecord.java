package dansplugins.activitytracker.objects;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dansplugins.activitytracker.utils.UUIDChecker;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ActivityRecord implements IActivityRecord, Savable {

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

    @Override
    public UUID getPlayerUUID() {
        return playerUUID;
    }

    @Override
    public ArrayList<Session> getSessions() {
        return sessions;
    }

    @Override
    public Session getMostRecentSession() {
        Session session = getSession(mostRecentSessionID);
        if (session == null) {
            return sessions.get(sessions.size() - 1);
        }
        return session;
    }

    @Override
    public void setMostRecentSession(Session newSession) {
        mostRecentSessionID = newSession.getID();
    }

    @Override
    public double getHoursSpentNotIncludingTheCurrentSession() {
        return hoursSpent;
    }

    public double getTotalHoursSpent() {
        if (getMostRecentSession().isActive()) {
            return getHoursSpentNotIncludingTheCurrentSession() + getMostRecentSession().getMinutesSinceLogin()/60;
        }
        else {
            return getHoursSpentNotIncludingTheCurrentSession();
        }
    }

    @Override
    public void setHoursSpent(double number) {
        hoursSpent = number;
    }

    @Override
    public Session getSession(int ID) {
        for (Session session : sessions) {
            if (session.getID() == ID) {
                return session;
            }
        }
        return null;
    }

    @Override
    public void sendInfoToSender(CommandSender sender) {
        String playerName = UUIDChecker.getInstance().findPlayerNameBasedOnUUID(playerUUID);
        Session mostRecentSession = getMostRecentSession();
        double hours = -1;
        boolean online = Bukkit.getPlayer(playerUUID) != null;

        if (online) {
            hours = hoursSpent + getMostRecentSession().getMinutesSinceLogin()/60;
        }
        else {
            hours = hoursSpent;
        }

        sender.sendMessage(ChatColor.AQUA + "=================================");
        sender.sendMessage(ChatColor.AQUA + "Activity Record for " + playerName);
        sender.sendMessage(ChatColor.AQUA + "=================================");
        sender.sendMessage(ChatColor.AQUA + "Number of Logins: " + sessions.size());
        sender.sendMessage(ChatColor.AQUA + "Play Time: " + String.format("%.2f", hours) + " hours");
        if (online) {
            sender.sendMessage(ChatColor.AQUA + "Status: Online");
            sender.sendMessage(ChatColor.AQUA + "Time Since Login: " + String.format("%.2f", mostRecentSession.getMinutesSinceLogin()/60) + " hours");
        }
        else {
            sender.sendMessage(ChatColor.AQUA + "Status: Offline");
            sender.sendMessage(ChatColor.AQUA + "Time Since Logout: " + String.format("%.2f", mostRecentSession.getMinutesSinceLogout()/60) + " hours");
        }
        sender.sendMessage(ChatColor.AQUA + "=================================");
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
}