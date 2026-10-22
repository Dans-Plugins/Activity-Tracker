package dansplugins.activitytracker.algorithms;

import dansplugins.activitytracker.objects.ActivityRecord;
import dansplugins.activitytracker.objects.Session;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests for ActivityRecordAdapter.
 * Verifies that an ActivityRecord is scored by its total hours spent so that
 * TopRecordsAlgorithm ranks real records correctly.
 *
 * @author Daniel McCoy Stephenson
 */
class ActivityRecordAdapterTest {

    private static final int SESSION_ID = 1;

    private Session session;

    @BeforeEach
    void setUp() {
        session = mock(Session.class);
        when(session.getID()).thenReturn(SESSION_ID);
    }

    @Test
    @DisplayName("Should return the wrapped record")
    void testGetRecord_ReturnsWrappedInstance() {
        ActivityRecord record = new ActivityRecord(UUID.randomUUID(), session);

        ActivityRecordAdapter adapter = new ActivityRecordAdapter(record);

        assertSame(record, adapter.getRecord());
    }

    @Test
    @DisplayName("Should score an inactive record by its recorded hours")
    void testGetScore_InactiveSession() {
        when(session.isActive()).thenReturn(false);
        ActivityRecord record = new ActivityRecord(UUID.randomUUID(), session);
        record.setHoursSpent(12.5);

        ActivityRecordAdapter adapter = new ActivityRecordAdapter(record);

        assertEquals(12.5, adapter.getScore(), 0.001);
    }

    @Test
    @DisplayName("Should include the current session in the score of an active record")
    void testGetScore_ActiveSessionIncludesCurrentSession() {
        when(session.isActive()).thenReturn(true);
        when(session.getMinutesSinceLogin()).thenReturn(90.0);
        ActivityRecord record = new ActivityRecord(UUID.randomUUID(), session);
        record.setHoursSpent(2.0);

        ActivityRecordAdapter adapter = new ActivityRecordAdapter(record);

        assertEquals(3.5, adapter.getScore(), 0.001);
    }

    @Test
    @DisplayName("Should score a record whose sessions are all missing as zero")
    void testGetScore_NoSessions() {
        ActivityRecord record = new ActivityRecord(UUID.randomUUID(), session);
        record.setHoursSpent(7.0);
        record.getSessions().clear();

        ActivityRecordAdapter adapter = new ActivityRecordAdapter(record);

        assertEquals(0.0, adapter.getScore(), 0.001);
    }

    @Test
    @DisplayName("Should let TopRecordsAlgorithm rank records by total hours spent")
    void testGetTopRecords_RanksAdaptedRecordsByHours() {
        when(session.isActive()).thenReturn(false);
        ActivityRecord low = createRecord(1.0);
        ActivityRecord high = createRecord(30.0);
        ActivityRecord mid = createRecord(5.0);

        List<ActivityRecordAdapter> adapters = new ArrayList<>();
        adapters.add(new ActivityRecordAdapter(low));
        adapters.add(new ActivityRecordAdapter(high));
        adapters.add(new ActivityRecordAdapter(mid));

        List<ActivityRecordAdapter> top = new TopRecordsAlgorithm().getTopRecords(adapters, 2);

        assertEquals(2, top.size());
        assertSame(high, top.get(0).getRecord());
        assertSame(mid, top.get(1).getRecord());
    }

    private ActivityRecord createRecord(double hours) {
        ActivityRecord record = new ActivityRecord(UUID.randomUUID(), session);
        record.setHoursSpent(hours);
        return record;
    }
}
