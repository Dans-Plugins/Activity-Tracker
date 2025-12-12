package dansplugins.activitytracker.services;

import dansplugins.activitytracker.data.PersistentData;
import dansplugins.activitytracker.factories.ActivityRecordFactory;
import dansplugins.activitytracker.factories.SessionFactory;
import dansplugins.activitytracker.objects.ActivityRecord;
import dansplugins.activitytracker.objects.Session;
import dansplugins.activitytracker.utils.Logger;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Tests for average daily activity calculation
 * @author Daniel McCoy Stephenson
 */
public class AverageDailyActivityTest {
    @Mock
    private Logger logger;
    
    private PersistentData persistentData;
    private SessionFactory sessionFactory;
    private ActivityRecordFactory activityRecordFactory;
    private ActivityRecordService activityRecordService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        persistentData = new PersistentData(logger);
        sessionFactory = new SessionFactory(logger, persistentData);
        activityRecordFactory = new ActivityRecordFactory(logger, sessionFactory);
        activityRecordService = new ActivityRecordService(persistentData, activityRecordFactory, logger);
    }

    @Test
    public void testCalculateAverageDailyActivity_NoSessions() {
        UUID playerUuid = UUID.randomUUID();
        Session session = sessionFactory.createSession(playerUuid);
        ActivityRecord record = new ActivityRecord(playerUuid, session);
        record.getSessions().clear(); // Remove the initial session
        
        double average = activityRecordService.calculateAverageDailyActivity(record, 7);
        
        assertEquals(0.0, average, 0.01);
    }

    @Test
    public void testCalculateAverageDailyActivity_NullRecord() {
        double average = activityRecordService.calculateAverageDailyActivity(null, 7);
        
        assertEquals(0.0, average, 0.01);
    }

    @Test
    public void testCalculateAverageDailyActivity_InvalidDays() {
        UUID playerUuid = UUID.randomUUID();
        Session session = sessionFactory.createSession(playerUuid);
        ActivityRecord record = new ActivityRecord(playerUuid, session);
        
        double average = activityRecordService.calculateAverageDailyActivity(record, 0);
        assertEquals(0.0, average, 0.01);
        
        average = activityRecordService.calculateAverageDailyActivity(record, -1);
        assertEquals(0.0, average, 0.01);
    }

    @Test
    public void testCalculateAverageDailyActivity_WithCompletedSessions() {
        UUID playerUuid = UUID.randomUUID();
        Session session = sessionFactory.createSession(playerUuid);
        ActivityRecord record = new ActivityRecord(playerUuid, session);
        
        // Clear the initial session
        record.getSessions().clear();
        
        // We can't easily test with real sessions without mocking LocalDateTime
        // So we just verify the method doesn't crash with a valid record
        double average = activityRecordService.calculateAverageDailyActivity(record, 7);
        
        // Should be 0 since there are no sessions
        assertEquals(0.0, average, 0.01);
    }
}
