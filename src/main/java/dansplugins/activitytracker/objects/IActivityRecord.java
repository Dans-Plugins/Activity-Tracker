package dansplugins.activitytracker.objects;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

public interface IActivityRecord {
    UUID getPlayerUUID();
    ArrayList<Session> getSessions();
    ISession getMostRecentSession();
    void setMostRecentSession(Session newSession);
    int getHoursSpent();
    void setHoursSpent(int number);
    Session getSession(int ID);
}