package dansplugins.activitytracker.services;

import dansplugins.activitytracker.data.PersistentData;
import dansplugins.activitytracker.factories.ActivityRecordFactory;
import dansplugins.activitytracker.objects.ActivityRecord;
import dansplugins.activitytracker.utils.Logger;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class LocalActivityRecordService {
    private static LocalActivityRecordService instance;

    private LocalActivityRecordService() {

    }
    public static LocalActivityRecordService getInstance() {
        if (instance == null) {
            instance = new LocalActivityRecordService();
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
        ArrayList<ActivityRecord> toIgnore = new ArrayList<>();
        ArrayList<ActivityRecord> toReturn = new ArrayList<>();

        int numRecords = 10; // TODO: make this a config option
        for (int i = 0; i < numRecords; i++) {
            ActivityRecord topRecord = getTopRecord(toIgnore);
            if (topRecord == null) {
                break;
            }
            toReturn.add(topRecord);
            toIgnore.add(topRecord);
        }

        return toReturn;
    }

    public ActivityRecord getTopRecord(ArrayList<ActivityRecord> toIgnore) {
        Logger.getInstance().log("Attempting to get top record, ignoring " + toIgnore.size() + " records.");
        ActivityRecord toReturn = null;
        double max = 0;
        for (ActivityRecord record : PersistentData.getInstance().getActivityRecords()) {
            if (toIgnore.contains(record)) {
                Logger.getInstance().log("Record for " + record.getPlayerUUID().toString() + " is in the ignore list.");
                continue;
            }
            if (record.getTotalHoursSpent() > max) {
                toReturn = record;
                max = record.getTotalHoursSpent();
            }
        }
        if (toReturn != null) {
            Logger.getInstance().log("Record for " + toReturn.getPlayerUUID() + " is the top record currently. " + toIgnore.size() + " records were ignored when performing this search.");
        }
        return toReturn;
    }
}