package dansplugins.activitytracker;

import dansplugins.activitytracker.managers.StorageManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class ActivityTracker extends JavaPlugin {

    private static ActivityTracker instance;

    public static ActivityTracker getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        EventRegistry.getInstance().registerEvents();
        StorageManager.getInstance().load();
    }

    @Override
    public void onDisable() {
        StorageManager.getInstance().save();
    }
}
