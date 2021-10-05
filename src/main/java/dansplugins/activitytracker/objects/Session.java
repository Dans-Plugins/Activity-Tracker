package dansplugins.activitytracker.objects;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

public class Session implements ISession {

    private UUID playerUUID;
    private Date loginDate;
    private Date logoutDate;
    private int minutesSpent;
    private boolean active;

    public Session(UUID playerUUID) {
        this.playerUUID = playerUUID;
        loginDate = new Date();
        active = true;
    }

    public Session(Map<String, String> data) {
        // TODO: implement
        // this.load(data);
    }

    @Override
    public UUID getPlayerUUID() {
        return playerUUID;
    }

    @Override
    public Date getLoginDate() {
        return loginDate;
    }

    @Override
    public Date getLogoutDate() {
        return logoutDate;
    }

    @Override
    public int getMinutesSpent() {
        return minutesSpent;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public void setActive(boolean b) {
        active = b;
    }

    @Override
    public boolean endSession() {
        if (!active) {
            return false;
        }
        logoutDate = new Date();
        minutesSpent = calculateMinutesSpent();
        setActive(false);
        return true;
    }

    private int calculateMinutesSpent() {
        // TODO: implement
        return 0;
    }
}