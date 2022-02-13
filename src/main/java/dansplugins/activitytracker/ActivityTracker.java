package dansplugins.activitytracker;

import dansplugins.activitytracker.bstats.Metrics;
import dansplugins.activitytracker.commands.DefaultCommand;
import dansplugins.activitytracker.data.PersistentData;
import dansplugins.activitytracker.services.LocalConfigService;
import dansplugins.activitytracker.services.LocalStorageService;
import dansplugins.activitytracker.utils.EventHandlerRegistry;
import dansplugins.activitytracker.utils.Scheduler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import preponderous.ponder.minecraft.bukkit.PonderMC;
import preponderous.ponder.minecraft.bukkit.abs.PonderBukkitPlugin;
import preponderous.ponder.minecraft.bukkit.services.CommandService;

import java.io.File;

public final class ActivityTracker extends PonderBukkitPlugin {
    private static ActivityTracker instance;
    private final String version = "v1.0";
    private final CommandService commandService = new CommandService((PonderMC) getPonder());

    public static ActivityTracker getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;

        // bStats
        int pluginId = 12983;
        Metrics metrics = new Metrics(this, pluginId);

        // create/load config
        if (!(new File("./plugins/ActivityTracker/config.yml").exists())) {
            LocalConfigService.getInstance().saveMissingConfigDefaultsIfNotPresent();
        }
        else {
            // pre load compatibility checks
            if (isVersionMismatched()) {
                LocalConfigService.getInstance().saveMissingConfigDefaultsIfNotPresent();
            }
            reloadConfig();
        }

        EventHandlerRegistry.getInstance().registerEvents();

        LocalStorageService.getInstance().load();

        Scheduler.getInstance().scheduleAutosave();
    }

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

    public String getVersion() {
        return version;
    }

    public boolean isDebugEnabled() {
        return LocalConfigService.getInstance().getBoolean("debugMode");
    }

    private boolean isVersionMismatched() {
        String version = getConfig().getString("version");
        return version == null || !version.equalsIgnoreCase(getVersion());
    }

}
