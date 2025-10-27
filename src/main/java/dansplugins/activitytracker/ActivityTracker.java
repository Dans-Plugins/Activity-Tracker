package dansplugins.activitytracker;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import dansplugins.activitytracker.data.PersistentData;
import dansplugins.activitytracker.factories.ActivityRecordFactory;
import dansplugins.activitytracker.factories.SessionFactory;
import dansplugins.activitytracker.services.ActivityRecordService;
import dansplugins.activitytracker.services.ConfigService;
import dansplugins.activitytracker.services.StorageService;
import dansplugins.activitytracker.api.RestApiService;
import dansplugins.activitytracker.utils.Logger;
import dansplugins.activitytracker.utils.Scheduler;
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
import dansplugins.activitytracker.eventhandlers.JoinHandler;
import dansplugins.activitytracker.eventhandlers.QuitHandler;
import preponderous.ponder.minecraft.bukkit.abs.AbstractPluginCommand;
import preponderous.ponder.minecraft.bukkit.abs.PonderBukkitPlugin;
import preponderous.ponder.minecraft.bukkit.services.CommandService;
import preponderous.ponder.minecraft.bukkit.tools.EventHandlerRegistry;

/**
 * @author Daniel McCoy Stephenson
 */
public final class ActivityTracker extends PonderBukkitPlugin {
    private final String pluginVersion = "v" + getDescription().getVersion();

    private final CommandService commandService = new CommandService(getPonder());
    private final Logger logger = new Logger(this);
    private final ConfigService configService = new ConfigService(this);
    private final PersistentData persistentData = new PersistentData(logger);
    private final StorageService storageService = new StorageService(configService, this, persistentData, logger);
    private final Scheduler scheduler = new Scheduler(logger, this, storageService);
    private final SessionFactory sessionFactory = new SessionFactory(logger, persistentData);
    private final ActivityRecordFactory activityRecordFactory = new ActivityRecordFactory(logger, sessionFactory);
    private final ActivityRecordService activityRecordService = new ActivityRecordService(persistentData, activityRecordFactory, logger);
    private RestApiService restApiService;

    /**
     * This runs when the server starts.
     */
    @Override
    public void onEnable() {
        initializeConfig();
        registerEventHandlers();
        initializeCommandService();
        storageService.load();
        scheduler.scheduleAutosave();
        handlebStatsIntegration();
        startRestApiIfEnabled();
    }

    /**
     * This runs when the server stops.
     */
    @Override
    public void onDisable() {
        stopRestApi();
        persistentData.endCurrentSessions();
        storageService.save();
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
            DefaultCommand defaultCommand = new DefaultCommand(this);
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
        return configService.getBoolean("debugMode");
    }

    private void initializeConfig() {
        if (configFileExists()) {
            performCompatibilityChecks();
        }
        else {
            configService.saveMissingConfigDefaultsIfNotPresent();
        }
    }

    private boolean configFileExists() {
        return new File("./plugins/" + getName() + "/config.yml").exists();
    }

    private void performCompatibilityChecks() {
        if (isVersionMismatched()) {
            configService.saveMissingConfigDefaultsIfNotPresent();
        }
    }

    /**
     * Registers the event handlers of the plugin using Ponder.
     */
    private void registerEventHandlers() {
        EventHandlerRegistry eventHandlerRegistry = new EventHandlerRegistry();
        ArrayList<Listener> listeners = new ArrayList<>(Arrays.asList(
                new JoinHandler(activityRecordService, persistentData, sessionFactory),
                new QuitHandler(persistentData, logger)
        ));
        eventHandlerRegistry.registerEventHandlers(listeners, this);
    }

    /**
     * Initializes Ponder's command service with the plugin's commands.
     */
    private void initializeCommandService() {
        ArrayList<AbstractPluginCommand> commands = new ArrayList<>(Arrays.asList(
                new ConfigCommand(configService),
                new HelpCommand(),
                new InfoCommand(persistentData, activityRecordService),
                new StatsCommand(persistentData),
                new TopCommand(activityRecordService)
        ));
        commandService.initialize(commands, "That command wasn't found.");
    }

    private void handlebStatsIntegration() {
        int pluginId = 12983;
        new Metrics(this, pluginId);
    }

    private void startRestApiIfEnabled() {
        if (configService.getBoolean("restApiEnabled")) {
            int port = configService.getInt("restApiPort");
            restApiService = new RestApiService(persistentData, activityRecordService, logger, port);
            restApiService.start();
        } else {
            logger.log("REST API is disabled in config.");
        }
    }

    private void stopRestApi() {
        if (restApiService != null) {
            restApiService.stop();
        }
    }

}
