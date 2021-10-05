package dansplugins.activitytracker.objects;

import java.util.Date;
import java.util.UUID;

public interface ISession {
    int getID();
    UUID getPlayerUUID();
    Date getLoginDate();
    Date getLogoutDate();
    int getMinutesSpent();
    boolean isActive();
    void setActive(boolean b);
    boolean endSession();
}