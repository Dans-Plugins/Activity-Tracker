package dansplugins.activitytracker.eventhandlers;

import dansplugins.activitytracker.data.PersistentData;
import dansplugins.activitytracker.factories.SessionFactory;
import dansplugins.activitytracker.objects.ActivityRecord;
import dansplugins.activitytracker.objects.Session;
import dansplugins.activitytracker.services.ActivityRecordService;
import dansplugins.activitytracker.services.DiscordWebhookService;
import dansplugins.activitytracker.utils.Logger;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for JoinHandler.
 * Covers session creation for returning players and the gates in front of the Discord join notification.
 * The asynchronous webhook dispatch itself is not exercised: JavaPlugin.getServer() is final and
 * cannot be stubbed with the default Mockito mock maker.
 *
 * @author Daniel McCoy Stephenson
 */
class JoinHandlerTest {

    private Logger logger;
    private ActivityRecordService activityRecordService;
    private PersistentData persistentData;
    private SessionFactory sessionFactory;
    private DiscordWebhookService discordWebhookService;
    private Player player;
    private JoinHandler joinHandler;

    @BeforeEach
    void setUp() {
        logger = mock(Logger.class);
        activityRecordService = mock(ActivityRecordService.class);
        persistentData = mock(PersistentData.class);
        sessionFactory = mock(SessionFactory.class);
        discordWebhookService = mock(DiscordWebhookService.class);
        player = mock(Player.class);
        when(player.getName()).thenReturn("Steve");
        when(player.getUniqueId()).thenReturn(UUID.randomUUID());
        joinHandler = new JoinHandler(activityRecordService, persistentData, sessionFactory,
                discordWebhookService, mock(JavaPlugin.class));
    }

    @Test
    @DisplayName("Should not create a second session when a new record was just assigned")
    void testHandle_NewPlayer_DoesNotCreateExtraSession() {
        when(activityRecordService.assignActivityRecordToPlayerIfNecessary(player)).thenReturn(true);

        joinHandler.handle(new PlayerJoinEvent(player, "joined"));

        verify(sessionFactory, never()).createSession(any(Player.class));
        verify(persistentData, never()).getActivityRecord(any(Player.class));
    }

    @Test
    @DisplayName("Should add a new session to a returning player's record and mark it most recent")
    void testHandle_ReturningPlayer_AddsAndSelectsNewSession() throws Exception {
        Session firstSession = new Session(logger, 1, player.getUniqueId());
        firstSession.endSession();
        ActivityRecord record = new ActivityRecord(player.getUniqueId(), firstSession);
        Session newSession = new Session(logger, 2, player.getUniqueId());
        when(activityRecordService.assignActivityRecordToPlayerIfNecessary(player)).thenReturn(false);
        when(persistentData.getActivityRecord(player)).thenReturn(record);
        when(sessionFactory.createSession(player)).thenReturn(newSession);

        joinHandler.handle(new PlayerJoinEvent(player, "joined"));

        assertEquals(2, record.getSessions().size());
        assertSame(newSession, record.getMostRecentSession());
    }

    @Test
    @DisplayName("Should retry assignment and skip the notification when the existing record cannot be found")
    void testHandle_MissingRecord_RetriesAssignmentAndReturns() {
        when(activityRecordService.assignActivityRecordToPlayerIfNecessary(player)).thenReturn(false);
        when(persistentData.getActivityRecord(player)).thenReturn(null);

        joinHandler.handle(new PlayerJoinEvent(player, "joined"));

        verify(activityRecordService, times(2)).assignActivityRecordToPlayerIfNecessary(player);
        verify(sessionFactory, never()).createSession(any(Player.class));
        verify(discordWebhookService, never()).isEnabled();
    }

    @Test
    @DisplayName("Should not prepare a join message when the Discord webhook is disabled")
    void testHandle_WebhookDisabled_NoNotification() {
        when(activityRecordService.assignActivityRecordToPlayerIfNecessary(player)).thenReturn(true);
        when(discordWebhookService.isEnabled()).thenReturn(false);

        joinHandler.handle(new PlayerJoinEvent(player, "joined"));

        verify(discordWebhookService, never()).prepareJoinMessage(anyString());
        verify(discordWebhookService, never()).sendWebhookMessage(anyString(), anyString());
    }

    @Test
    @DisplayName("Should not notify for a non-staff player when the webhook is staff-only")
    void testHandle_StaffOnlyAndNotStaff_NoNotification() {
        when(activityRecordService.assignActivityRecordToPlayerIfNecessary(player)).thenReturn(true);
        when(discordWebhookService.isEnabled()).thenReturn(true);
        when(discordWebhookService.isStaffOnly()).thenReturn(true);
        when(player.hasPermission("at.staff")).thenReturn(false);

        joinHandler.handle(new PlayerJoinEvent(player, "joined"));

        verify(discordWebhookService, never()).prepareJoinMessage(anyString());
        verify(discordWebhookService, never()).sendWebhookMessage(anyString(), anyString());
    }

    @Test
    @DisplayName("Should not schedule a dispatch when the prepared join message is null")
    void testHandle_NullMessage_NoDispatch() {
        when(activityRecordService.assignActivityRecordToPlayerIfNecessary(player)).thenReturn(true);
        when(discordWebhookService.isEnabled()).thenReturn(true);
        when(discordWebhookService.isStaffOnly()).thenReturn(false);
        when(discordWebhookService.getWebhookUrl()).thenReturn("https://discord.com/api/webhooks/1/abc");
        when(discordWebhookService.prepareJoinMessage("Steve")).thenReturn(null);

        // Reaching the scheduler would throw here, since the mocked plugin has no server.
        assertDoesNotThrow(() -> joinHandler.handle(new PlayerJoinEvent(player, "joined")));

        verify(discordWebhookService).prepareJoinMessage("Steve");
        verify(discordWebhookService, never()).sendWebhookMessage(anyString(), anyString());
    }
}
