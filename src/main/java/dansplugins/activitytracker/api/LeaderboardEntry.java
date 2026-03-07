package dansplugins.activitytracker.api;

/**
 * Response object for player leaderboard entry
 * @author Daniel McCoy Stephenson
 */
public class LeaderboardEntry {
    private String playerUuid;
    private String playerName;
    private double hoursPlayed;
    private int totalLogins;

    public LeaderboardEntry(String playerUuid, String playerName, double hoursPlayed, int totalLogins) {
        this.playerUuid = playerUuid;
        this.playerName = playerName;
        this.hoursPlayed = hoursPlayed;
        this.totalLogins = totalLogins;
    }

    public String getPlayerUuid() {
        return playerUuid;
    }

    public void setPlayerUuid(String playerUuid) {
        this.playerUuid = playerUuid;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public double getHoursPlayed() {
        return hoursPlayed;
    }

    public void setHoursPlayed(double hoursPlayed) {
        this.hoursPlayed = hoursPlayed;
    }

    public int getTotalLogins() {
        return totalLogins;
    }

    public void setTotalLogins(int totalLogins) {
        this.totalLogins = totalLogins;
    }
}
