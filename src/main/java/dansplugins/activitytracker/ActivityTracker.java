package dansplugins.activitytracker;

import dansplugins.activitytracker.data.PersistentData;
import dansplugins.activitytracker.managers.ConfigManager;
import dansplugins.activitytracker.managers.StorageManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class ActivityTracker extends JavaPlugin {

    private static ActivityTracker instance;

    private String version = "v0.3";

    public static ActivityTracker getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;

        // create/load config
        if (!(new File("./plugins/Mailboxes/config.yml").exists())) {
            ConfigManager.getInstance().saveMissingConfigDefaultsIfNotPresent();
        }
        else {
            // pre load compatibility checks
            if (isVersionMismatched()) {
                ConfigManager.getInstance().saveMissingConfigDefaultsIfNotPresent();
            }
            reloadConfig();
        }

        EventRegistry.getInstance().registerEvents();

        StorageManager.getInstance().load();
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
