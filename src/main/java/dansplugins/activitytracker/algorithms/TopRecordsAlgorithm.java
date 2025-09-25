package dansplugins.activitytracker.algorithms;

import dansplugins.activitytracker.objects.ActivityRecord;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Algorithm for efficiently finding top N activity records by hours played.
 * Extracted from ActivityRecordService for better testability.
 * 
 * Uses efficient O(n log n) sorting instead of the previous O(n²) approach.
 * 
 * @author Daniel McCoy Stephenson
 */
public class TopRecordsAlgorithm {
    
    /**
     * Gets the top N activity records sorted by total hours spent (descending).
     * 
     * @param records The list of activity records to analyze
     * @param count The number of top records to return (e.g., 10 for top 10)
     * @return List of top N records, or fewer if less than N records exist
     * @throws IllegalArgumentException if count is negative
     */
    public List<ActivityRecord> getTopRecords(List<ActivityRecord> records, int count) {
        if (count < 0) {
            throw new IllegalArgumentException("Count cannot be negative");
        }
        
        if (records == null || records.isEmpty()) {
            return new ArrayList<>();
        }
        
        // Create a copy to avoid modifying the original list
        ArrayList<ActivityRecord> sortedRecords = new ArrayList<>(records);
        
        // Sort records by total hours spent in descending order
        Collections.sort(sortedRecords, new Comparator<ActivityRecord>() {
            @Override
            public int compare(ActivityRecord r1, ActivityRecord r2) {
                return Double.compare(r2.getTotalHoursSpent(), r1.getTotalHoursSpent());
            }
        });
        
        // Return the top N records (or fewer if less than N exist)
        int numRecords = Math.min(count, sortedRecords.size());
        return new ArrayList<>(sortedRecords.subList(0, numRecords));
    }
    
    /**
     * Convenience method to get the top 10 records.
     * 
     * @param records The list of activity records to analyze
     * @return List of top 10 records, or fewer if less than 10 records exist
     */
    public List<ActivityRecord> getTopTenRecords(List<ActivityRecord> records) {
        return getTopRecords(records, 10);
    }
}