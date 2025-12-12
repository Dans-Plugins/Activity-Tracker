package dansplugins.activitytracker.api;

/**
 * Response object for average daily activity data
 * @author Daniel McCoy Stephenson
 */
public class AverageDailyActivityResponse {
    private final String playerUuid;
    private final String playerName;
    private final int days;
    private final double averageHoursPerDay;
    private final double totalHours;

    public AverageDailyActivityResponse(String playerUuid, String playerName, int days, double averageHoursPerDay, double totalHours) {
        this.playerUuid = playerUuid;
        this.playerName = playerName;
        this.days = days;
        this.averageHoursPerDay = averageHoursPerDay;
        this.totalHours = totalHours;
    }

    public String getPlayerUuid() {
        return playerUuid;
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getDays() {
        return days;
    }

    public double getAverageHoursPerDay() {
        return averageHoursPerDay;
    }

    public double getTotalHours() {
        return totalHours;
    }
}
