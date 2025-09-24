package dansplugins.activitytracker.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

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
        ArrayList<ActivityRecord> allRecords = new ArrayList<>(persistentData.getActivityRecords());
        
        // Sort records by total hours spent in descending order
        Collections.sort(allRecords, new Comparator<ActivityRecord>() {
            @Override
            public int compare(ActivityRecord r1, ActivityRecord r2) {
                return Double.compare(r2.getTotalHoursSpent(), r1.getTotalHoursSpent());
            }
        });
        
        // Return the top 10 records (or fewer if less than 10 exist)
        ArrayList<ActivityRecord> topTen = new ArrayList<>();
        int numRecords = Math.min(10, allRecords.size()); // TODO: make this a config option
        for (int i = 0; i < numRecords; i++) {
            topTen.add(allRecords.get(i));
        }
        
        return topTen;
    }
}