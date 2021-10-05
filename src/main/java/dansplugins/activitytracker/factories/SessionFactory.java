package dansplugins.activitytracker.factories;

import dansplugins.activitytracker.objects.Session;

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

    public Session createSession(UUID playerUUID) {
        return new Session(playerUUID);
    }

}
