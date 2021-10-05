package dansplugins.activitytracker.factories;

import org.bukkit.entity.Player;

public class ActivityRecordFactory {

    private static ActivityRecordFactory instance;

    private ActivityRecordFactory() {

    }

    public static ActivityRecordFactory getInstance() {
        if (instance == null) {
            instance = new ActivityRecordFactory();
        }
        return instance;
    }

    public ActivityRecord createActivityRecord(Player player) {
        // TODO: implement
        return true;
    }

}
