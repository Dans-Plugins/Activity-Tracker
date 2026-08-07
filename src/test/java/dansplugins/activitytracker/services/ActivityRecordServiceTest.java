package dansplugins.activitytracker.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.UUID;

import dansplugins.activitytracker.data.PersistentData;
import dansplugins.activitytracker.factories.ActivityRecordFactory;
import dansplugins.activitytracker.objects.ActivityRecord;
import dansplugins.activitytracker.objects.Session;
import dansplugins.activitytracker.utils.Logger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

/**
 * Unit tests for ActivityRecordService.
 * Covers the configurable record count used by /at top.
 *
 * @author Daniel McCoy Stephenson
 */
class ActivityRecordServiceTest {

    private Logger logger;
    private PersistentData persistentData;
    private ActivityRecordService activityRecordService;

    @BeforeEach
    void setUp() {
        logger = mock(Logger.class);
        persistentData = new PersistentData(logger);
        activityRecordService = new ActivityRecordService(persistentData, mock(ActivityRecordFactory.class), logger);
    }

    @Test
    @DisplayName("Should return an empty list when no records exist")
    void testGetTopRecords_NoRecords() {
        ArrayList<ActivityRecord> result = activityRecordService.getTopRecords(5);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should return the requested number of records sorted by hours descending")
    void testGetTopRecords_ReturnsRequestedCountInOrder() {
        addRecordWithHours(1.0);
        addRecordWithHours(5.0);
        addRecordWithHours(3.0);
        addRecordWithHours(9.0);

        ArrayList<ActivityRecord> result = activityRecordService.getTopRecords(3);

        assertEquals(3, result.size());
        assertEquals(9.0, result.get(0).getTotalHoursSpent(), 0.001);
        assertEquals(5.0, result.get(1).getTotalHoursSpent(), 0.001);
        assertEquals(3.0, result.get(2).getTotalHoursSpent(), 0.001);
    }

    @Test
    @DisplayName("Should return every record when fewer records exist than requested")
    void testGetTopRecords_FewerRecordsThanRequested() {
        addRecordWithHours(2.0);
        addRecordWithHours(4.0);

        ArrayList<ActivityRecord> result = activityRecordService.getTopRecords(25);

        assertEquals(2, result.size());
        assertEquals(4.0, result.get(0).getTotalHoursSpent(), 0.001);
    }

    @Test
    @DisplayName("Should return an empty list when zero records are requested")
    void testGetTopRecords_ZeroRequested() {
        addRecordWithHours(2.0);

        assertTrue(activityRecordService.getTopRecords(0).isEmpty());
    }

    @Test
    @DisplayName("Should throw when a negative number of records is requested")
    void testGetTopRecords_NegativeRequested() {
        addRecordWithHours(2.0);

        assertThrows(IllegalArgumentException.class, () -> activityRecordService.getTopRecords(-1));
    }

    @Test
    @DisplayName("Should return at most ten records from getTopTenRecords")
    void testGetTopTenRecords_LimitsToTen() {
        for (int i = 0; i < 12; i++) {
            addRecordWithHours(i);
        }

        ArrayList<ActivityRecord> result = activityRecordService.getTopTenRecords();

        assertEquals(10, result.size());
        assertEquals(11.0, result.get(0).getTotalHoursSpent(), 0.001);
        assertEquals(2.0, result.get(9).getTotalHoursSpent(), 0.001);
    }

    private void addRecordWithHours(double hours) {
        UUID playerUUID = UUID.randomUUID();
        Session session = new Session(logger, 1, playerUUID);
        session.setActive(false);
        ActivityRecord record = new ActivityRecord(playerUUID, session);
        record.setHoursSpent(hours);
        persistentData.addRecord(record);
    }
}
