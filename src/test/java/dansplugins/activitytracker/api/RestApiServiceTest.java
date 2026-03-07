package dansplugins.activitytracker.api;

import com.google.gson.Gson;
import dansplugins.activitytracker.data.PersistentData;
import dansplugins.activitytracker.objects.ActivityRecord;
import dansplugins.activitytracker.objects.Session;
import dansplugins.activitytracker.services.ActivityRecordService;
import dansplugins.activitytracker.utils.Logger;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for RestApiService
 * @author Daniel McCoy Stephenson
 */
public class RestApiServiceTest {

    @Mock
    private PersistentData persistentData;

    @Mock
    private ActivityRecordService activityRecordService;

    @Mock
    private Logger logger;

    @Mock
    private ActivityRecord mockRecord;

    @Mock
    private Session mockSession;

    private Gson gson;
    private int testPort = 8080;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        gson = new Gson();
    }

    @Test
    public void testRestApiServiceCreation() {
        // Act
        RestApiService service = new RestApiService(persistentData, activityRecordService, logger, testPort);

        // Assert
        assertNotNull(service);
    }

    @Test
    public void testStatsResponseSerialization() {
        // Arrange
        int uniqueLogins = 150;
        int totalLogins = 2543;
        StatsResponse stats = new StatsResponse(uniqueLogins, totalLogins);

        // Act
        String json = gson.toJson(stats);
        StatsResponse deserialized = gson.fromJson(json, StatsResponse.class);

        // Assert
        assertNotNull(json);
        assertTrue(json.contains("\"uniqueLogins\":150"));
        assertTrue(json.contains("\"totalLogins\":2543"));
        assertEquals(uniqueLogins, deserialized.getUniqueLogins());
        assertEquals(totalLogins, deserialized.getTotalLogins());
    }

    @Test
    public void testLeaderboardEntrySerialization() {
        // Arrange
        String playerUuid = "a1b2c3d4-e5f6-7890-abcd-ef1234567890";
        String playerName = "Steve";
        double hoursPlayed = 123.45;
        int totalLogins = 87;
        LeaderboardEntry entry = new LeaderboardEntry(playerUuid, playerName, hoursPlayed, totalLogins);

        // Act
        String json = gson.toJson(entry);
        LeaderboardEntry deserialized = gson.fromJson(json, LeaderboardEntry.class);

        // Assert
        assertNotNull(json);
        assertTrue(json.contains("\"playerUuid\":\"" + playerUuid + "\""));
        assertTrue(json.contains("\"playerName\":\"" + playerName + "\""));
        assertTrue(json.contains("\"hoursPlayed\":123.45"));
        assertTrue(json.contains("\"totalLogins\":87"));
        assertEquals(playerUuid, deserialized.getPlayerUuid());
        assertEquals(playerName, deserialized.getPlayerName());
        assertEquals(hoursPlayed, deserialized.getHoursPlayed(), 0.001);
        assertEquals(totalLogins, deserialized.getTotalLogins());
    }

    @Test
    public void testPlayerActivityResponseSerialization() {
        // Arrange
        PlayerActivityResponse response = new PlayerActivityResponse(
            "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
            "Steve",
            87,
            123.45,
            true,
            "2023-01-15T14:30:00",
            "2023-10-26T10:15:00",
            null,
            2.5,
            null
        );

        // Act
        String json = gson.toJson(response);
        PlayerActivityResponse deserialized = gson.fromJson(json, PlayerActivityResponse.class);

        // Assert
        assertNotNull(json);
        assertTrue(json.contains("\"playerUuid\""));
        assertTrue(json.contains("\"playerName\":\"Steve\""));
        assertTrue(json.contains("\"currentlyOnline\":true"));
        assertEquals(response.getPlayerUuid(), deserialized.getPlayerUuid());
        assertEquals(response.getPlayerName(), deserialized.getPlayerName());
        assertEquals(response.getTotalLogins(), deserialized.getTotalLogins());
        assertEquals(response.getTotalHoursPlayed(), deserialized.getTotalHoursPlayed(), 0.001);
        assertEquals(response.isCurrentlyOnline(), deserialized.isCurrentlyOnline());
    }

    @Test
    public void testStatsDataRetrieval() {
        // Arrange
        ArrayList<ActivityRecord> mockRecords = new ArrayList<>();
        mockRecords.add(mockRecord);
        mockRecords.add(mockRecord);
        when(persistentData.getActivityRecords()).thenReturn(mockRecords);
        when(persistentData.getTotalNumberOfLogins()).thenReturn(10);

        // Act
        int uniqueLogins = persistentData.getActivityRecords().size();
        int totalLogins = persistentData.getTotalNumberOfLogins();

        // Assert
        assertEquals(2, uniqueLogins);
        assertEquals(10, totalLogins);
        verify(persistentData, times(1)).getActivityRecords();
        verify(persistentData, times(1)).getTotalNumberOfLogins();
    }

    @Test
    public void testLeaderboardDataRetrieval() {
        // Arrange
        ArrayList<ActivityRecord> topRecords = new ArrayList<>();
        topRecords.add(mockRecord);
        when(activityRecordService.getTopTenRecords()).thenReturn(topRecords);
        when(mockRecord.getPlayerUUID()).thenReturn(UUID.fromString("a1b2c3d4-e5f6-7890-abcd-ef1234567890"));
        when(mockRecord.getTotalHoursSpent()).thenReturn(100.5);
        ArrayList<Session> sessions = new ArrayList<>();
        sessions.add(mockSession);
        when(mockRecord.getSessions()).thenReturn(sessions);

        // Act
        ArrayList<ActivityRecord> records = activityRecordService.getTopTenRecords();

        // Assert
        assertNotNull(records);
        assertEquals(1, records.size());
        assertEquals(100.5, records.get(0).getTotalHoursSpent(), 0.001);
        verify(activityRecordService, times(1)).getTopTenRecords();
    }

    @Test
    public void testEmptyLeaderboardDataRetrieval() {
        // Arrange
        ArrayList<ActivityRecord> emptyRecords = new ArrayList<>();
        when(activityRecordService.getTopTenRecords()).thenReturn(emptyRecords);

        // Act
        ArrayList<ActivityRecord> records = activityRecordService.getTopTenRecords();

        // Assert
        assertNotNull(records);
        assertEquals(0, records.size());
        verify(activityRecordService, times(1)).getTopTenRecords();
    }

    @Test
    public void testPlayerDataRetrieval() {
        // Arrange
        UUID testUuid = UUID.fromString("a1b2c3d4-e5f6-7890-abcd-ef1234567890");
        when(persistentData.getActivityRecord(testUuid)).thenReturn(mockRecord);
        when(mockRecord.getSessions()).thenReturn(new ArrayList<>());
        when(mockRecord.getTotalHoursSpent()).thenReturn(50.0);

        // Act
        ActivityRecord record = persistentData.getActivityRecord(testUuid);

        // Assert
        assertNotNull(record);
        assertEquals(50.0, record.getTotalHoursSpent(), 0.001);
        verify(persistentData, times(1)).getActivityRecord(testUuid);
    }

    @Test
    public void testPlayerNotFound() {
        // Arrange
        UUID testUuid = UUID.fromString("b2c3d4e5-f6a7-8901-bcde-f12345678901");
        when(persistentData.getActivityRecord(testUuid)).thenReturn(null);

        // Act
        ActivityRecord record = persistentData.getActivityRecord(testUuid);

        // Assert
        assertNull(record);
        verify(persistentData, times(1)).getActivityRecord(testUuid);
    }

    @Test
    public void testAllPlayersRetrieval() {
        // Arrange
        ArrayList<ActivityRecord> records = new ArrayList<>();
        ActivityRecord record1 = mock(ActivityRecord.class);
        ActivityRecord record2 = mock(ActivityRecord.class);
        when(record1.getPlayerUUID()).thenReturn(UUID.fromString("a1b2c3d4-e5f6-7890-abcd-ef1234567890"));
        when(record2.getPlayerUUID()).thenReturn(UUID.fromString("b2c3d4e5-f6a7-8901-bcde-f12345678901"));
        records.add(record1);
        records.add(record2);
        when(persistentData.getActivityRecords()).thenReturn(records);

        // Act
        ArrayList<ActivityRecord> allRecords = persistentData.getActivityRecords();

        // Assert
        assertNotNull(allRecords);
        assertEquals(2, allRecords.size());
        verify(persistentData, times(1)).getActivityRecords();
    }

    @Test
    public void testJsonArraySerialization() {
        // Arrange
        ArrayList<LeaderboardEntry> leaderboard = new ArrayList<>();
        leaderboard.add(new LeaderboardEntry("uuid1", "Player1", 100.0, 50));
        leaderboard.add(new LeaderboardEntry("uuid2", "Player2", 80.0, 40));

        // Act
        String json = gson.toJson(leaderboard);

        // Assert
        assertNotNull(json);
        assertTrue(json.startsWith("["));
        assertTrue(json.endsWith("]"));
        assertTrue(json.contains("Player1"));
        assertTrue(json.contains("Player2"));
    }

    @Test
    public void testAverageDailyActivityResponseSerialization() {
        // Arrange
        String playerUuid = "a1b2c3d4-e5f6-7890-abcd-ef1234567890";
        String playerName = "Steve";
        int days = 7;
        double averageHoursPerDay = 3.25;
        double totalHours = 22.75;
        AverageDailyActivityResponse response = new AverageDailyActivityResponse(
            playerUuid, playerName, days, averageHoursPerDay, totalHours
        );

        // Act
        String json = gson.toJson(response);
        AverageDailyActivityResponse deserialized = gson.fromJson(json, AverageDailyActivityResponse.class);

        // Assert
        assertNotNull(json);
        assertTrue(json.contains("\"playerUuid\":\"" + playerUuid + "\""));
        assertTrue(json.contains("\"playerName\":\"" + playerName + "\""));
        assertTrue(json.contains("\"days\":7"));
        assertTrue(json.contains("\"averageHoursPerDay\":3.25"));
        assertTrue(json.contains("\"totalHours\":22.75"));
        assertEquals(playerUuid, deserialized.getPlayerUuid());
        assertEquals(playerName, deserialized.getPlayerName());
        assertEquals(days, deserialized.getDays());
        assertEquals(averageHoursPerDay, deserialized.getAverageHoursPerDay(), 0.001);
        assertEquals(totalHours, deserialized.getTotalHours(), 0.001);
    }

    @Test
    public void testAverageDailyActivityDataRetrieval() {
        // Arrange
        UUID testUuid = UUID.fromString("a1b2c3d4-e5f6-7890-abcd-ef1234567890");
        int days = 7;
        double expectedAverage = 3.5;
        double expectedTotal = 24.5;
        
        when(persistentData.getActivityRecord(testUuid)).thenReturn(mockRecord);
        when(activityRecordService.calculateAverageDailyActivity(mockRecord, days)).thenReturn(expectedAverage);
        when(activityRecordService.calculateTotalHoursInPeriod(mockRecord, days)).thenReturn(expectedTotal);

        // Act
        ActivityRecord record = persistentData.getActivityRecord(testUuid);
        double average = activityRecordService.calculateAverageDailyActivity(record, days);
        double total = activityRecordService.calculateTotalHoursInPeriod(record, days);

        // Assert
        assertNotNull(record);
        assertEquals(expectedAverage, average, 0.001);
        assertEquals(expectedTotal, total, 0.001);
        verify(persistentData, times(1)).getActivityRecord(testUuid);
        verify(activityRecordService, times(1)).calculateAverageDailyActivity(mockRecord, days);
        verify(activityRecordService, times(1)).calculateTotalHoursInPeriod(mockRecord, days);
    }

    @Test
    public void testAverageDailyActivityWithInvalidDays() {
        // Arrange
        UUID testUuid = UUID.fromString("a1b2c3d4-e5f6-7890-abcd-ef1234567890");
        when(persistentData.getActivityRecord(testUuid)).thenReturn(mockRecord);
        when(activityRecordService.calculateAverageDailyActivity(mockRecord, 0)).thenReturn(0.0);
        when(activityRecordService.calculateAverageDailyActivity(mockRecord, -1)).thenReturn(0.0);

        // Act
        double averageZero = activityRecordService.calculateAverageDailyActivity(mockRecord, 0);
        double averageNegative = activityRecordService.calculateAverageDailyActivity(mockRecord, -1);

        // Assert
        assertEquals(0.0, averageZero, 0.001);
        assertEquals(0.0, averageNegative, 0.001);
    }

    @Test
    public void testAverageDailyActivityPlayerNotFound() {
        // Arrange
        UUID testUuid = UUID.fromString("b2c3d4e5-f6a7-8901-bcde-f12345678901");
        when(persistentData.getActivityRecord(testUuid)).thenReturn(null);

        // Act
        ActivityRecord record = persistentData.getActivityRecord(testUuid);

        // Assert
        assertNull(record);
        verify(persistentData, times(1)).getActivityRecord(testUuid);
    }

    @Test
    public void testAverageDailyActivityWithDifferentPeriods() {
        // Arrange
        UUID testUuid = UUID.fromString("a1b2c3d4-e5f6-7890-abcd-ef1234567890");
        when(persistentData.getActivityRecord(testUuid)).thenReturn(mockRecord);
        when(activityRecordService.calculateAverageDailyActivity(mockRecord, 7)).thenReturn(3.5);
        when(activityRecordService.calculateAverageDailyActivity(mockRecord, 30)).thenReturn(2.0);
        when(activityRecordService.calculateTotalHoursInPeriod(mockRecord, 7)).thenReturn(24.5);
        when(activityRecordService.calculateTotalHoursInPeriod(mockRecord, 30)).thenReturn(60.0);

        // Act
        double average7 = activityRecordService.calculateAverageDailyActivity(mockRecord, 7);
        double average30 = activityRecordService.calculateAverageDailyActivity(mockRecord, 30);
        double total7 = activityRecordService.calculateTotalHoursInPeriod(mockRecord, 7);
        double total30 = activityRecordService.calculateTotalHoursInPeriod(mockRecord, 30);

        // Assert
        assertEquals(3.5, average7, 0.001);
        assertEquals(2.0, average30, 0.001);
        assertEquals(24.5, total7, 0.001);
        assertEquals(60.0, total30, 0.001);
    }
}
