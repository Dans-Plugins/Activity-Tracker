package dansplugins.activitytracker.api;

import java.util.List;

/**
 * Response object for player activity information
 * @author Daniel McCoy Stephenson
 */
public class PlayerActivityResponse {
    private String playerUuid;
    private String playerName;
    private int totalLogins;
    private double totalHoursPlayed;
    private boolean currentlyOnline;
    private String firstLogin;
    private String lastLogin;
    private String lastLogout;
    private Double hoursSinceLogin;
    private Double hoursSinceLogout;

    public PlayerActivityResponse(String playerUuid, String playerName, int totalLogins, 
                                   double totalHoursPlayed, boolean currentlyOnline, 
                                   String firstLogin, String lastLogin, String lastLogout,
                                   Double hoursSinceLogin, Double hoursSinceLogout) {
        this.playerUuid = playerUuid;
        this.playerName = playerName;
        this.totalLogins = totalLogins;
        this.totalHoursPlayed = totalHoursPlayed;
        this.currentlyOnline = currentlyOnline;
        this.firstLogin = firstLogin;
        this.lastLogin = lastLogin;
        this.lastLogout = lastLogout;
        this.hoursSinceLogin = hoursSinceLogin;
        this.hoursSinceLogout = hoursSinceLogout;
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

    public int getTotalLogins() {
        return totalLogins;
    }

    public void setTotalLogins(int totalLogins) {
        this.totalLogins = totalLogins;
    }

    public double getTotalHoursPlayed() {
        return totalHoursPlayed;
    }

    public void setTotalHoursPlayed(double totalHoursPlayed) {
        this.totalHoursPlayed = totalHoursPlayed;
    }

    public boolean isCurrentlyOnline() {
        return currentlyOnline;
    }

    public void setCurrentlyOnline(boolean currentlyOnline) {
        this.currentlyOnline = currentlyOnline;
    }

    public String getFirstLogin() {
        return firstLogin;
    }

    public void setFirstLogin(String firstLogin) {
        this.firstLogin = firstLogin;
    }

    public String getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(String lastLogin) {
        this.lastLogin = lastLogin;
    }

    public String getLastLogout() {
        return lastLogout;
    }

    public void setLastLogout(String lastLogout) {
        this.lastLogout = lastLogout;
    }

    public Double getHoursSinceLogin() {
        return hoursSinceLogin;
    }

    public void setHoursSinceLogin(Double hoursSinceLogin) {
        this.hoursSinceLogin = hoursSinceLogin;
    }

    public Double getHoursSinceLogout() {
        return hoursSinceLogout;
    }

    public void setHoursSinceLogout(Double hoursSinceLogout) {
        this.hoursSinceLogout = hoursSinceLogout;
    }
}
