package dansplugins.activitytracker.objects;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dansplugins.activitytracker.utils.UUIDChecker;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ActivityRecord implements IActivityRecord, Savable {

    private UUID playerUUID;
    private ArrayList<Session> sessions = new ArrayList<>();
    private Session mostRecentSession;
    private double hoursSpent;

    public ActivityRecord(UUID playerUUID, Session session) {
        this.playerUUID = playerUUID;
        sessions.add(session);
        this.mostRecentSession = session;
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
        return mostRecentSession;
    }

    @Override
    public void setMostRecentSession(Session newSession) {
        mostRecentSession = newSession;
    }

    @Override
    public double getHoursSpent() {
        return hoursSpent;
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
        sender.sendMessage(ChatColor.AQUA + "=================================");
        sender.sendMessage(ChatColor.AQUA + "Activity Record for " + playerName);
        sender.sendMessage(ChatColor.AQUA + "Number of Logins: " + sessions.size());
        sender.sendMessage(ChatColor.AQUA + "Play Time: " + hoursSpent);
        // TODO: add last online
    }

    @Override
    public Map<String, String> save() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        Map<String, String> saveMap = new HashMap<>();
        saveMap.put("playerUUID", gson.toJson(playerUUID));
        saveMap.put("hoursSpent", gson.toJson(hoursSpent));

        return saveMap;
    }

    @Override
    public void load(Map<String, String> data) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        playerUUID = UUID.fromString(gson.fromJson(data.get("playerUUID"), String.class));
        hoursSpent = Double.parseDouble(gson.fromJson(data.get("hoursSpent"), String.class));
    }
}