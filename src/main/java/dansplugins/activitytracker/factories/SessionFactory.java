package dansplugins.activitytracker.factories;

import dansplugins.activitytracker.objects.Session;
import dansplugins.activitytracker.utils.Logger;
import org.bukkit.entity.Player;

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
        return new Session(playerUUID);
    }

}
