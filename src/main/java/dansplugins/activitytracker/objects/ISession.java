package dansplugins.activitytracker.objects;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

public interface ISession {
    int getID();
    UUID getPlayerUUID();
    LocalDateTime getLoginDate();
    LocalDateTime getLogoutDate();
    int getMinutesSpent();
    boolean isActive();
    void setActive(boolean b);
    boolean endSession();
}