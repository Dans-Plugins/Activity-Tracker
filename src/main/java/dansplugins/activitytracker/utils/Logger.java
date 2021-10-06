package dansplugins.activitytracker.utils;

import dansplugins.activitytracker.ActivityTracker;

public class Logger implements ILogger {

    private static Logger instance;

    private Logger() {

    }

    public static Logger getInstance() {
        if (instance == null) {
            instance = new Logger();
        }
        return instance;
    }

    @Override
    public void log(String message) {
        if (ActivityTracker.getInstance().isDebugEnabled()) {
            System.out.println("[Activity Tracker] " + message);
        }
    }

}
