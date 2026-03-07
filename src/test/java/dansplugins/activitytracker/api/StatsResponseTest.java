package dansplugins.activitytracker.api;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for StatsResponse
 * @author Daniel McCoy Stephenson
 */
public class StatsResponseTest {

    @Test
    public void testConstructorAndGetters() {
        // Arrange
        int uniqueLogins = 150;
        int totalLogins = 2543;

        // Act
        StatsResponse response = new StatsResponse(uniqueLogins, totalLogins);

        // Assert
        assertEquals(uniqueLogins, response.getUniqueLogins());
        assertEquals(totalLogins, response.getTotalLogins());
    }

    @Test
    public void testSetters() {
        // Arrange
        StatsResponse response = new StatsResponse(0, 0);

        // Act
        response.setUniqueLogins(100);
        response.setTotalLogins(500);

        // Assert
        assertEquals(100, response.getUniqueLogins());
        assertEquals(500, response.getTotalLogins());
    }

    @Test
    public void testZeroValues() {
        // Arrange & Act
        StatsResponse response = new StatsResponse(0, 0);

        // Assert
        assertEquals(0, response.getUniqueLogins());
        assertEquals(0, response.getTotalLogins());
    }

    @Test
    public void testLargeValues() {
        // Arrange
        int uniqueLogins = Integer.MAX_VALUE;
        int totalLogins = Integer.MAX_VALUE;

        // Act
        StatsResponse response = new StatsResponse(uniqueLogins, totalLogins);

        // Assert
        assertEquals(uniqueLogins, response.getUniqueLogins());
        assertEquals(totalLogins, response.getTotalLogins());
    }
}
