package dansplugins.activitytracker.managers;

import org.bukkit.entity.Player;

public class ActivityRecordManager {

    private static ActivityRecordManager instance;

    private ActivityRecordManager() {

    }
    public static ActivityRecordManager getInstance() {
        if (instance == null) {
            instance = new ActivityRecordManager();
        }
        return instance;
    }


    public void assignActivityRecordToPlayerIfNecessary(Player player) {
        if ()
    }
}
