package dansplugins.activitytracker.objects;

import dansplugins.activitytracker.utils.Logger;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.*;

/**
 * Unit tests for Session
 * Tests the fix for session time tracking on server restart
 * @author Daniel McCoy Stephenson
 */
public class SessionTest {

    @Mock
    private Logger logger;

    private UUID testPlayerUUID;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        testPlayerUUID = UUID.randomUUID();
    }

    @Test
    public void testSessionCreation() {
        // Arrange & Act
        Session session = new Session(logger, 1, testPlayerUUID);

        // Assert
        assertEquals(1, session.getID());
        assertEquals(testPlayerUUID, session.getPlayerUUID());
        assertTrue(session.isActive());
        assertNotNull(session.getLoginDate());
        assertNull(session.getLogoutDate());
        assertEquals(0.0, session.getMinutesSpent(), 0.001);
    }

    @Test
    public void testSessionIsActiveInitially() {
        // Arrange & Act
        Session session = new Session(logger, 1, testPlayerUUID);

        // Assert
        assertTrue("New session should be active", session.isActive());
    }

    @Test
    public void testEndSessionSetsInactive() {
        // Arrange
        Session session = new Session(logger, 1, testPlayerUUID);
        
        // Act
        boolean result = session.endSession();

        // Assert
        assertTrue("endSession should return true when ending active session", result);
        assertFalse("Session should be inactive after ending", session.isActive());
        assertNotNull("Logout date should be set", session.getLogoutDate());
    }

    @Test
    public void testEndSessionCalculatesMinutesSpent() throws InterruptedException {
        // Arrange
        Session session = new Session(logger, 1, testPlayerUUID);
        Thread.sleep(1000); // Sleep for 1 second to ensure measurable time passes
        
        // Act
        session.endSession();

        // Assert
        double minutesSpent = session.getMinutesSpent();
        assertTrue("Minutes spent should be greater than 0", minutesSpent > 0);
        assertTrue("Minutes spent should be small (< 1 minute)", minutesSpent < 1.0);
    }

    @Test
    public void testEndSessionCannotBeCalledTwice() {
        // Arrange
        Session session = new Session(logger, 1, testPlayerUUID);
        session.endSession();
        
        // Act
        boolean secondResult = session.endSession();

        // Assert
        assertFalse("endSession should return false when called on inactive session", secondResult);
    }

    @Test
    public void testGetMinutesSinceLogin() throws InterruptedException {
        // Arrange
        Session session = new Session(logger, 1, testPlayerUUID);
        Thread.sleep(1000); // Sleep for 1 second to ensure measurable time
        
        // Act
        double minutesSinceLogin = session.getMinutesSinceLogin();

        // Assert
        assertTrue("Minutes since login should be positive", minutesSinceLogin > 0);
        assertTrue("Minutes since login should be small", minutesSinceLogin < 1.0);
    }

    @Test
    public void testSessionSaveAndLoad() {
        // Arrange
        Session originalSession = new Session(logger, 123, testPlayerUUID);
        originalSession.endSession();
        
        // Act
        Map<String, String> savedData = originalSession.save();
        Session loadedSession = new Session(savedData, logger);

        // Assert
        assertEquals("ID should match", originalSession.getID(), loadedSession.getID());
        assertEquals("Player UUID should match", originalSession.getPlayerUUID(), loadedSession.getPlayerUUID());
        assertEquals("Minutes spent should match", originalSession.getMinutesSpent(), loadedSession.getMinutesSpent(), 0.001);
        assertEquals("Active status should match", originalSession.isActive(), loadedSession.isActive());
        assertFalse("Loaded session should be inactive", loadedSession.isActive());
    }

    @Test
    public void testSessionPreservesTimeAfterSave() {
        // Arrange
        Session session = new Session(logger, 456, testPlayerUUID);
        session.endSession();
        double originalMinutes = session.getMinutesSpent();
        
        // Act - simulate save/load cycle
        Map<String, String> savedData = session.save();
        Session reloadedSession = new Session(savedData, logger);

        // Assert
        assertEquals("Minutes spent should be preserved after save/load", 
            originalMinutes, reloadedSession.getMinutesSpent(), 0.001);
        assertFalse("Session should remain inactive after reload", reloadedSession.isActive());
    }

    @Test
    public void testEndSessionPreservesMinutesSpent() throws InterruptedException {
        // Arrange
        Session session = new Session(logger, 789, testPlayerUUID);
        Thread.sleep(100);
        
        // Act
        session.endSession();
        double firstCheck = session.getMinutesSpent();
        Thread.sleep(100);
        double secondCheck = session.getMinutesSpent();

        // Assert
        assertEquals("Minutes spent should not change after session ends", 
            firstCheck, secondCheck, 0.001);
    }

    @Test
    public void testActiveSessionSaveAndLoad() {
        // Arrange - create an active session (not ended)
        Session activeSession = new Session(logger, 999, testPlayerUUID);
        
        // Act
        Map<String, String> savedData = activeSession.save();
        Session loadedSession = new Session(savedData, logger);

        // Assert
        assertEquals("ID should match", activeSession.getID(), loadedSession.getID());
        assertEquals("Player UUID should match", activeSession.getPlayerUUID(), loadedSession.getPlayerUUID());
        assertTrue("Loaded session should be active", loadedSession.isActive());
        assertNull("Loaded session should have null logoutDate", loadedSession.getLogoutDate());
    }

    @Test
    public void testActiveSessionWithNullLogoutDateHandling() {
        // Arrange - create an active session
        Session session = new Session(logger, 888, testPlayerUUID);
        
        // Act - save and reload
        Map<String, String> savedData = session.save();
        
        // Assert - save should not throw exception even with null logoutDate
        assertNotNull("Saved data should not be null", savedData);
        assertTrue("Saved data should contain logoutDate key", savedData.containsKey("logoutDate"));
        
        // Load and verify
        Session reloadedSession = new Session(savedData, logger);
        assertNull("Reloaded session should have null logoutDate", reloadedSession.getLogoutDate());
        assertTrue("Reloaded session should be active", reloadedSession.isActive());
    }
}
