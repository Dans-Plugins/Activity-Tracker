package dansplugins.activitytracker.services;

import java.util.ArrayList;
import java.util.List;

import dansplugins.activitytracker.algorithms.TopRecordsAlgorithm;
import dansplugins.activitytracker.algorithms.ActivityRecordAdapter;
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
    private final TopRecordsAlgorithm topRecordsAlgorithm;

    public ActivityRecordService(PersistentData persistentData, ActivityRecordFactory activityRecordFactory, Logger logger) {
        this.persistentData = persistentData;
        this.activityRecordFactory = activityRecordFactory;
        this.logger = logger;
        this.topRecordsAlgorithm = new TopRecordsAlgorithm();
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
        
        // Wrap records in adapters for the generic algorithm
        List<ActivityRecordAdapter> adapters = new ArrayList<>();
        for (ActivityRecord record : allRecords) {
            adapters.add(new ActivityRecordAdapter(record));
        }
        
        // Use the generic algorithm
        List<ActivityRecordAdapter> topAdapters = topRecordsAlgorithm.getTopTenRecords(adapters);
        
        // Extract the original records
        ArrayList<ActivityRecord> result = new ArrayList<>();
        for (ActivityRecordAdapter adapter : topAdapters) {
            result.add(adapter.getRecord());
        }
        
        return result;
    }
}