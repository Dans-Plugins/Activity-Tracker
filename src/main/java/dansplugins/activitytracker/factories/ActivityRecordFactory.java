package dansplugins.activitytracker.factories;

import dansplugins.activitytracker.data.PersistentData;
import dansplugins.activitytracker.objects.ActivityRecord;
import dansplugins.activitytracker.objects.Session;
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
        Session session = SessionFactory.getInstance().createSession(player.getUniqueId());
        ActivityRecord newRecord = new ActivityRecord(player.getUniqueId(), session);
        return newRecord;
    }

}
