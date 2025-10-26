package dansplugins.activitytracker.api;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for PlayerActivityResponse
 * @author Daniel McCoy Stephenson
 */
public class PlayerActivityResponseTest {

    @Test
    public void testConstructorAndGettersOnlinePlayer() {
        // Arrange
        String playerUuid = "a1b2c3d4-e5f6-7890-abcd-ef1234567890";
        String playerName = "Steve";
        int totalLogins = 87;
        double totalHoursPlayed = 123.45;
        boolean currentlyOnline = true;
        String firstLogin = "2023-01-15T14:30:00";
        String lastLogin = "2023-10-26T10:15:00";
        String lastLogout = null;
        Double hoursSinceLogin = 2.5;
        Double hoursSinceLogout = null;

        // Act
        PlayerActivityResponse response = new PlayerActivityResponse(
            playerUuid, playerName, totalLogins, totalHoursPlayed, 
            currentlyOnline, firstLogin, lastLogin, lastLogout,
            hoursSinceLogin, hoursSinceLogout
        );

        // Assert
        assertEquals(playerUuid, response.getPlayerUuid());
        assertEquals(playerName, response.getPlayerName());
        assertEquals(totalLogins, response.getTotalLogins());
        assertEquals(totalHoursPlayed, response.getTotalHoursPlayed(), 0.001);
        assertTrue(response.isCurrentlyOnline());
        assertEquals(firstLogin, response.getFirstLogin());
        assertEquals(lastLogin, response.getLastLogin());
        assertNull(response.getLastLogout());
        assertEquals(hoursSinceLogin, response.getHoursSinceLogin());
        assertNull(response.getHoursSinceLogout());
    }

    @Test
    public void testConstructorAndGettersOfflinePlayer() {
        // Arrange
        String playerUuid = "b2c3d4e5-f6a7-8901-bcde-f12345678901";
        String playerName = "Alex";
        int totalLogins = 65;
        double totalHoursPlayed = 98.76;
        boolean currentlyOnline = false;
        String firstLogin = "2023-02-20T08:00:00";
        String lastLogin = "2023-10-25T18:30:00";
        String lastLogout = "2023-10-25T20:45:00";
        Double hoursSinceLogin = null;
        Double hoursSinceLogout = 48.3;

        // Act
        PlayerActivityResponse response = new PlayerActivityResponse(
            playerUuid, playerName, totalLogins, totalHoursPlayed,
            currentlyOnline, firstLogin, lastLogin, lastLogout,
            hoursSinceLogin, hoursSinceLogout
        );

        // Assert
        assertEquals(playerUuid, response.getPlayerUuid());
        assertEquals(playerName, response.getPlayerName());
        assertEquals(totalLogins, response.getTotalLogins());
        assertEquals(totalHoursPlayed, response.getTotalHoursPlayed(), 0.001);
        assertFalse(response.isCurrentlyOnline());
        assertEquals(firstLogin, response.getFirstLogin());
        assertEquals(lastLogin, response.getLastLogin());
        assertEquals(lastLogout, response.getLastLogout());
        assertNull(response.getHoursSinceLogin());
        assertEquals(hoursSinceLogout, response.getHoursSinceLogout());
    }

    @Test
    public void testSetters() {
        // Arrange
        PlayerActivityResponse response = new PlayerActivityResponse(
            "uuid1", "Player1", 0, 0.0, false, null, null, null, null, null
        );

        // Act
        response.setPlayerUuid("uuid2");
        response.setPlayerName("Player2");
        response.setTotalLogins(10);
        response.setTotalHoursPlayed(25.5);
        response.setCurrentlyOnline(true);
        response.setFirstLogin("2023-01-01T00:00:00");
        response.setLastLogin("2023-10-26T12:00:00");
        response.setLastLogout("2023-10-26T11:00:00");
        response.setHoursSinceLogin(1.5);
        response.setHoursSinceLogout(2.0);

        // Assert
        assertEquals("uuid2", response.getPlayerUuid());
        assertEquals("Player2", response.getPlayerName());
        assertEquals(10, response.getTotalLogins());
        assertEquals(25.5, response.getTotalHoursPlayed(), 0.001);
        assertTrue(response.isCurrentlyOnline());
        assertEquals("2023-01-01T00:00:00", response.getFirstLogin());
        assertEquals("2023-10-26T12:00:00", response.getLastLogin());
        assertEquals("2023-10-26T11:00:00", response.getLastLogout());
        assertEquals(Double.valueOf(1.5), response.getHoursSinceLogin());
        assertEquals(Double.valueOf(2.0), response.getHoursSinceLogout());
    }

    @Test
    public void testNewPlayer() {
        // Arrange & Act - new player with minimal data
        PlayerActivityResponse response = new PlayerActivityResponse(
            "new-uuid", "NewPlayer", 0, 0.0, false, null, null, null, null, null
        );

        // Assert
        assertEquals("new-uuid", response.getPlayerUuid());
        assertEquals("NewPlayer", response.getPlayerName());
        assertEquals(0, response.getTotalLogins());
        assertEquals(0.0, response.getTotalHoursPlayed(), 0.001);
        assertFalse(response.isCurrentlyOnline());
        assertNull(response.getFirstLogin());
        assertNull(response.getLastLogin());
        assertNull(response.getLastLogout());
        assertNull(response.getHoursSinceLogin());
        assertNull(response.getHoursSinceLogout());
    }

    @Test
    public void testVeteranPlayer() {
        // Arrange & Act - veteran player with large values
        PlayerActivityResponse response = new PlayerActivityResponse(
            "veteran-uuid", "VeteranPlayer", 5000, 10000.5, 
            true, "2020-01-01T00:00:00", "2023-10-26T10:00:00", null,
            200.5, null
        );

        // Assert
        assertEquals(5000, response.getTotalLogins());
        assertEquals(10000.5, response.getTotalHoursPlayed(), 0.001);
        assertTrue(response.isCurrentlyOnline());
        assertEquals(Double.valueOf(200.5), response.getHoursSinceLogin());
    }
}
