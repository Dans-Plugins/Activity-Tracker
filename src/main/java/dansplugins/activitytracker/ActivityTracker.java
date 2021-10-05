package dansplugins.activitytracker;

import dansplugins.activitytracker.managers.StorageManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public final class ActivityTracker extends JavaPlugin {

    private static ActivityTracker instance;

    private String version = "v0.2-alpha-1";

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

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        CommandInterpreter commandInterpreter = new CommandInterpreter();
        return commandInterpreter.interpretCommand(sender, label, args);
    }

    public String getVersion() {
        return version;
    }

    public boolean isDebugEnabled() {
        // TODO: implement
        return true;
        //return ConfigManager.getInstance().getBoolean("debugMode");
    }

    private boolean isVersionMismatched() {
        // TODO: implement
        return false;
        // return !getConfig().getString("version").equalsIgnoreCase(getVersion());
    }

}
