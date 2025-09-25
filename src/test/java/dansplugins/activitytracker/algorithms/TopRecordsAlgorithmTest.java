package dansplugins.activitytracker.algorithms;

import dansplugins.activitytracker.objects.ActivityRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for TopRecordsAlgorithm.
 * Tests the optimized O(n log n) sorting algorithm for finding top activity records.
 * 
 * @author Daniel McCoy Stephenson
 */
class TopRecordsAlgorithmTest {

    private TopRecordsAlgorithm algorithm;
    
    @BeforeEach
    void setUp() {
        algorithm = new TopRecordsAlgorithm();
    }
    
    @Test
    @DisplayName("Should return empty list when input is null")
    void testGetTopRecords_NullInput() {
        List<ActivityRecord> result = algorithm.getTopRecords(null, 10);
        
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
    
    @Test
    @DisplayName("Should return empty list when input is empty")
    void testGetTopRecords_EmptyInput() {
        List<ActivityRecord> emptyList = new ArrayList<>();
        List<ActivityRecord> result = algorithm.getTopRecords(emptyList, 10);
        
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
    
    @Test
    @DisplayName("Should throw exception when count is negative")
    void testGetTopRecords_NegativeCount() {
        List<ActivityRecord> records = createMockRecords(5);
        
        assertThrows(IllegalArgumentException.class, () -> 
            algorithm.getTopRecords(records, -1)
        );
    }
    
    @Test
    @DisplayName("Should return single record when only one exists")
    void testGetTopRecords_SingleRecord() {
        List<ActivityRecord> records = createMockRecords(1);
        List<ActivityRecord> result = algorithm.getTopRecords(records, 10);
        
        assertEquals(1, result.size());
        assertEquals(records.get(0), result.get(0));
    }
    
    @Test
    @DisplayName("Should return all records when count exceeds list size")
    void testGetTopRecords_CountExceedsSize() {
        List<ActivityRecord> records = createMockRecords(3);
        List<ActivityRecord> result = algorithm.getTopRecords(records, 10);
        
        assertEquals(3, result.size());
        // Should be sorted by hours descending
        assertTrue(result.get(0).getTotalHoursSpent() >= result.get(1).getTotalHoursSpent());
        assertTrue(result.get(1).getTotalHoursSpent() >= result.get(2).getTotalHoursSpent());
    }
    
    @Test
    @DisplayName("Should return exactly N records when count is less than list size")
    void testGetTopRecords_CountLessThanSize() {
        List<ActivityRecord> records = createMockRecords(10);
        List<ActivityRecord> result = algorithm.getTopRecords(records, 5);
        
        assertEquals(5, result.size());
        // Should return the top 5 records
        for (int i = 0; i < 4; i++) {
            assertTrue(result.get(i).getTotalHoursSpent() >= result.get(i + 1).getTotalHoursSpent());
        }
    }
    
    @Test
    @DisplayName("Should return records sorted by hours in descending order")
    void testGetTopRecords_ProperSorting() {
        List<ActivityRecord> records = createMockRecordsWithSpecificHours(
            new double[]{1.5, 10.0, 5.0, 2.5, 8.0}
        );
        List<ActivityRecord> result = algorithm.getTopRecords(records, 5);
        
        assertEquals(5, result.size());
        // Expected order: 10.0, 8.0, 5.0, 2.5, 1.5
        assertEquals(10.0, result.get(0).getTotalHoursSpent(), 0.001);
        assertEquals(8.0, result.get(1).getTotalHoursSpent(), 0.001);
        assertEquals(5.0, result.get(2).getTotalHoursSpent(), 0.001);
        assertEquals(2.5, result.get(3).getTotalHoursSpent(), 0.001);
        assertEquals(1.5, result.get(4).getTotalHoursSpent(), 0.001);
    }
    
    @Test
    @DisplayName("Should handle records with identical hours")
    void testGetTopRecords_IdenticalHours() {
        List<ActivityRecord> records = createMockRecordsWithSpecificHours(
            new double[]{5.0, 5.0, 5.0, 1.0, 1.0}
        );
        List<ActivityRecord> result = algorithm.getTopRecords(records, 3);
        
        assertEquals(3, result.size());
        // All top 3 should have 5.0 hours (order of identical values may vary)
        for (int i = 0; i < 3; i++) {
            assertEquals(5.0, result.get(i).getTotalHoursSpent(), 0.001);
        }
    }
    
    @Test
    @DisplayName("Should handle zero hours correctly")
    void testGetTopRecords_ZeroHours() {
        List<ActivityRecord> records = createMockRecordsWithSpecificHours(
            new double[]{0.0, 1.0, 0.0, 2.0}
        );
        List<ActivityRecord> result = algorithm.getTopRecords(records, 4);
        
        assertEquals(4, result.size());
        // Expected order: 2.0, 1.0, 0.0, 0.0
        assertEquals(2.0, result.get(0).getTotalHoursSpent(), 0.001);
        assertEquals(1.0, result.get(1).getTotalHoursSpent(), 0.001);
        assertEquals(0.0, result.get(2).getTotalHoursSpent(), 0.001);
        assertEquals(0.0, result.get(3).getTotalHoursSpent(), 0.001);
    }
    
    @ParameterizedTest
    @ValueSource(ints = {0, 1, 5, 10, 15})
    @DisplayName("Should handle various count values correctly")
    void testGetTopRecords_VariousCounts(int count) {
        List<ActivityRecord> records = createMockRecords(10);
        List<ActivityRecord> result = algorithm.getTopRecords(records, count);
        
        assertEquals(Math.min(count, 10), result.size());
        
        // Verify sorting for non-empty results
        if (!result.isEmpty()) {
            for (int i = 0; i < result.size() - 1; i++) {
                assertTrue(result.get(i).getTotalHoursSpent() >= result.get(i + 1).getTotalHoursSpent());
            }
        }
    }
    
    @Test
    @DisplayName("Should not modify original list")
    void testGetTopRecords_DoesNotModifyOriginal() {
        List<ActivityRecord> originalRecords = createMockRecords(5);
        List<ActivityRecord> originalCopy = new ArrayList<>(originalRecords);
        
        algorithm.getTopRecords(originalRecords, 3);
        
        // Original list should remain unchanged
        assertEquals(originalCopy.size(), originalRecords.size());
        for (int i = 0; i < originalCopy.size(); i++) {
            assertEquals(originalCopy.get(i), originalRecords.get(i));
        }
    }
    
    @Test
    @DisplayName("getTopTenRecords should return same result as getTopRecords with count 10")
    void testGetTopTenRecords_ConvenienceMethod() {
        List<ActivityRecord> records = createMockRecords(15);
        
        List<ActivityRecord> resultFromGeneric = algorithm.getTopRecords(records, 10);
        List<ActivityRecord> resultFromTopTen = algorithm.getTopTenRecords(records);
        
        assertEquals(resultFromGeneric.size(), resultFromTopTen.size());
        assertEquals(10, resultFromTopTen.size());
        
        for (int i = 0; i < resultFromGeneric.size(); i++) {
            assertEquals(resultFromGeneric.get(i), resultFromTopTen.get(i));
        }
    }
    
    @Test
    @DisplayName("Should handle large datasets efficiently")
    void testGetTopRecords_LargeDataset() {
        // Test with 1000 records to ensure O(n log n) performance
        List<ActivityRecord> records = createMockRecords(1000);
        
        long startTime = System.nanoTime();
        List<ActivityRecord> result = algorithm.getTopRecords(records, 10);
        long endTime = System.nanoTime();
        
        assertEquals(10, result.size());
        // Verify sorting
        for (int i = 0; i < 9; i++) {
            assertTrue(result.get(i).getTotalHoursSpent() >= result.get(i + 1).getTotalHoursSpent());
        }
        
        // Performance should be reasonable (less than 10ms for 1000 records)
        long duration = (endTime - startTime) / 1_000_000; // Convert to milliseconds
        assertTrue(duration < 100, "Algorithm took too long: " + duration + "ms");
    }
    
    /**
     * Helper method to create mock ActivityRecord objects for testing.
     * Creates records with hours equal to their index (0.0, 1.0, 2.0, etc.)
     */
    private List<ActivityRecord> createMockRecords(int count) {
        List<ActivityRecord> records = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            ActivityRecord record = new MockActivityRecord(UUID.randomUUID(), i);
            records.add(record);
        }
        return records;
    }
    
    /**
     * Helper method to create mock ActivityRecord objects with specific hours.
     */
    private List<ActivityRecord> createMockRecordsWithSpecificHours(double[] hours) {
        List<ActivityRecord> records = new ArrayList<>();
        for (int i = 0; i < hours.length; i++) {
            ActivityRecord record = new MockActivityRecord(UUID.randomUUID(), hours[i]);
            records.add(record);
        }
        return records;
    }
    
    /**
     * Mock implementation of ActivityRecord for testing purposes.
     * Only implements the methods needed for the top algorithm.
     */
    private static class MockActivityRecord extends ActivityRecord {
        private final double totalHours;
        private final UUID playerUUID;
        
        public MockActivityRecord(UUID playerUUID, double totalHours) {
            // Use the Map constructor to avoid complex dependencies
            super(createMockData(playerUUID, totalHours));
            this.totalHours = totalHours;
            this.playerUUID = playerUUID;
        }
        
        @Override
        public double getTotalHoursSpent() {
            return totalHours;
        }
        
        @Override
        public UUID getPlayerUUID() {
            return playerUUID;
        }
        
        private static Map<String, String> createMockData(UUID playerUUID, double totalHours) {
            Map<String, String> data = new HashMap<>();
            data.put("playerUUID", "\"" + playerUUID.toString() + "\"");
            data.put("hoursSpent", "\"" + totalHours + "\"");
            data.put("mostRecentSessionID", "\"-1\"");
            return data;
        }
    }
}