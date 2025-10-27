package dansplugins.activitytracker.objects;

import dansplugins.activitytracker.utils.Logger;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.UUID;

import static org.junit.Assert.*;

/**
 * Unit tests for ActivityRecord
 * Tests the fix for session time tracking on server restart
 * @author Daniel McCoy Stephenson
 */
public class ActivityRecordTest {

    @Mock
    private Logger logger;

    @Mock
    private Session mockSession;

    private UUID testPlayerUUID;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        testPlayerUUID = UUID.randomUUID();
    }

    @Test
    public void testActivityRecordCreation() {
        // Arrange
        Session session = new Session(logger, 1, testPlayerUUID);
        
        // Act
        ActivityRecord record = new ActivityRecord(testPlayerUUID, session);

        // Assert
        assertEquals(testPlayerUUID, record.getPlayerUUID());
        assertEquals(1, record.getSessions().size());
        assertEquals(0.0, record.getHoursSpentNotIncludingTheCurrentSession(), 0.001);
        assertNotNull(record.getMostRecentSession());
    }

    @Test
    public void testSetHoursSpent() {
        // Arrange
        Session session = new Session(logger, 1, testPlayerUUID);
        ActivityRecord record = new ActivityRecord(testPlayerUUID, session);
        
        // Act
        record.setHoursSpent(10.5);

        // Assert
        assertEquals(10.5, record.getHoursSpentNotIncludingTheCurrentSession(), 0.001);
    }

    @Test
    public void testGetTotalHoursSpentWithInactiveSession() throws InterruptedException {
        // Arrange
        Session session = new Session(logger, 1, testPlayerUUID);
        ActivityRecord record = new ActivityRecord(testPlayerUUID, session);
        Thread.sleep(100); // Let some time pass
        session.endSession();
        
        // Act - Simulate what happens on server restart fix
        double totalHoursSpent = record.getHoursSpentNotIncludingTheCurrentSession() + session.getMinutesSpent() / 60;
        record.setHoursSpent(totalHoursSpent);

        // Assert
        assertTrue("Total hours should be greater than 0", record.getHoursSpentNotIncludingTheCurrentSession() > 0);
        assertEquals("Total hours should equal getTotalHoursSpent", 
            record.getHoursSpentNotIncludingTheCurrentSession(), 
            record.getTotalHoursSpent(), 0.001);
    }

    @Test
    public void testGetTotalHoursSpentWithActiveSession() throws InterruptedException {
        // Arrange
        Session session = new Session(logger, 1, testPlayerUUID);
        ActivityRecord record = new ActivityRecord(testPlayerUUID, session);
        record.setHoursSpent(5.0); // Previous sessions
        Thread.sleep(100); // Current session time
        
        // Act
        double totalHours = record.getTotalHoursSpent();

        // Assert
        assertTrue("Total hours should include current session", totalHours > 5.0);
        assertTrue("Current session should add small amount", totalHours < 6.0);
    }

    @Test
    public void testMultipleSessionsAccumulation() throws InterruptedException {
        // Arrange
        Session session1 = new Session(logger, 1, testPlayerUUID);
        ActivityRecord record = new ActivityRecord(testPlayerUUID, session1);
        
        // Simulate first session
        Thread.sleep(50);
        session1.endSession();
        double totalAfterSession1 = record.getHoursSpentNotIncludingTheCurrentSession() + session1.getMinutesSpent() / 60;
        record.setHoursSpent(totalAfterSession1);
        
        // Simulate second session
        Session session2 = new Session(logger, 2, testPlayerUUID);
        record.getSessions().add(session2);
        record.setMostRecentSession(session2);
        Thread.sleep(50);
        session2.endSession();
        
        // Act - This is what the fix does on server restart
        double totalAfterSession2 = record.getHoursSpentNotIncludingTheCurrentSession() + session2.getMinutesSpent() / 60;
        record.setHoursSpent(totalAfterSession2);

        // Assert
        assertEquals(2, record.getSessions().size());
        assertTrue("Total should accumulate from both sessions", 
            record.getHoursSpentNotIncludingTheCurrentSession() > 0);
        assertTrue("Total should include both sessions", 
            record.getHoursSpentNotIncludingTheCurrentSession() >= totalAfterSession1);
    }

    @Test
    public void testServerRestartScenario() throws InterruptedException {
        // Arrange - Player joins server
        Session session1 = new Session(logger, 1, testPlayerUUID);
        ActivityRecord record = new ActivityRecord(testPlayerUUID, session1);
        Thread.sleep(100); // Player plays for some time
        
        // Act - Server restarts (this is the fix being tested)
        // BEFORE FIX: session would be ended but hours not updated
        // AFTER FIX: hours are updated before save
        session1.endSession();
        double totalHoursAfterRestart = record.getHoursSpentNotIncludingTheCurrentSession() + session1.getMinutesSpent() / 60;
        record.setHoursSpent(totalHoursAfterRestart);
        
        // Assert - Time should be preserved
        assertTrue("Hours should be tracked after restart", 
            record.getHoursSpentNotIncludingTheCurrentSession() > 0);
        assertFalse("Session should be inactive after restart", session1.isActive());
        
        // Simulate player rejoining after restart
        Session session2 = new Session(logger, 2, testPlayerUUID);
        record.getSessions().add(session2);
        record.setMostRecentSession(session2);
        
        // Assert - Previous hours should still be there
        double previousHours = record.getHoursSpentNotIncludingTheCurrentSession();
        assertTrue("Previous session hours should be preserved", previousHours > 0);
        assertEquals("Total should include previous hours when active", 
            previousHours, 
            record.getHoursSpentNotIncludingTheCurrentSession(), 
            0.001);
    }

    @Test
    public void testHoursNotLostOnProperShutdown() throws InterruptedException {
        // Arrange
        Session session = new Session(logger, 1, testPlayerUUID);
        ActivityRecord record = new ActivityRecord(testPlayerUUID, session);
        record.setHoursSpent(10.0); // Previous sessions: 10 hours
        Thread.sleep(100); // Current session: ~0.0017 hours
        
        // Act - Simulate proper shutdown (the fix)
        session.endSession();
        double updatedHours = record.getHoursSpentNotIncludingTheCurrentSession() + session.getMinutesSpent() / 60;
        record.setHoursSpent(updatedHours);
        
        // Assert
        assertTrue("Hours should be more than previous total", 
            record.getHoursSpentNotIncludingTheCurrentSession() > 10.0);
        assertTrue("Increase should be small", 
            record.getHoursSpentNotIncludingTheCurrentSession() < 10.1);
    }

    @Test
    public void testGetMostRecentSession() {
        // Arrange
        Session session1 = new Session(logger, 1, testPlayerUUID);
        ActivityRecord record = new ActivityRecord(testPlayerUUID, session1);
        Session session2 = new Session(logger, 2, testPlayerUUID);
        record.getSessions().add(session2);
        
        // Act
        record.setMostRecentSession(session2);

        // Assert
        assertEquals(session2.getID(), record.getMostRecentSession().getID());
    }

    @Test
    public void testGetSessionById() {
        // Arrange
        Session session1 = new Session(logger, 1, testPlayerUUID);
        Session session2 = new Session(logger, 2, testPlayerUUID);
        ActivityRecord record = new ActivityRecord(testPlayerUUID, session1);
        record.getSessions().add(session2);
        
        // Act
        Session found1 = record.getSession(1);
        Session found2 = record.getSession(2);
        Session notFound = record.getSession(999);

        // Assert
        assertNotNull(found1);
        assertNotNull(found2);
        assertNull(notFound);
        assertEquals(1, found1.getID());
        assertEquals(2, found2.getID());
    }

    @Test
    public void testZeroHoursInitially() {
        // Arrange
        Session session = new Session(logger, 1, testPlayerUUID);
        
        // Act
        ActivityRecord record = new ActivityRecord(testPlayerUUID, session);

        // Assert
        assertEquals("New record should start with 0 hours", 
            0.0, record.getHoursSpentNotIncludingTheCurrentSession(), 0.001);
    }
}
