package dansplugins.activitytracker.utils;

import dansplugins.activitytracker.ActivityTracker;
import dansplugins.activitytracker.managers.StorageManager;
import org.bukkit.Bukkit;

public class Scheduler implements IScheduler {

    private static Scheduler instance;

    private Scheduler() {

    }

    public static Scheduler getInstance() {
        if (instance == null) {
            instance = new Scheduler();
        }
        return instance;
    }

    @Override
    public void scheduleAutosave() {
        Logger.getInstance().log("Scheduling hourly autosave.");
        int delay = 60 * 60; // 1 hour
        int secondsUntilRepeat = 60 * 60; // 1 hour
        Bukkit.getScheduler().scheduleSyncRepeatingTask(ActivityTracker.getInstance(), new Runnable() {
            @Override
            public void run() {
                Logger.getInstance().log("Saving. This will happen hourly.");
                StorageManager.getInstance().save();
            }
        }, delay * 20, secondsUntilRepeat * 20);
    }

}
