package dansplugins.activitytracker.services;

import java.util.ArrayList;

import dansplugins.activitytracker.factories.ActivityRecordFactory;
import dansplugins.activitytracker.utils.Logger;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import dansplugins.activitytracker.data.PersistentData;
import dansplugins.activitytracker.objects.ActivityRecord;

/**
 * @author Daniel McCoy Stephenson
 */
public class ActivityRecordService {
    private final PersistentData persistentData;
    private final ActivityRecordFactory activityRecordFactory;
    private final Logger logger;

    public ActivityRecordService(PersistentData persistentData, ActivityRecordFactory activityRecordFactory, Logger logger) {
        this.persistentData = persistentData;
        this.activityRecordFactory = activityRecordFactory;
        this.logger = logger;
    }

    public boolean assignActivityRecordToPlayerIfNecessary(Player player) {
        if (persistentData.getActivityRecord(player) != null) {
            // player has an activity record
            return false;
        }

        // player doesn't have an activity record

        ActivityRecord newRecord = activityRecordFactory.createActivityRecord(player);
        persistentData.addRecord(newRecord);
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
        logger.log("Attempting to get top record, ignoring " + toIgnore.size() + " records.");
        ActivityRecord toReturn = null;
        double max = 0;
        for (ActivityRecord record : persistentData.getActivityRecords()) {
            if (toIgnore.contains(record)) {
                logger.log("Record for " + record.getPlayerUUID().toString() + " is in the ignore list.");
                continue;
            }
            if (record.getTotalHoursSpent() > max) {
                toReturn = record;
                max = record.getTotalHoursSpent();
            }
        }
        if (toReturn != null) {
            logger.log("Record for " + toReturn.getPlayerUUID() + " is the top record currently. " + toIgnore.size() + " records were ignored when performing this search.");
        }
        return toReturn;
    }

    public int getPlayerRank(ActivityRecord playerRecord) {
        if (playerRecord == null) {
            return -1;
        }
        
        double playerHours = playerRecord.getTotalHoursSpent();
        int rank = 1;
        
        for (ActivityRecord record : persistentData.getActivityRecords()) {
            if (record != playerRecord && record.getTotalHoursSpent() > playerHours) {
                rank++;
            }
        }
        
        return rank;
    }
}