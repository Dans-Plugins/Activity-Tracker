package dansplugins.activitytracker.algorithms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Algorithm for efficiently finding top N records by a numeric value.
 * Extracted from ActivityRecordService for better testability.
 * 
 * Uses efficient O(n log n) sorting instead of the previous O(n²) approach.
 * 
 * @author Daniel McCoy Stephenson
 */
public class TopRecordsAlgorithm {
    
    /**
     * Interface for objects that have a numeric value that can be compared.
     */
    public interface Scorable {
        double getScore();
    }
    
    /**
     * Gets the top N records sorted by score (descending).
     * 
     * @param records The list of records to analyze
     * @param count The number of top records to return (e.g., 10 for top 10)
     * @return List of top N records, or fewer if less than N records exist
     * @throws IllegalArgumentException if count is negative
     */
    public <T extends Scorable> List<T> getTopRecords(List<T> records, int count) {
        if (count < 0) {
            throw new IllegalArgumentException("Count cannot be negative");
        }
        
        if (records == null || records.isEmpty()) {
            return new ArrayList<>();
        }
        
        // Create a copy to avoid modifying the original list
        ArrayList<T> sortedRecords = new ArrayList<>(records);
        
        // Sort records by score in descending order
        Collections.sort(sortedRecords, new Comparator<T>() {
            @Override
            public int compare(T r1, T r2) {
                return Double.compare(r2.getScore(), r1.getScore());
            }
        });
        
        // Return the top N records (or fewer if less than N exist)
        int numRecords = Math.min(count, sortedRecords.size());
        return new ArrayList<>(sortedRecords.subList(0, numRecords));
    }
    
    /**
     * Convenience method to get the top 10 records.
     * 
     * @param records The list of records to analyze
     * @return List of top 10 records, or fewer if less than 10 records exist
     */
    public <T extends Scorable> List<T> getTopTenRecords(List<T> records) {
        return getTopRecords(records, 10);
    }
}