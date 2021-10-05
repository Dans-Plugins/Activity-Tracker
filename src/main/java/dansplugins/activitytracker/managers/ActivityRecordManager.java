package dansplugins.activitytracker.managers;

import dansplugins.activitytracker.data.PersistentData;
import dansplugins.activitytracker.factories.ActivityRecordFactory;
import dansplugins.activitytracker.objects.ActivityRecord;
import org.bukkit.ChatColor;
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


    public boolean assignActivityRecordToPlayerIfNecessary(Player player) {
        if (PersistentData.getInstance().getActivityRecord(player) != null) {
            // player has an activity record
            return false;
        }

        // player doesn't have an activity record

        ActivityRecord newRecord = ActivityRecordFactory.getInstance().createActivityRecord(player);
        PersistentData.getInstance().addRecord(newRecord);
        player.sendMessage(ChatColor.AQUA + "You've been assigned an activity record.");
        return true;
    }
}
