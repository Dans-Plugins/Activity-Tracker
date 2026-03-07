package dansplugins.activitytracker.api;

/**
 * Response object for activity statistics
 * @author Daniel McCoy Stephenson
 */
public class StatsResponse {
    private int uniqueLogins;
    private int totalLogins;

    public StatsResponse(int uniqueLogins, int totalLogins) {
        this.uniqueLogins = uniqueLogins;
        this.totalLogins = totalLogins;
    }

    public int getUniqueLogins() {
        return uniqueLogins;
    }

    public void setUniqueLogins(int uniqueLogins) {
        this.uniqueLogins = uniqueLogins;
    }

    public int getTotalLogins() {
        return totalLogins;
    }

    public void setTotalLogins(int totalLogins) {
        this.totalLogins = totalLogins;
    }
}
