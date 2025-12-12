package dansplugins.activitytracker.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
import java.util.HashMap;
import java.util.Map;
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
        MockitoAnnotations.openMocks(this);
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
        // Arrange
        UUID playerUuid = UUID.randomUUID();
        Session initialSession = sessionFactory.createSession(playerUuid);
        ActivityRecord record = new ActivityRecord(playerUuid, initialSession);
        record.getSessions().clear();

        // Create a session from 3 days ago that lasted 2 hours
        Session session1 = createSessionWithDate(playerUuid, LocalDateTime.now().minusDays(3), 120.0, false);
        record.addSession(session1);

        // Create a session from 5 days ago that lasted 3 hours
        Session session2 = createSessionWithDate(playerUuid, LocalDateTime.now().minusDays(5), 180.0, false);
        record.addSession(session2);

        // Create a session from 10 days ago that lasted 1 hour (should be excluded for 7 day period)
        Session session3 = createSessionWithDate(playerUuid, LocalDateTime.now().minusDays(10), 60.0, false);
        record.addSession(session3);

        // Act
        double average7Days = activityRecordService.calculateAverageDailyActivity(record, 7);
        double average30Days = activityRecordService.calculateAverageDailyActivity(record, 30);
        double total7Days = activityRecordService.calculateTotalHoursInPeriod(record, 7);
        double total30Days = activityRecordService.calculateTotalHoursInPeriod(record, 30);

        // Assert
        // For 7 days: session1 (2h) + session2 (3h) = 5h total, 5h/7days = 0.714h/day
        assertEquals(5.0, total7Days, 0.01);
        assertEquals(5.0 / 7, average7Days, 0.01);

        // For 30 days: all 3 sessions = 6h total, 6h/30days = 0.2h/day
        assertEquals(6.0, total30Days, 0.01);
        assertEquals(6.0 / 30, average30Days, 0.01);
    }

    /**
     * Helper method to create a session with a specific date for testing
     */
    private Session createSessionWithDate(UUID playerUuid, LocalDateTime loginDate, double minutesSpent, boolean active) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Map<String, String> data = new HashMap<>();
        data.put("ID", gson.toJson(1));
        data.put("playerUUID", gson.toJson(playerUuid.toString()));
        data.put("loginDate", gson.toJson(loginDate.toString()));
        data.put("logoutDate", gson.toJson(loginDate.plusMinutes((long)minutesSpent).toString()));
        data.put("minutesSpent", gson.toJson(minutesSpent));
        data.put("active", gson.toJson(active));
        
        return new Session(data, logger);
    }
}
