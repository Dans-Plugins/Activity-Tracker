package dansplugins.activitytracker;

import dansplugins.activitytracker.data.PersistentData;
import dansplugins.activitytracker.managers.ConfigManager;
import dansplugins.activitytracker.managers.StorageManager;
import dansplugins.activitytracker.utils.EventHandlerRegistry;
import dansplugins.activitytracker.utils.Scheduler;
import dansplugins.dansapi.implementation.DansAPI_Integrator;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class ActivityTracker extends JavaPlugin {

    private static ActivityTracker instance;

    private String version = "v0.4-alpha-2";

    public static ActivityTracker getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;

        // create/load config
        if (!(new File("./plugins/ActivityTracker/config.yml").exists())) {
            ConfigManager.getInstance().saveMissingConfigDefaultsIfNotPresent();
        }
        else {
            // pre load compatibility checks
            if (isVersionMismatched()) {
                ConfigManager.getInstance().saveMissingConfigDefaultsIfNotPresent();
            }
            reloadConfig();
        }

        EventHandlerRegistry.getInstance().registerEvents();

        StorageManager.getInstance().load();

        Scheduler.getInstance().scheduleAutosave();

        DansAPI_Integrator DAPI_Integrator = new DansAPI_Integrator(this);
        DAPI_Integrator.getAPI().getToolbox().getLogger().log(isDebugEnabled(), "This debug message was printed through the use of Dan's API.");
    }

    @Override
    public void onDisable() {
        PersistentData.getInstance().endCurrentSessions();
        StorageManager.getInstance().save();
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        CommandInterpreter commandInterpreter = new CommandInterpreter();
        return commandInterpreter.interpretCommand(sender, label, args);
    }

    public String getVersion() {
        return version;
    }

    public boolean isDebugEnabled() {
        return ConfigManager.getInstance().getBoolean("debugMode");
    }

    private boolean isVersionMismatched() {
        String version = getConfig().getString("version");
        return version == null || !version.equalsIgnoreCase(getVersion());
    }

}
