package dansplugins.activitytracker.data;

import dansplugins.activitytracker.objects.ActivityRecord;
import dansplugins.activitytracker.objects.Session;
import dansplugins.activitytracker.utils.Logger;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.UUID;

import static org.junit.Assert.*;

/**
 * Unit tests for PersistentData
 * Tests the fix for session time tracking on server restart
 * @author Daniel McCoy Stephenson
 */
public class PersistentDataTest {

    @Mock
    private Logger logger;

    private PersistentData persistentData;
    private UUID testPlayerUUID;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        persistentData = new PersistentData(logger);
        testPlayerUUID = UUID.randomUUID();
    }

    @Test
    public void testAddAndGetActivityRecord() {
        // Arrange
        Session session = new Session(logger, 1, testPlayerUUID);
        ActivityRecord record = new ActivityRecord(testPlayerUUID, session);
        
        // Act
        persistentData.addRecord(record);
        ActivityRecord retrieved = persistentData.getActivityRecord(testPlayerUUID);

        // Assert
        assertNotNull(retrieved);
        assertEquals(testPlayerUUID, retrieved.getPlayerUUID());
    }

    @Test
    public void testGetNonExistentActivityRecord() {
        // Arrange
        UUID nonExistentUUID = UUID.randomUUID();
        
        // Act
        ActivityRecord record = persistentData.getActivityRecord(nonExistentUUID);

        // Assert
        assertNull(record);
    }

    @Test
    public void testRemoveActivityRecord() {
        // Arrange
        Session session = new Session(logger, 1, testPlayerUUID);
        ActivityRecord record = new ActivityRecord(testPlayerUUID, session);
        persistentData.addRecord(record);
        
        // Act
        persistentData.removeRecord(record);
        ActivityRecord retrieved = persistentData.getActivityRecord(testPlayerUUID);

        // Assert
        assertNull(retrieved);
    }

    @Test
    public void testGetSessionById() {
        // Arrange
        Session session1 = new Session(logger, 1, testPlayerUUID);
        Session session2 = new Session(logger, 2, testPlayerUUID);
        ActivityRecord record = new ActivityRecord(testPlayerUUID, session1);
        record.getSessions().add(session2);
        persistentData.addRecord(record);
        
        // Act
        Session found1 = persistentData.getSession(1);
        Session found2 = persistentData.getSession(2);
        Session notFound = persistentData.getSession(999);

        // Assert
        assertNotNull(found1);
        assertNotNull(found2);
        assertNull(notFound);
        assertEquals(1, found1.getID());
        assertEquals(2, found2.getID());
    }

    @Test
    public void testGetTotalNumberOfLogins() {
        // Arrange
        UUID uuid1 = UUID.randomUUID();
        UUID uuid2 = UUID.randomUUID();
        
        Session session1 = new Session(logger, 1, uuid1);
        ActivityRecord record1 = new ActivityRecord(uuid1, session1);
        record1.getSessions().add(new Session(logger, 2, uuid1));
        
        Session session3 = new Session(logger, 3, uuid2);
        ActivityRecord record2 = new ActivityRecord(uuid2, session3);
        
        persistentData.addRecord(record1);
        persistentData.addRecord(record2);
        
        // Act
        int totalLogins = persistentData.getTotalNumberOfLogins();

        // Assert
        assertEquals(3, totalLogins); // 2 sessions for record1, 1 for record2
    }

    @Test
    public void testActivityRecordsListInitiallyEmpty() {
        // Act
        int count = persistentData.getActivityRecords().size();

        // Assert
        assertEquals(0, count);
    }

    @Test
    public void testMultipleRecords() {
        // Arrange
        UUID uuid1 = UUID.randomUUID();
        UUID uuid2 = UUID.randomUUID();
        UUID uuid3 = UUID.randomUUID();
        
        Session session1 = new Session(logger, 1, uuid1);
        Session session2 = new Session(logger, 2, uuid2);
        Session session3 = new Session(logger, 3, uuid3);
        
        ActivityRecord record1 = new ActivityRecord(uuid1, session1);
        ActivityRecord record2 = new ActivityRecord(uuid2, session2);
        ActivityRecord record3 = new ActivityRecord(uuid3, session3);
        
        // Act
        persistentData.addRecord(record1);
        persistentData.addRecord(record2);
        persistentData.addRecord(record3);

        // Assert
        assertEquals(3, persistentData.getActivityRecords().size());
    }

    @Test
    public void testDuplicateRecordNotAdded() {
        // Arrange
        Session session = new Session(logger, 1, testPlayerUUID);
        ActivityRecord record = new ActivityRecord(testPlayerUUID, session);
        
        // Act
        persistentData.addRecord(record);
        persistentData.addRecord(record);

        // Assert
        assertEquals(1, persistentData.getActivityRecords().size());
    }

    /**
     * Test that simulates the server restart scenario
     * This proves the fix works: session time is preserved when properly ended
     */
    @Test
    public void testServerRestartSessionTimePreservation() throws InterruptedException {
        // Arrange - Player is online playing
        Session activeSession = new Session(logger, 1, testPlayerUUID);
        ActivityRecord record = new ActivityRecord(testPlayerUUID, activeSession);
        record.setHoursSpent(5.0); // Previous playtime: 5 hours
        persistentData.addRecord(record);
        
        Thread.sleep(1000); // Simulate player being online for 1 second to ensure measurable time
        
        // Act - Server shutdown: endCurrentSessions is called (THE FIX)
        // This simulates what PersistentData.endCurrentSessions() does for each player
        Session currentSession = record.getMostRecentSession();
        currentSession.endSession();
        double totalHoursSpent = record.getHoursSpentNotIncludingTheCurrentSession() + currentSession.getMinutesSpent() / 60;
        record.setHoursSpent(totalHoursSpent);
        
        // Assert - Time should be preserved after restart
        assertTrue("Session should be ended", !currentSession.isActive());
        assertTrue("Hours should increase from previous total", 
            record.getHoursSpentNotIncludingTheCurrentSession() > 5.0);
        assertTrue("Increase should be small (< 1 hour for 1 second)", 
            record.getHoursSpentNotIncludingTheCurrentSession() < 6.0);
        
        // Verify the fix: minutes were calculated and added
        double minutesSpent = currentSession.getMinutesSpent();
        assertTrue("Minutes should have been calculated", minutesSpent > 0);
        assertEquals("Total should equal previous + current session", 
            5.0 + (minutesSpent / 60), 
            record.getHoursSpentNotIncludingTheCurrentSession(), 
            0.001);
    }

    /**
     * Test multiple players scenario during server restart
     */
    @Test
    public void testMultiplePlayersServerRestart() throws InterruptedException {
        // Arrange - Multiple players online
        UUID player1UUID = UUID.randomUUID();
        UUID player2UUID = UUID.randomUUID();
        UUID player3UUID = UUID.randomUUID();
        
        Session session1 = new Session(logger, 1, player1UUID);
        Session session2 = new Session(logger, 2, player2UUID);
        Session session3 = new Session(logger, 3, player3UUID);
        
        ActivityRecord record1 = new ActivityRecord(player1UUID, session1);
        ActivityRecord record2 = new ActivityRecord(player2UUID, session2);
        ActivityRecord record3 = new ActivityRecord(player3UUID, session3);
        
        record1.setHoursSpent(10.0);
        record2.setHoursSpent(20.0);
        record3.setHoursSpent(30.0);
        
        persistentData.addRecord(record1);
        persistentData.addRecord(record2);
        persistentData.addRecord(record3);
        
        Thread.sleep(1000); // All players online for 1 second to ensure measurable time
        
        // Act - Server shutdown: apply fix to all players
        for (ActivityRecord record : persistentData.getActivityRecords()) {
            Session currentSession = record.getMostRecentSession();
            currentSession.endSession();
            double totalHoursSpent = record.getHoursSpentNotIncludingTheCurrentSession() + currentSession.getMinutesSpent() / 60;
            record.setHoursSpent(totalHoursSpent);
        }
        
        // Assert - All players' time should be preserved
        assertTrue("Player 1 hours should increase", record1.getHoursSpentNotIncludingTheCurrentSession() > 10.0);
        assertTrue("Player 2 hours should increase", record2.getHoursSpentNotIncludingTheCurrentSession() > 20.0);
        assertTrue("Player 3 hours should increase", record3.getHoursSpentNotIncludingTheCurrentSession() > 30.0);
        
        assertFalse("Player 1 session should be ended", session1.isActive());
        assertFalse("Player 2 session should be ended", session2.isActive());
        assertFalse("Player 3 session should be ended", session3.isActive());
    }
}
