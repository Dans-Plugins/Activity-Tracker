package dansplugins.activitytracker.eventhandlers;

import dansplugins.activitytracker.data.PersistentData;
import dansplugins.activitytracker.exceptions.NoSessionException;
import dansplugins.activitytracker.objects.ActivityRecord;
import dansplugins.activitytracker.objects.Session;
import dansplugins.activitytracker.services.DiscordWebhookService;
import dansplugins.activitytracker.utils.Logger;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for QuitHandler.
 * Covers ending the current session, rolling its minutes into the record's hours,
 * the error paths that return early, and the gates in front of the Discord quit notification.
 * The asynchronous webhook dispatch itself is not exercised: JavaPlugin.getServer() is final and
 * cannot be stubbed with the default Mockito mock maker.
 *
 * @author Daniel McCoy Stephenson
 */
class QuitHandlerTest {

    private Logger logger;
    private PersistentData persistentData;
    private DiscordWebhookService discordWebhookService;
    private Player player;
    private QuitHandler quitHandler;

    @BeforeEach
    void setUp() {
        logger = mock(Logger.class);
        persistentData = mock(PersistentData.class);
        discordWebhookService = mock(DiscordWebhookService.class);
        player = mock(Player.class);
        when(player.getName()).thenReturn("Steve");
        when(player.getUniqueId()).thenReturn(UUID.randomUUID());
        quitHandler = new QuitHandler(persistentData, logger, discordWebhookService, mock(JavaPlugin.class));
    }

    private Session mockSession(boolean active, double minutesSpent) {
        Session session = mock(Session.class);
        when(session.getID()).thenReturn(7);
        when(session.isActive()).thenReturn(active);
        when(session.getMinutesSpent()).thenReturn(minutesSpent);
        return session;
    }

    @Test
    @DisplayName("Should end an active session and add its minutes to the record's hours")
    void testHandle_ActiveSession_EndsAndAccumulatesHours() {
        Session session = mockSession(true, 90.0);
        ActivityRecord record = new ActivityRecord(player.getUniqueId(), session);
        record.setHoursSpent(2.0);
        when(persistentData.getActivityRecord(player)).thenReturn(record);

        quitHandler.handle(new PlayerQuitEvent(player, "left"));

        verify(session).endSession();
        assertEquals(3.5, record.getHoursSpentNotIncludingTheCurrentSession(), 0.0001);
    }

    @Test
    @DisplayName("Should warn and leave hours unchanged when the session was already ended")
    void testHandle_InactiveSession_WarnsAndKeepsHours() {
        Session session = mockSession(false, 90.0);
        ActivityRecord record = new ActivityRecord(player.getUniqueId(), session);
        record.setHoursSpent(2.0);
        when(persistentData.getActivityRecord(player)).thenReturn(record);

        quitHandler.handle(new PlayerQuitEvent(player, "left"));

        verify(session, never()).endSession();
        verify(logger).log(contains("WARNING: Session for Steve was already ended."));
        assertEquals(2.0, record.getHoursSpentNotIncludingTheCurrentSession(), 0.0001);
    }

    @Test
    @DisplayName("Should log and still reach the notification step when ending the session throws")
    void testHandle_EndSessionThrows_LogsAndContinues() {
        Session session = mockSession(true, 90.0);
        doThrow(new IllegalStateException("boom")).when(session).endSession();
        ActivityRecord record = new ActivityRecord(player.getUniqueId(), session);
        record.setHoursSpent(2.0);
        when(persistentData.getActivityRecord(player)).thenReturn(record);

        quitHandler.handle(new PlayerQuitEvent(player, "left"));

        verify(logger).log(contains("ERROR: Failed to properly end session for Steve: boom"));
        assertEquals(2.0, record.getHoursSpentNotIncludingTheCurrentSession(), 0.0001);
        verify(discordWebhookService).isEnabled();
    }

    @Test
    @DisplayName("Should log an error and skip the notification when the player has no record")
    void testHandle_NoRecord_LogsErrorAndReturns() {
        when(persistentData.getActivityRecord(player)).thenReturn(null);

        quitHandler.handle(new PlayerQuitEvent(player, "left"));

        verify(logger).log(contains("ERROR: No activity record found for Steve on logout."));
        verify(discordWebhookService, never()).isEnabled();
    }

    @Test
    @DisplayName("Should log an error and skip the notification when the record has no session")
    void testHandle_NoSession_LogsErrorAndReturns() throws Exception {
        ActivityRecord record = mock(ActivityRecord.class);
        when(record.getMostRecentSession()).thenThrow(new NoSessionException("none"));
        when(persistentData.getActivityRecord(player)).thenReturn(record);

        quitHandler.handle(new PlayerQuitEvent(player, "left"));

        verify(logger).log(contains("ERROR: The most recent session was null for Steve: none"));
        verify(record, never()).setHoursSpent(anyDouble());
        verify(discordWebhookService, never()).isEnabled();
    }

    @Test
    @DisplayName("Should not notify for a non-staff player when the webhook is staff-only")
    void testHandle_StaffOnlyAndNotStaff_NoNotification() {
        ActivityRecord record = new ActivityRecord(player.getUniqueId(), mockSession(true, 0.0));
        when(persistentData.getActivityRecord(player)).thenReturn(record);
        when(discordWebhookService.isEnabled()).thenReturn(true);
        when(discordWebhookService.isStaffOnly()).thenReturn(true);
        when(player.hasPermission("at.staff")).thenReturn(false);

        quitHandler.handle(new PlayerQuitEvent(player, "left"));

        verify(discordWebhookService, never()).prepareQuitMessage(anyString());
        verify(discordWebhookService, never()).sendWebhookMessage(anyString(), anyString());
    }

    @Test
    @DisplayName("Should not schedule a dispatch when the webhook URL is null")
    void testHandle_NullUrl_NoDispatch() {
        ActivityRecord record = new ActivityRecord(player.getUniqueId(), mockSession(true, 0.0));
        when(persistentData.getActivityRecord(player)).thenReturn(record);
        when(discordWebhookService.isEnabled()).thenReturn(true);
        when(discordWebhookService.isStaffOnly()).thenReturn(false);
        when(discordWebhookService.getWebhookUrl()).thenReturn(null);
        when(discordWebhookService.prepareQuitMessage("Steve")).thenReturn("Steve left");

        // Reaching the scheduler would throw here, since the mocked plugin has no server.
        assertDoesNotThrow(() -> quitHandler.handle(new PlayerQuitEvent(player, "left")));

        verify(discordWebhookService, never()).sendWebhookMessage(anyString(), anyString());
    }
}
