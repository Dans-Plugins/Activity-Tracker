package dansplugins.activitytracker.algorithms;

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
 * Tests the optimized O(n log n) sorting algorithm for finding top records.
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
        List<MockScorable> result = algorithm.getTopRecords(null, 10);
        
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
    
    @Test
    @DisplayName("Should return empty list when input is empty")
    void testGetTopRecords_EmptyInput() {
        List<MockScorable> emptyList = new ArrayList<>();
        List<MockScorable> result = algorithm.getTopRecords(emptyList, 10);
        
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
    
    @Test
    @DisplayName("Should throw exception when count is negative")
    void testGetTopRecords_NegativeCount() {
        List<MockScorable> records = createMockRecords(5);
        
        assertThrows(IllegalArgumentException.class, () -> 
            algorithm.getTopRecords(records, -1)
        );
    }
    
    @Test
    @DisplayName("Should return single record when only one exists")
    void testGetTopRecords_SingleRecord() {
        List<MockScorable> records = createMockRecords(1);
        List<MockScorable> result = algorithm.getTopRecords(records, 10);
        
        assertEquals(1, result.size());
        assertEquals(records.get(0).getScore(), result.get(0).getScore(), 0.001);
    }
    
    @Test
    @DisplayName("Should return all records when count exceeds list size")
    void testGetTopRecords_CountExceedsSize() {
        List<MockScorable> records = createMockRecords(3);
        List<MockScorable> result = algorithm.getTopRecords(records, 10);
        
        assertEquals(3, result.size());
        // Should be sorted by score descending
        assertTrue(result.get(0).getScore() >= result.get(1).getScore());
        assertTrue(result.get(1).getScore() >= result.get(2).getScore());
    }
    
    @Test
    @DisplayName("Should return exactly N records when count is less than list size")
    void testGetTopRecords_CountLessThanSize() {
        List<MockScorable> records = createMockRecords(10);
        List<MockScorable> result = algorithm.getTopRecords(records, 5);
        
        assertEquals(5, result.size());
        // Should return the top 5 records
        for (int i = 0; i < 4; i++) {
            assertTrue(result.get(i).getScore() >= result.get(i + 1).getScore());
        }
    }
    
    @Test
    @DisplayName("Should return records sorted by score in descending order")
    void testGetTopRecords_ProperSorting() {
        List<MockScorable> records = createMockRecordsWithSpecificScores(
            new double[]{1.5, 10.0, 5.0, 2.5, 8.0}
        );
        List<MockScorable> result = algorithm.getTopRecords(records, 5);
        
        assertEquals(5, result.size());
        // Expected order: 10.0, 8.0, 5.0, 2.5, 1.5
        assertEquals(10.0, result.get(0).getScore(), 0.001);
        assertEquals(8.0, result.get(1).getScore(), 0.001);
        assertEquals(5.0, result.get(2).getScore(), 0.001);
        assertEquals(2.5, result.get(3).getScore(), 0.001);
        assertEquals(1.5, result.get(4).getScore(), 0.001);
    }
    
    @Test
    @DisplayName("Should handle records with identical scores")
    void testGetTopRecords_IdenticalScores() {
        List<MockScorable> records = createMockRecordsWithSpecificScores(
            new double[]{5.0, 5.0, 5.0, 1.0, 1.0}
        );
        List<MockScorable> result = algorithm.getTopRecords(records, 3);
        
        assertEquals(3, result.size());
        // All top 3 should have 5.0 score (order of identical values may vary)
        for (int i = 0; i < 3; i++) {
            assertEquals(5.0, result.get(i).getScore(), 0.001);
        }
    }
    
    @Test
    @DisplayName("Should handle zero scores correctly")
    void testGetTopRecords_ZeroScores() {
        List<MockScorable> records = createMockRecordsWithSpecificScores(
            new double[]{0.0, 1.0, 0.0, 2.0}
        );
        List<MockScorable> result = algorithm.getTopRecords(records, 4);
        
        assertEquals(4, result.size());
        // Expected order: 2.0, 1.0, 0.0, 0.0
        assertEquals(2.0, result.get(0).getScore(), 0.001);
        assertEquals(1.0, result.get(1).getScore(), 0.001);
        assertEquals(0.0, result.get(2).getScore(), 0.001);
        assertEquals(0.0, result.get(3).getScore(), 0.001);
    }
    
    @ParameterizedTest
    @ValueSource(ints = {0, 1, 5, 10, 15})
    @DisplayName("Should handle various count values correctly")
    void testGetTopRecords_VariousCounts(int count) {
        List<MockScorable> records = createMockRecords(10);
        List<MockScorable> result = algorithm.getTopRecords(records, count);
        
        assertEquals(Math.min(count, 10), result.size());
        
        // Verify sorting for non-empty results
        if (!result.isEmpty()) {
            for (int i = 0; i < result.size() - 1; i++) {
                assertTrue(result.get(i).getScore() >= result.get(i + 1).getScore());
            }
        }
    }
    
    @Test
    @DisplayName("Should not modify original list")
    void testGetTopRecords_DoesNotModifyOriginal() {
        List<MockScorable> originalRecords = createMockRecords(5);
        double[] originalScores = new double[originalRecords.size()];
        for (int i = 0; i < originalRecords.size(); i++) {
            originalScores[i] = originalRecords.get(i).getScore();
        }
        
        algorithm.getTopRecords(originalRecords, 3);
        
        // Original list should remain unchanged
        assertEquals(5, originalRecords.size());
        for (int i = 0; i < originalRecords.size(); i++) {
            assertEquals(originalScores[i], originalRecords.get(i).getScore(), 0.001);
        }
    }
    
    @Test
    @DisplayName("getTopTenRecords should return same result as getTopRecords with count 10")
    void testGetTopTenRecords_ConvenienceMethod() {
        List<MockScorable> records = createMockRecords(15);
        
        List<MockScorable> resultFromGeneric = algorithm.getTopRecords(records, 10);
        List<MockScorable> resultFromTopTen = algorithm.getTopTenRecords(records);
        
        assertEquals(resultFromGeneric.size(), resultFromTopTen.size());
        assertEquals(10, resultFromTopTen.size());
        
        for (int i = 0; i < resultFromGeneric.size(); i++) {
            assertEquals(resultFromGeneric.get(i).getScore(), resultFromTopTen.get(i).getScore(), 0.001);
        }
    }
    
    @Test
    @DisplayName("Should handle large datasets efficiently")
    void testGetTopRecords_LargeDataset() {
        // Test with 1000 records to ensure O(n log n) performance
        List<MockScorable> records = createMockRecords(1000);
        
        long startTime = System.nanoTime();
        List<MockScorable> result = algorithm.getTopRecords(records, 10);
        long endTime = System.nanoTime();
        
        assertEquals(10, result.size());
        // Verify sorting
        for (int i = 0; i < 9; i++) {
            assertTrue(result.get(i).getScore() >= result.get(i + 1).getScore());
        }
        
        // Performance should be reasonable (less than 100ms for 1000 records)
        long duration = (endTime - startTime) / 1_000_000; // Convert to milliseconds
        assertTrue(duration < 100, "Algorithm took too long: " + duration + "ms");
    }
    
    /**
     * Helper method to create mock Scorable objects for testing.
     * Creates records with scores equal to their index (0.0, 1.0, 2.0, etc.)
     */
    private List<MockScorable> createMockRecords(int count) {
        List<MockScorable> records = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            records.add(new MockScorable(i));
        }
        return records;
    }
    
    /**
     * Helper method to create mock Scorable objects with specific scores.
     */
    private List<MockScorable> createMockRecordsWithSpecificScores(double[] scores) {
        List<MockScorable> records = new ArrayList<>();
        for (double score : scores) {
            records.add(new MockScorable(score));
        }
        return records;
    }
    
    /**
     * Mock implementation of Scorable for testing purposes.
     */
    private static class MockScorable implements TopRecordsAlgorithm.Scorable {
        private final double score;
        
        public MockScorable(double score) {
            this.score = score;
        }
        
        @Override
        public double getScore() {
            return score;
        }
    }
}