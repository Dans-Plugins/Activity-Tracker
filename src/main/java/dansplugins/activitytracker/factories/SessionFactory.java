package dansplugins.activitytracker.factories;

import java.util.Random;
import java.util.UUID;

import dansplugins.activitytracker.data.PersistentData;
import org.bukkit.entity.Player;

import dansplugins.activitytracker.objects.Session;
import dansplugins.activitytracker.utils.Logger;

/**
 * @author Daniel McCoy Stephenson
 */
public class SessionFactory {
    private final Logger logger;
    private final PersistentData persistentData;

    public SessionFactory(Logger logger, PersistentData persistentData) {
        this.logger = logger;
        this.persistentData = persistentData;
    }

    public Session createSession(Player player) {
        logger.log("Creating session for " + player.getName());
        return createSession(player.getUniqueId());
    }

    public Session createSession(UUID playerUUID) {
        return new Session(logger, getNewSessionID(), playerUUID);
    }

    private int getNewSessionID() {
        Random random = new Random();
        int numAttempts = 0;
        int maxAttempts = 25;
        int newID = -1;
        do {
            int maxMessageIDNumber = 100000;
            newID = random.nextInt(maxMessageIDNumber);
            numAttempts++;
        } while (isSessionIDTaken(newID) && numAttempts <= maxAttempts);
        return newID;
    }

    private boolean isSessionIDTaken(int sessionID) {
        return persistentData.getSession(sessionID) != null;
    }
}