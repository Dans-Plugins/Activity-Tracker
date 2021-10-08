package dansplugins.activitytracker.objects;

import java.time.LocalDateTime;
import java.util.UUID;

public interface ISession {
    int getID();
    UUID getPlayerUUID();
    LocalDateTime getLoginDate();
    LocalDateTime getLogoutDate();
    double getMinutesSpent();
    boolean isActive();
    void setActive(boolean b);
    boolean endSession();
    double getMinutesSinceLogin();
    double getMinutesSinceLogout();
}