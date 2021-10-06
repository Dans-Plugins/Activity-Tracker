package dansplugins.activitytracker.objects;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Session implements ISession, Savable {

    private int ID;
    private UUID playerUUID;
    private Date loginDate;
    private Date logoutDate;
    private int minutesSpent;
    private boolean active;

    public Session(int ID, UUID playerUUID) {
        this.ID = ID;
        this.playerUUID = playerUUID;
        loginDate = new Date();
        active = true;
    }

    public Session(Map<String, String> data) {
        this.load(data);
    }

    @Override
    public int getID() {
        return ID;
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

    @Override
    public Map<String, String> save() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        Map<String, String> saveMap = new HashMap<>();
        saveMap.put("ID", gson.toJson(ID));
        saveMap.put("playerUUID", gson.toJson(playerUUID));
        saveMap.put("loginDate", gson.toJson(loginDate));
        saveMap.put("logoutDate", gson.toJson(logoutDate));
        saveMap.put("minutesSpent", gson.toJson(minutesSpent));
        saveMap.put("active", gson.toJson(active));

        return saveMap;
    }

    @Override
    public void load(Map<String, String> data) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        ID = Integer.parseInt(gson.fromJson(data.get("ID"), String.class));
        playerUUID = UUID.fromString(gson.fromJson(data.get("playerUUID"), String.class));
        loginDate = new Date(gson.fromJson(data.get("loginDate"), String.class));

    }

}