package dansplugins.activitytracker.api;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests for AverageDailyActivityResponse
 * @author Daniel McCoy Stephenson
 */
public class AverageDailyActivityResponseTest {

    @Test
    public void testConstructorAndGetters() {
        String uuid = "a1b2c3d4-e5f6-7890-abcd-ef1234567890";
        String name = "TestPlayer";
        int days = 7;
        double avgHours = 3.5;
        double totalHours = 24.5;

        AverageDailyActivityResponse response = new AverageDailyActivityResponse(
                uuid, name, days, avgHours, totalHours
        );

        assertEquals(uuid, response.getPlayerUuid());
        assertEquals(name, response.getPlayerName());
        assertEquals(days, response.getDays());
        assertEquals(avgHours, response.getAverageHoursPerDay(), 0.001);
        assertEquals(totalHours, response.getTotalHours(), 0.001);
    }

    @Test
    public void testWithZeroValues() {
        AverageDailyActivityResponse response = new AverageDailyActivityResponse(
                "uuid", "name", 7, 0.0, 0.0
        );

        assertEquals(0.0, response.getAverageHoursPerDay(), 0.001);
        assertEquals(0.0, response.getTotalHours(), 0.001);
    }

    @Test
    public void testWithLargeDays() {
        AverageDailyActivityResponse response = new AverageDailyActivityResponse(
                "uuid", "name", 365, 5.5, 2007.5
        );

        assertEquals(365, response.getDays());
        assertEquals(5.5, response.getAverageHoursPerDay(), 0.001);
        assertEquals(2007.5, response.getTotalHours(), 0.001);
    }
}
