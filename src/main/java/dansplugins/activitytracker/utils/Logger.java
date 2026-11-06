package dansplugins.activitytracker.utils;

import dansplugins.activitytracker.ActivityTracker;

import java.util.function.BooleanSupplier;

/**
 * @author Daniel McCoy Stephenson
 */
public class Logger {
    private final BooleanSupplier debugEnabled;

    public Logger(ActivityTracker activityTracker) {
        this(activityTracker::isDebugEnabled);
    }

    public Logger(BooleanSupplier debugEnabled) {
        this.debugEnabled = debugEnabled;
    }

    public void log(String message) {
        if (debugEnabled.getAsBoolean()) {
            System.out.println("[Activity Tracker] " + message);
        }
    }

    /**
     * Prints a problem a server owner has to act on, whether or not debug mode is on.
     */
    public void error(String message) {
        System.out.println("[Activity Tracker] ERROR: " + message);
    }
}
