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
        if (!getConfig().isSet("discordWebhookEnabled")) {
            getConfig().set("discordWebhookEnabled", false);
        }
        if (!getConfig().isSet("discordWebhookUrl")) {
            getConfig().set("discordWebhookUrl", "");
        }
        if (!getConfig().isSet("discordWebhookStaffOnly")) {
            getConfig().set("discordWebhookStaffOnly", false);
        }
        if (!getConfig().isSet("discordWebhookJoinMessage")) {
            getConfig().set("discordWebhookJoinMessage", "\u2694\uFE0F **{player}** has joined the server!");
        }
        if (!getConfig().isSet("discordWebhookQuitMessage")) {
            getConfig().set("discordWebhookQuitMessage", "\uD83D\uDC4B **{player}** has left the server.");
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
            } else if (option.equalsIgnoreCase("debugMode") || option.equalsIgnoreCase("restApiEnabled")
                        || option.equalsIgnoreCase("discordWebhookEnabled") || option.equalsIgnoreCase("discordWebhookStaffOnly")) {
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
        sender.sendMessage("");
        sender.sendMessage(ChatColor.GOLD + "┌─ " + ChatColor.YELLOW + "" + ChatColor.BOLD + "Activity Tracker" +
                          ChatColor.RESET + ChatColor.GOLD + " ─ Config");
        sender.sendMessage(ChatColor.GOLD + "│ " + ChatColor.GRAY + "version:                  " +
                          ChatColor.WHITE + getConfig().getString("version"));
        sender.sendMessage(ChatColor.GOLD + "│ " + ChatColor.GRAY + "debugMode:                " +
                          ChatColor.WHITE + getString("debugMode"));
        sender.sendMessage(ChatColor.GOLD + "│ " + ChatColor.GRAY + "restApiEnabled:           " +
                          ChatColor.WHITE + getString("restApiEnabled"));
        sender.sendMessage(ChatColor.GOLD + "│ " + ChatColor.GRAY + "restApiPort:              " +
                          ChatColor.WHITE + getString("restApiPort"));
        sender.sendMessage(ChatColor.GOLD + "│ " + ChatColor.GRAY + "discordWebhookEnabled:    " +
                          ChatColor.WHITE + getString("discordWebhookEnabled"));
        sender.sendMessage(ChatColor.GOLD + "│ " + ChatColor.GRAY + "discordWebhookUrl:        " +
                          ChatColor.WHITE + getString("discordWebhookUrl"));
        sender.sendMessage(ChatColor.GOLD + "│ " + ChatColor.GRAY + "discordWebhookStaffOnly:  " +
                          ChatColor.WHITE + getString("discordWebhookStaffOnly"));
        sender.sendMessage(ChatColor.GOLD + "│ " + ChatColor.GRAY + "discordWebhookJoinMessage:" +
                          ChatColor.WHITE + " " + getString("discordWebhookJoinMessage"));
        sender.sendMessage(ChatColor.GOLD + "│ " + ChatColor.GRAY + "discordWebhookQuitMessage:" +
                          ChatColor.WHITE + " " + getString("discordWebhookQuitMessage"));
        sender.sendMessage(ChatColor.GOLD + "└─────────────────────────");
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