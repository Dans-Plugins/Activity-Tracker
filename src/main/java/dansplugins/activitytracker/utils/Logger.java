package dansplugins.activitytracker.utils;

import dansplugins.activitytracker.ActivityTracker;

/**
 * @author Daniel McCoy Stephenson
 */
public class Logger {
    private final ActivityTracker activityTracker;

    public Logger(ActivityTracker activityTracker) {
        this.activityTracker = activityTracker;
    }

    public void log(String message) {
        if (activityTracker.isDebugEnabled()) {
            System.out.println("[Activity Tracker] " + message);
        }
    }
}