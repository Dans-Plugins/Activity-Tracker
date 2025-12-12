package dansplugins.activitytracker.services;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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
import dansplugins.activitytracker.objects.Session;

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

    /**
     * Calculate average daily activity for a player over the specified number of days
     * @param record The activity record to analyze
     * @param days The number of days to calculate the average over (default: 7)
     * @return Average hours per day
     */
    public double calculateAverageDailyActivity(ActivityRecord record, int days) {
        if (record == null || days <= 0) {
            return 0.0;
        }

        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(days);
        double totalHours = 0.0;

        // Sum up hours from sessions within the time period
        for (Session session : record.getSessions()) {
            if (session.getLoginDate().isAfter(cutoffDate)) {
                if (session.isActive()) {
                    // For active sessions, calculate time since login
                    totalHours += session.getMinutesSinceLogin() / 60.0;
                } else {
                    // For completed sessions, use the stored minutes spent
                    totalHours += session.getMinutesSpent() / 60.0;
                }
            }
        }

        // Calculate average
        return totalHours / days;
    }
}