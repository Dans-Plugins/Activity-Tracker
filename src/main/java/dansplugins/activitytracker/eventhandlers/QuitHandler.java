package dansplugins.activitytracker.eventhandlers;

import dansplugins.activitytracker.exceptions.NoSessionException;
import dansplugins.activitytracker.services.DiscordWebhookService;
import dansplugins.activitytracker.utils.Logger;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import dansplugins.activitytracker.data.PersistentData;
import dansplugins.activitytracker.objects.ActivityRecord;
import dansplugins.activitytracker.objects.Session;

/**
 * @author Daniel McCoy Stephenson
 */
public class QuitHandler implements Listener {
    private final PersistentData persistentData;
    private final Logger logger;
    private final DiscordWebhookService discordWebhookService;
    private final JavaPlugin plugin;

    private static final String STAFF_PERMISSION = "at.staff";

    public QuitHandler(PersistentData persistentData, Logger logger, DiscordWebhookService discordWebhookService, JavaPlugin plugin) {
        this.persistentData = persistentData;
        this.logger = logger;
        this.discordWebhookService = discordWebhookService;
        this.plugin = plugin;
    }

    @EventHandler()
    public void handle(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        ActivityRecord record = persistentData.getActivityRecord(player);
        
        if (record == null) {
            logger.log("ERROR: No activity record found for " + player.getName() + " on logout.");
            return;
        }
        
        Session currentSession;
        try {
            currentSession = record.getMostRecentSession();
        }
        catch (NoSessionException e) {
            logger.log("ERROR: The most recent session was null for " + player.getName() + ": " + e.getMessage());
            return;
        }
        
        logger.log(player.getName() + " has quit the server. Ending their session.");
        
        try {
            if (currentSession.isActive()) {
                currentSession.endSession();
                double totalHoursSpent = record.getHoursSpentNotIncludingTheCurrentSession() + currentSession.getMinutesSpent() / 60;
                logger.log(player.getName() + " total hours spent on the server: " + totalHoursSpent);
                record.setHoursSpent(totalHoursSpent);
            } else {
                logger.log("WARNING: Session for " + player.getName() + " was already ended.");
            }
        } catch (Exception e) {
            logger.log("ERROR: Failed to properly end session for " + player.getName() + ": " + e.getMessage());
        }

        sendDiscordQuitNotification(player);
    }

    private void sendDiscordQuitNotification(Player player) {
        if (!discordWebhookService.isEnabled()) {
            return;
        }
        if (discordWebhookService.isStaffOnly() && !player.hasPermission(STAFF_PERMISSION)) {
            return;
        }
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                discordWebhookService.sendQuitNotification(player.getName());
            }
        });
    }
}