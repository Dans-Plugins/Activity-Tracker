package dansplugins.activitytracker.utils;

import dansplugins.activitytracker.ActivityTracker;
import dansplugins.activitytracker.services.StorageService;
import org.bukkit.Bukkit;

/**
 * @author Daniel McCoy Stephenson
 */
public class Scheduler {
    private final Logger logger;
    private final ActivityTracker activityTracker;
    private final StorageService storageService;

    public Scheduler(Logger logger, ActivityTracker activityTracker, StorageService storageService) {
        this.logger = logger;
        this.activityTracker = activityTracker;
        this.storageService = storageService;
    }

    public void scheduleAutosave() {
        logger.log("Scheduling hourly autosave.");
        int delay = 60 * 60; // 1 hour
        int secondsUntilRepeat = 60 * 60; // 1 hour
        Bukkit.getScheduler().scheduleSyncRepeatingTask(activityTracker, new Runnable() {
            @Override
            public void run() {
                logger.log("Saving. This will happen hourly.");
                storageService.save();
            }
        }, delay * 20, secondsUntilRepeat * 20);
    }
}