package dansplugins.activitytracker.eventhandlers;

import java.util.Collection;

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
        if (startSession(player)) {
            sendDiscordJoinNotification(player);
        }
    }

    /**
     * Opens a session for each player already on the server. No join event fires
     * for them after a reload, and none of them has joined, so no notification is sent.
     * @param players The players who are online.
     */
    public void startSessions(Collection<? extends Player> players) {
        for (Player player : players) {
            startSession(player);
        }
    }

    /**
     * @return Whether the player's record now has a new session.
     */
    private boolean startSession(Player player) {
        boolean assignmentNeeded = activityRecordService.assignActivityRecordToPlayerIfNecessary(player);
        if (!assignmentNeeded) {
            ActivityRecord record = persistentData.getActivityRecord(player);
            if (record == null) {
                // This should not happen, but handle it gracefully
                activityRecordService.assignActivityRecordToPlayerIfNecessary(player);
                return false;
            }

            Session newSession = sessionFactory.createSession(player);
            record.addSession(newSession);
            record.setMostRecentSession(newSession);
        }
        return true;
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