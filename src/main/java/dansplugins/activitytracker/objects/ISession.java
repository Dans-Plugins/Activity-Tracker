package dansplugins.activitytracker.objects;

import java.util.Date;
import java.util.UUID;

public interface ISession {
    UUID getPlayerUUID();
    Date getLoginDate();
    Date getLogoutDate();
    int getMinutesSpent();
}