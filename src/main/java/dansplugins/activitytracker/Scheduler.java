package dansplugins.activitytracker;

import dansplugins.activitytracker.managers.StorageManager;
import dansplugins.activitytracker.utils.Logger;
import org.bukkit.Bukkit;

public class Scheduler {

    private static Scheduler instance;

    private Scheduler() {

    }

    public static Scheduler getInstance() {
        if (instance == null) {
            instance = new Scheduler();
        }
        return instance;
    }

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