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
}
