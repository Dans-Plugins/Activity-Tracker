package dansplugins.activitytracker.objects;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

public class ActivityRecord implements IActivityRecord, Savable {

    private UUID playerUUID;
    private ArrayList<ISession> sessions = new ArrayList<>();
    private Session mostRecentSession;
    private int hoursSpent;

    public ActivityRecord(UUID playerUUID, Session session) {
        this.playerUUID = playerUUID;
        sessions.add(session);
        this.mostRecentSession = session;
        hoursSpent = 0;
    }

    public ActivityRecord(Map<String, String> data) {
        // TODO: implement
        // this.load(data);
    }

    @Override
    public UUID getPlayerUUID() {
        return playerUUID;
    }

    @Override
    public ArrayList<ISession> getSessions() {
        return sessions;
    }

    @Override
    public ISession getMostRecentSession() {
        return mostRecentSession;
    }

    @Override
    public void setMostRecentSession(Session newSession) {
        mostRecentSession = newSession;
    }

    @Override
    public int getHoursSpent() {
        return hoursSpent;
    }

    @Override
    public void setHoursSpent(int number) {
        hoursSpent = number;
    }

    @Override
    public ISession getSession(int ID) {
        for (ISession session : sessions) {
            if (session.getID() == ID) {
                return session;
            }
        }
        return null;
    }

    @Override
    public Map<String, String> save() {
        // TODO: implement
        return null;
    }

    @Override
    public void load(Map<String, String> data) {
        // TODO: implement
    }
}