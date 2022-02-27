package dansplugins.activitytracker;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;

import dansplugins.activitytracker.bstats.Metrics;
import dansplugins.activitytracker.commands.ConfigCommand;
import dansplugins.activitytracker.commands.DefaultCommand;
import dansplugins.activitytracker.commands.HelpCommand;
import dansplugins.activitytracker.commands.InfoCommand;
import dansplugins.activitytracker.commands.StatsCommand;
import dansplugins.activitytracker.commands.TopCommand;
import dansplugins.activitytracker.data.PersistentData;
import dansplugins.activitytracker.eventhandlers.JoinHandler;
import dansplugins.activitytracker.eventhandlers.QuitHandler;
import dansplugins.activitytracker.services.LocalConfigService;
import dansplugins.activitytracker.services.LocalStorageService;
import dansplugins.activitytracker.utils.Scheduler;
import preponderous.ponder.minecraft.bukkit.abs.AbstractPluginCommand;
import preponderous.ponder.minecraft.bukkit.abs.PonderBukkitPlugin;
import preponderous.ponder.minecraft.bukkit.services.CommandService;
import preponderous.ponder.minecraft.bukkit.tools.EventHandlerRegistry;

/**
 * @author Daniel McCoy Stephenson
 */
public final class ActivityTracker extends PonderBukkitPlugin {
    private static ActivityTracker instance;
    private final String pluginVersion = "v" + getDescription().getVersion();
    private final CommandService commandService = new CommandService(getPonder());

    /**
     * This can be used to get the instance of the main class that is managed by itself.
     * @return The managed instance of the main class.
     */
    public static ActivityTracker getInstance() {
        return instance;
    }

    /**
     * This runs when the server starts.
     */
    @Override
    public void onEnable() {
        instance = this;
        initializeConfig();
        registerEventHandlers();
        initializeCommandService();
        LocalStorageService.getInstance().load();
        Scheduler.getInstance().scheduleAutosave();
        handlebStatsIntegration();
    }

    /**
     * This runs when the server stops.
     */
    @Override
    public void onDisable() {
        PersistentData.getInstance().endCurrentSessions();
        LocalStorageService.getInstance().save();
    }

    /**
     * This method handles commands sent to the minecraft server and interprets them if the label matches one of the core commands.
     * @param sender The sender of the command.
     * @param cmd The command that was sent. This is unused.
     * @param label The core command that has been invoked.
     * @param args Arguments of the core command. Often sub-commands.
     * @return A boolean indicating whether the execution of the command was successful.
     */
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 0) {
            DefaultCommand defaultCommand = new DefaultCommand();
            return defaultCommand.execute(sender);
        }

        return commandService.interpretAndExecuteCommand(sender, label, args);
    }

    /**
     * This can be used to get the version of the plugin.
     * @return A string containing the version preceded by 'v'
     */
    public String getVersion() {
        return pluginVersion;
    }

    /**
     * Checks if the version is mismatched.
     * @return A boolean indicating if the version is mismatched.
     */
    public boolean isVersionMismatched() {
        String configVersion = this.getConfig().getString("version");
        if (configVersion == null || this.getVersion() == null) {
            return false;
        } else {
            return !configVersion.equalsIgnoreCase(this.getVersion());
        }
    }

    /**
     * Checks if debug is enabled.
     * @return Whether debug is enabled.
     */
    public boolean isDebugEnabled() {
        return LocalConfigService.getInstance().getBoolean("debugMode");
    }

    private void initializeConfig() {
        if (configFileExists()) {
            performCompatibilityChecks();
        }
        else {
            LocalConfigService.getInstance().saveMissingConfigDefaultsIfNotPresent();
        }
    }

    private boolean configFileExists() {
        return new File("./plugins/" + getName() + "/config.yml").exists();
    }

    private void performCompatibilityChecks() {
        if (isVersionMismatched()) {
            LocalConfigService.getInstance().saveMissingConfigDefaultsIfNotPresent();
        }
    }

    /**
     * Registers the event handlers of the plugin using Ponder.
     */
    private void registerEventHandlers() {
        EventHandlerRegistry eventHandlerRegistry = new EventHandlerRegistry();
        ArrayList<Listener> listeners = new ArrayList<>(Arrays.asList(
                new JoinHandler(),
                new QuitHandler()
        ));
        eventHandlerRegistry.registerEventHandlers(listeners, this);
    }

    /**
     * Initializes Ponder's command service with the plugin's commands.
     */
    private void initializeCommandService() {
        ArrayList<AbstractPluginCommand> commands = new ArrayList<>(Arrays.asList(
                new ConfigCommand(),
                new HelpCommand(),
                new InfoCommand(),
                new StatsCommand(),
                new TopCommand()
        ));
        commandService.initialize(commands, "That command wasn't found.");
    }

    private void handlebStatsIntegration() {
        int pluginId = 12983;
        new Metrics(this, pluginId);
    }

}
