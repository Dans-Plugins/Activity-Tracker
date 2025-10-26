package dansplugins.activitytracker.api;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for LeaderboardEntry
 * @author Daniel McCoy Stephenson
 */
public class LeaderboardEntryTest {

    @Test
    public void testConstructorAndGetters() {
        // Arrange
        String playerUuid = "a1b2c3d4-e5f6-7890-abcd-ef1234567890";
        String playerName = "Steve";
        double hoursPlayed = 123.45;
        int totalLogins = 87;

        // Act
        LeaderboardEntry entry = new LeaderboardEntry(playerUuid, playerName, hoursPlayed, totalLogins);

        // Assert
        assertEquals(playerUuid, entry.getPlayerUuid());
        assertEquals(playerName, entry.getPlayerName());
        assertEquals(hoursPlayed, entry.getHoursPlayed(), 0.001);
        assertEquals(totalLogins, entry.getTotalLogins());
    }

    @Test
    public void testSetters() {
        // Arrange
        LeaderboardEntry entry = new LeaderboardEntry("uuid1", "Player1", 0.0, 0);

        // Act
        entry.setPlayerUuid("uuid2");
        entry.setPlayerName("Player2");
        entry.setHoursPlayed(50.5);
        entry.setTotalLogins(25);

        // Assert
        assertEquals("uuid2", entry.getPlayerUuid());
        assertEquals("Player2", entry.getPlayerName());
        assertEquals(50.5, entry.getHoursPlayed(), 0.001);
        assertEquals(25, entry.getTotalLogins());
    }

    @Test
    public void testZeroHoursAndLogins() {
        // Arrange & Act
        LeaderboardEntry entry = new LeaderboardEntry("uuid", "NewPlayer", 0.0, 0);

        // Assert
        assertEquals(0.0, entry.getHoursPlayed(), 0.001);
        assertEquals(0, entry.getTotalLogins());
    }

    @Test
    public void testLargeValues() {
        // Arrange
        double largeHours = 999999.99;
        int largeLogins = Integer.MAX_VALUE;

        // Act
        LeaderboardEntry entry = new LeaderboardEntry("uuid", "VeteranPlayer", largeHours, largeLogins);

        // Assert
        assertEquals(largeHours, entry.getHoursPlayed(), 0.001);
        assertEquals(largeLogins, entry.getTotalLogins());
    }

    @Test
    public void testNullValues() {
        // Arrange & Act
        LeaderboardEntry entry = new LeaderboardEntry(null, null, 10.5, 5);

        // Assert
        assertNull(entry.getPlayerUuid());
        assertNull(entry.getPlayerName());
        assertEquals(10.5, entry.getHoursPlayed(), 0.001);
        assertEquals(5, entry.getTotalLogins());
    }
}
