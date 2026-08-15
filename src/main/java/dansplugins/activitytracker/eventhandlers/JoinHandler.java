package dansplugins.activitytracker.eventhandlers;

import dansplugins.activitytracker.data.PersistentData;
import dansplugins.activitytracker.factories.SessionFactory;
import dansplugins.activitytracker.services.DiscordWebhookService;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import dansplugins.activitytracker.objects.ActivityRecord;
import dansplugins.activitytracker.objects.Session;
import dansplugins.activitytracker.services.ActivityRecordService;

/**
 * @author Daniel McCoy Stephenson
 */
public class JoinHandler implements Listener {
    private final ActivityRecordService activityRecordService;
    private final PersistentData persistentData;
    private final SessionFactory sessionFactory;
    private final DiscordWebhookService discordWebhookService;
    private final JavaPlugin plugin;

    private static final String STAFF_PERMISSION = "at.staff";

    public JoinHandler(ActivityRecordService activityRecordService, PersistentData persistentData, SessionFactory sessionFactory, DiscordWebhookService discordWebhookService, JavaPlugin plugin) {
        this.activityRecordService = activityRecordService;
        this.persistentData = persistentData;
        this.sessionFactory = sessionFactory;
        this.discordWebhookService = discordWebhookService;
        this.plugin = plugin;
    }

    @EventHandler()
    public void handle(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        boolean assignmentNeeded = activityRecordService.assignActivityRecordToPlayerIfNecessary(player);
        if (!assignmentNeeded) {
            ActivityRecord record = persistentData.getActivityRecord(player);
            if (record == null) {
                // This should not happen, but handle it gracefully
                activityRecordService.assignActivityRecordToPlayerIfNecessary(player);
                return;
            }
            
            Session newSession = sessionFactory.createSession(player);
            record.addSession(newSession);
            record.setMostRecentSession(newSession);
        }

        sendDiscordJoinNotification(player);
    }

    private void sendDiscordJoinNotification(Player player) {
        if (!discordWebhookService.isEnabled()) {
            return;
        }
        if (discordWebhookService.isStaffOnly() && !player.hasPermission(STAFF_PERMISSION)) {
            return;
        }
        final String webhookUrl = discordWebhookService.getWebhookUrl();
        final String message = discordWebhookService.prepareJoinMessage(player.getName());
        if (webhookUrl == null || message == null) {
            return;
        }
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                discordWebhookService.sendWebhookMessage(webhookUrl, message);
            }
        });
    }
}