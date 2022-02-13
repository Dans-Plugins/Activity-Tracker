package dansplugins.activitytracker.utils;

import dansplugins.activitytracker.ActivityTracker;
import dansplugins.activitytracker.eventhandlers.JoinHandler;
import dansplugins.activitytracker.eventhandlers.QuitHandler;
import org.bukkit.plugin.PluginManager;

public class EventHandlerRegistry {
    private static EventHandlerRegistry instance;

    private EventHandlerRegistry() {

    }

    public static EventHandlerRegistry getInstance() {
        if (instance == null) {
            instance = new EventHandlerRegistry();
        }
        return instance;
    }

    public void registerEvents() {
        ActivityTracker mainInstance = ActivityTracker.getInstance();
        PluginManager manager = mainInstance.getServer().getPluginManager();

        manager.registerEvents(new JoinHandler(), mainInstance);
        manager.registerEvents(new QuitHandler(), mainInstance);
    }
}