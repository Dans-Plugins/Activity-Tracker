package dansplugins.activitytracker.factories;

import dansplugins.activitytracker.objects.ActivityRecord;
import dansplugins.activitytracker.objects.Session;
import dansplugins.activitytracker.utils.Logger;
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
        Logger.getInstance().log("Creating activity record for " + player.getName());
        Session session = SessionFactory.getInstance().createSession(player);
        ActivityRecord newRecord = new ActivityRecord(player.getUniqueId(), session);
        return newRecord;
    }
}