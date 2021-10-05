package dansplugins.activitytracker;

import dansplugins.activitytracker.eventhandlers.JoinHandler;
import dansplugins.activitytracker.eventhandlers.QuitHandler;
import org.bukkit.plugin.PluginManager;

public class EventRegistry {

    private static EventRegistry instance;

    private EventRegistry() {

    }

    public static EventRegistry getInstance() {
        if (instance == null) {
            instance = new EventRegistry();
        }
        return instance;
    }

    public void registerEvents() {

        ActivityTracker mainInstance = ActivityTracker.getInstance();
        PluginManager manager = mainInstance.getServer().getPluginManager();

        // blocks and interaction
        manager.registerEvents(new JoinHandler(), mainInstance);
        manager.registerEvents(new QuitHandler(), mainInstance);
    }
    
}
