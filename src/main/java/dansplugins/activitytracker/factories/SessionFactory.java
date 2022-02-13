package dansplugins.activitytracker.factories;

import dansplugins.activitytracker.data.PersistentData;
import dansplugins.activitytracker.objects.Session;
import dansplugins.activitytracker.utils.Logger;
import org.bukkit.entity.Player;

import java.util.Random;
import java.util.UUID;

public class SessionFactory {
    private static SessionFactory instance;

    private SessionFactory() {

    }

    public static SessionFactory getInstance() {
        if (instance == null) {
            instance = new SessionFactory();
        }
        return instance;
    }

    public Session createSession(Player player) {
        Logger.getInstance().log("Creating session for " + player.getName());
        return createSession(player.getUniqueId());
    }

    public Session createSession(UUID playerUUID) {
        return new Session(getNewSessionID(), playerUUID);
    }

    private int getNewSessionID() {
        Random random = new Random();
        int numAttempts = 0;
        int maxAttempts = 25;
        int newID = -1;
        do {
            int maxMessageIDNumber = 100000;
            // int maxMessageIDNumber = ConfigManager.getInstance().getInt("maxMessageIDNumber");
            newID = random.nextInt(maxMessageIDNumber);
            numAttempts++;
        } while (isSessionIDTaken(newID) && numAttempts <= maxAttempts);
        return newID;
    }

    private boolean isSessionIDTaken(int sessionID) {
        return PersistentData.getInstance().getSession(sessionID) != null;
    }
}