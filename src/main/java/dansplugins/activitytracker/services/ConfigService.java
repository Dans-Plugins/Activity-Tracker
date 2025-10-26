package dansplugins.activitytracker.services;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import dansplugins.activitytracker.ActivityTracker;

/*
    To add a new config option, the following methods must be altered:
    - saveMissingConfigDefaultsIfNotPresent
    - setConfigOption()
    - sendConfigList()
 */

/**
 * @author Daniel McCoy Stephenson
 */
public class ConfigService {
    private final ActivityTracker activityTracker;

    private boolean altered = false;

    public ConfigService(ActivityTracker activityTracker) {
        this.activityTracker = activityTracker;
    }

    public void saveMissingConfigDefaultsIfNotPresent() {
        // set version
        if (!getConfig().isString("version")) {
            getConfig().addDefault("version", activityTracker.getVersion());
        }
        else {
            getConfig().set("version", activityTracker.getVersion());
        }

        // save config options
        if (!getConfig().isSet("debugMode")) {
            getConfig().set("debugMode", false);
        }
        if (!getConfig().isSet("restApiEnabled")) {
            getConfig().set("restApiEnabled", false);
        }
        if (!getConfig().isSet("restApiPort")) {
            getConfig().set("restApiPort", 8080);
        }
        getConfig().options().copyDefaults(true);
        activityTracker.saveConfig();
    }

    public void setConfigOption(String option, String value, CommandSender sender) {

        if (getConfig().isSet(option)) {

            if (option.equalsIgnoreCase("version")) {
                sender.sendMessage(ChatColor.RED + "Cannot set version.");
                return;
            } else if (option.equalsIgnoreCase("restApiPort")) {
                getConfig().set(option, Integer.parseInt(value));
                sender.sendMessage(ChatColor.GREEN + "Integer set.");
            } else if (option.equalsIgnoreCase("debugMode") || option.equalsIgnoreCase("restApiEnabled")) {
                getConfig().set(option, Boolean.parseBoolean(value));
                sender.sendMessage(ChatColor.GREEN + "Boolean set.");
            } else if (option.equalsIgnoreCase("")) { // no doubles yet
                getConfig().set(option, Double.parseDouble(value));
                sender.sendMessage(ChatColor.GREEN + "Double set.");
            } else {
                getConfig().set(option, value);
                sender.sendMessage(ChatColor.GREEN + "String set.");
            }

            // save
            activityTracker.saveConfig();
            altered = true;
        } else {
            sender.sendMessage(ChatColor.RED + "That config option wasn't found.");
        }
    }

    public void sendConfigList(CommandSender sender) {
        sender.sendMessage(ChatColor.AQUA + "=== Config List ===");
        sender.sendMessage(ChatColor.AQUA + "version: " + getConfig().getString("version")
                + ", debugMode: " + getString("debugMode")
                + ", restApiEnabled: " + getString("restApiEnabled")
                + ", restApiPort: " + getString("restApiPort"));
    }

    public boolean hasBeenAltered() {
        return altered;
    }

    public FileConfiguration getConfig() {
        return activityTracker.getConfig();
    }

    public int getInt(String option) {
        return getConfig().getInt(option);
    }

    public boolean getBoolean(String option) {
        return getConfig().getBoolean(option);
    }

    public double getDouble(String option) {
        return getConfig().getDouble(option);
    }

    public String getString(String option) {
        return getConfig().getString(option);
    }
}