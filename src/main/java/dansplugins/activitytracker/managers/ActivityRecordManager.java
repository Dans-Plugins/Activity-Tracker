package dansplugins.activitytracker.managers;

import dansplugins.activitytracker.data.PersistentData;
import dansplugins.activitytracker.factories.ActivityRecordFactory;
import dansplugins.activitytracker.objects.ActivityRecord;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;

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

    public ArrayList<ActivityRecord> getTopTenRecords() {
        ArrayList<ActivityRecord> copyOfActivityRecords = getCopyOfActivityRecords();
        ArrayList<ActivityRecord> toReturn = new ArrayList<>();

        int topNumber = 10; // TODO: make this a config option
        for (int i = 0; i < topNumber; i++) {
            ActivityRecord topRecord = getTopRecord(copyOfActivityRecords);
            toReturn.add(topRecord);
            copyOfActivityRecords.remove(topRecord);
        }

        return toReturn;
    }

    public ActivityRecord getTopRecord(ArrayList<ActivityRecord> records) {
        ActivityRecord toReturn = null;
        double max = 0;
        for (ActivityRecord record : records) {
            if (record.getHoursSpent() > max) {
                toReturn = record;
                max = record.getHoursSpent();
            }
        }
        return toReturn;
    }

    private ArrayList<ActivityRecord> getCopyOfActivityRecords() {
        ArrayList<ActivityRecord> toReturn = new ArrayList<>();
        for (ActivityRecord record : PersistentData.getInstance().getActivityRecords()) {
            toReturn.add(record);
        }
        return toReturn;
    }
}
