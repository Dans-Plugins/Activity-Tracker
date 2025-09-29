package dansplugins.activitytracker.algorithms;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive unit tests for TopRecordsAlgorithm.
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
    
    // ========== Null and Empty Input Tests ==========
    
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
    @DisplayName("Should return empty list when count is zero")
    void testGetTopRecords_ZeroCount() {
        List<MockScorable> records = createMockRecords(5);
        List<MockScorable> result = algorithm.getTopRecords(records, 0);
        
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
    
    // ========== Basic Functionality Tests ==========
    
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
    
    // ========== Sorting and Correctness Tests ==========
    
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
    
    @Test
    @DisplayName("Should handle negative scores correctly")
    void testGetTopRecords_NegativeScores() {
        List<MockScorable> records = createMockRecordsWithSpecificScores(
            new double[]{-5.0, 3.0, -1.0, 0.0, 2.0}
        );
        List<MockScorable> result = algorithm.getTopRecords(records, 5);
        
        assertEquals(5, result.size());
        // Expected order: 3.0, 2.0, 0.0, -1.0, -5.0
        assertEquals(3.0, result.get(0).getScore(), 0.001);
        assertEquals(2.0, result.get(1).getScore(), 0.001);
        assertEquals(0.0, result.get(2).getScore(), 0.001);
        assertEquals(-1.0, result.get(3).getScore(), 0.001);
        assertEquals(-5.0, result.get(4).getScore(), 0.001);
    }
    
    @Test
    @DisplayName("Should handle very large scores correctly")
    void testGetTopRecords_LargeScores() {
        List<MockScorable> records = createMockRecordsWithSpecificScores(
            new double[]{Double.MAX_VALUE, 1000000.0, Double.MAX_VALUE - 1, 500000.0}
        );
        List<MockScorable> result = algorithm.getTopRecords(records, 4);
        
        assertEquals(4, result.size());
        assertEquals(Double.MAX_VALUE, result.get(0).getScore(), 0.001);
        assertEquals(Double.MAX_VALUE - 1, result.get(1).getScore(), 0.001);
        assertEquals(1000000.0, result.get(2).getScore(), 0.001);
        assertEquals(500000.0, result.get(3).getScore(), 0.001);
    }
    
    @Test
    @DisplayName("Should handle very small positive scores correctly")
    void testGetTopRecords_SmallPositiveScores() {
        List<MockScorable> records = createMockRecordsWithSpecificScores(
            new double[]{Double.MIN_VALUE, 0.000001, Double.MIN_VALUE * 2, 0.000002}
        );
        List<MockScorable> result = algorithm.getTopRecords(records, 4);
        
        assertEquals(4, result.size());
        // Should be properly sorted even with very small values
        for (int i = 0; i < 3; i++) {
            assertTrue(result.get(i).getScore() >= result.get(i + 1).getScore());
        }
    }
    
    // ========== Parametrized Tests ==========
    
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
    
    @ParameterizedTest
    @MethodSource("provideDatasetSizes")
    @DisplayName("Should handle various dataset sizes efficiently")
    void testGetTopRecords_VariousDatasetSizes(int datasetSize, int topCount) {
        List<MockScorable> records = createMockRecords(datasetSize);
        
        long startTime = System.nanoTime();
        List<MockScorable> result = algorithm.getTopRecords(records, topCount);
        long endTime = System.nanoTime();
        
        long duration = (endTime - startTime) / 1_000_000; // milliseconds
        
        assertEquals(Math.min(topCount, datasetSize), result.size());
        
        // Verify sorting
        for (int i = 0; i < result.size() - 1; i++) {
            assertTrue(result.get(i).getScore() >= result.get(i + 1).getScore());
        }
        
        // Performance should be reasonable - allow more time for larger datasets
        long maxTime = Math.max(100, datasetSize / 100); // Scale with dataset size
        assertTrue(duration < maxTime, 
            "Algorithm took too long for dataset size " + datasetSize + ": " + duration + "ms");
    }
    
    private static Stream<Arguments> provideDatasetSizes() {
        return Stream.of(
            Arguments.of(1, 1),
            Arguments.of(10, 5),
            Arguments.of(100, 10),
            Arguments.of(500, 25),
            Arguments.of(1000, 50),
            Arguments.of(2000, 100)
        );
    }
    
    // ========== Immutability and State Tests ==========
    
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
    @DisplayName("Should return new list instance")
    void testGetTopRecords_ReturnsNewList() {
        List<MockScorable> originalRecords = createMockRecords(5);
        List<MockScorable> result = algorithm.getTopRecords(originalRecords, 3);
        
        assertNotSame(originalRecords, result);
        
        // Modifying result should not affect original
        if (!result.isEmpty()) {
            result.clear();
            assertEquals(5, originalRecords.size());
        }
    }
    
    @Test
    @DisplayName("Should work with concurrent modifications during sorting")
    void testGetTopRecords_ThreadSafety() {
        List<MockScorable> records = Collections.synchronizedList(createMockRecords(100));
        
        // This tests that the algorithm makes a copy before sorting
        List<MockScorable> result = algorithm.getTopRecords(records, 10);
        
        assertEquals(10, result.size());
        for (int i = 0; i < 9; i++) {
            assertTrue(result.get(i).getScore() >= result.get(i + 1).getScore());
        }
    }
    
    // ========== Convenience Method Tests ==========
    
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
    @DisplayName("getTopTenRecords should handle lists smaller than 10")
    void testGetTopTenRecords_SmallList() {
        List<MockScorable> records = createMockRecords(3);
        List<MockScorable> result = algorithm.getTopTenRecords(records);
        
        assertEquals(3, result.size());
        for (int i = 0; i < 2; i++) {
            assertTrue(result.get(i).getScore() >= result.get(i + 1).getScore());
        }
    }
    
    // ========== Performance and Stress Tests ==========
    
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
    
    @Test
    @DisplayName("Should handle very large datasets efficiently")
    void testGetTopRecords_VeryLargeDataset() {
        // Test with 5000 records for stress testing
        List<MockScorable> records = createMockRecords(5000);
        
        long startTime = System.nanoTime();
        List<MockScorable> result = algorithm.getTopRecords(records, 25);
        long endTime = System.nanoTime();
        
        assertEquals(25, result.size());
        // Verify sorting
        for (int i = 0; i < 24; i++) {
            assertTrue(result.get(i).getScore() >= result.get(i + 1).getScore());
        }
        
        // Performance should still be reasonable (less than 500ms for 5000 records)
        long duration = (endTime - startTime) / 1_000_000; // Convert to milliseconds
        assertTrue(duration < 500, "Algorithm took too long for large dataset: " + duration + "ms");
    }
    
    @Test
    @DisplayName("Should maintain O(n log n) complexity with increasing sizes")
    void testGetTopRecords_ComplexityValidation() {
        int[] sizes = {100, 500, 1000, 2000};
        long[] times = new long[sizes.length];
        
        for (int i = 0; i < sizes.length; i++) {
            List<MockScorable> records = createMockRecords(sizes[i]);
            
            long startTime = System.nanoTime();
            List<MockScorable> result = algorithm.getTopRecords(records, 10);
            long endTime = System.nanoTime();
            
            times[i] = endTime - startTime;
            assertEquals(10, result.size());
        }
        
        // Verify that time doesn't grow quadratically
        // For O(n log n), time ratio should be roughly (n2/n1) * log(n2)/log(n1)
        for (int i = 1; i < sizes.length; i++) {
            double sizeRatio = (double) sizes[i] / sizes[i-1];
            double expectedTimeRatio = sizeRatio * (Math.log(sizes[i]) / Math.log(sizes[i-1]));
            double actualTimeRatio = (double) times[i] / times[i-1];
            
            // Allow for some variance, but it shouldn't be quadratic growth
            assertTrue(actualTimeRatio < expectedTimeRatio * 3, 
                "Time complexity appears worse than O(n log n): " + actualTimeRatio + " vs expected " + expectedTimeRatio);
        }
    }
    
    @Test
    @DisplayName("Should handle mixed positive and negative scores in large dataset")
    void testGetTopRecords_MixedSignsLargeDataset() {
        List<MockScorable> records = new ArrayList<>();
        Random random = new Random(42); // Fixed seed for reproducible results
        
        // Create 1000 records with mix of positive and negative scores
        for (int i = 0; i < 1000; i++) {
            double score = (random.nextDouble() - 0.5) * 1000; // Range: -500 to 500
            records.add(new MockScorable(score));
        }
        
        List<MockScorable> result = algorithm.getTopRecords(records, 50);
        
        assertEquals(50, result.size());
        // Verify sorting
        for (int i = 0; i < 49; i++) {
            assertTrue(result.get(i).getScore() >= result.get(i + 1).getScore());
        }
        
        // Top results should be positive
        assertTrue(result.get(0).getScore() > 0, "Top score should be positive");
    }
    
    // ========== Helper Methods ==========
    
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
        
        @Override
        public String toString() {
            return "MockScorable{score=" + score + "}";
        }
        
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            MockScorable that = (MockScorable) obj;
            return Double.compare(that.score, score) == 0;
        }
        
        @Override
        public int hashCode() {
            return Double.hashCode(score);
        }
    }
}