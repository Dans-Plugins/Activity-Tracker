package dansplugins.activitytracker.services;

import dansplugins.activitytracker.utils.Logger;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for DiscordWebhookService
 */
public class DiscordWebhookServiceTest {

    @Mock
    private ConfigService configService;

    @Mock
    private Logger logger;

    private DiscordWebhookService discordWebhookService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        discordWebhookService = new DiscordWebhookService(configService, logger);
    }

    @Test
    public void testIsEnabledReturnsFalseWhenDisabled() {
        when(configService.getBoolean("discordWebhookEnabled")).thenReturn(false);

        assertFalse(discordWebhookService.isEnabled());
    }

    @Test
    public void testIsEnabledReturnsFalseWhenUrlIsEmpty() {
        when(configService.getBoolean("discordWebhookEnabled")).thenReturn(true);
        when(configService.getString("discordWebhookUrl")).thenReturn("");

        assertFalse(discordWebhookService.isEnabled());
    }

    @Test
    public void testIsEnabledReturnsFalseWhenUrlIsNull() {
        when(configService.getBoolean("discordWebhookEnabled")).thenReturn(true);
        when(configService.getString("discordWebhookUrl")).thenReturn(null);

        assertFalse(discordWebhookService.isEnabled());
    }

    @Test
    public void testIsEnabledReturnsTrueWhenConfigured() {
        when(configService.getBoolean("discordWebhookEnabled")).thenReturn(true);
        when(configService.getString("discordWebhookUrl")).thenReturn("https://discord.com/api/webhooks/test");

        assertTrue(discordWebhookService.isEnabled());
    }

    @Test
    public void testIsStaffOnlyReturnsConfigValue() {
        when(configService.getBoolean("discordWebhookStaffOnly")).thenReturn(true);
        assertTrue(discordWebhookService.isStaffOnly());

        when(configService.getBoolean("discordWebhookStaffOnly")).thenReturn(false);
        assertFalse(discordWebhookService.isStaffOnly());
    }

    @Test
    public void testSendJoinNotificationSkipsWhenTemplateIsEmpty() {
        when(configService.getString("discordWebhookJoinMessage")).thenReturn("");

        // Should not throw and should not attempt to send
        discordWebhookService.sendJoinNotification("TestPlayer");
    }

    @Test
    public void testSendJoinNotificationSkipsWhenTemplateIsNull() {
        when(configService.getString("discordWebhookJoinMessage")).thenReturn(null);

        // Should not throw and should not attempt to send
        discordWebhookService.sendJoinNotification("TestPlayer");
    }

    @Test
    public void testSendQuitNotificationSkipsWhenTemplateIsEmpty() {
        when(configService.getString("discordWebhookQuitMessage")).thenReturn("");

        // Should not throw and should not attempt to send
        discordWebhookService.sendQuitNotification("TestPlayer");
    }

    @Test
    public void testSendQuitNotificationSkipsWhenTemplateIsNull() {
        when(configService.getString("discordWebhookQuitMessage")).thenReturn(null);

        // Should not throw and should not attempt to send
        discordWebhookService.sendQuitNotification("TestPlayer");
    }

    @Test
    public void testSendJoinNotificationSkipsWhenUrlIsEmpty() {
        when(configService.getString("discordWebhookJoinMessage")).thenReturn("**{player}** joined!");
        when(configService.getString("discordWebhookUrl")).thenReturn("");

        // Should not throw
        discordWebhookService.sendJoinNotification("TestPlayer");
    }

    @Test
    public void testSendQuitNotificationSkipsWhenUrlIsEmpty() {
        when(configService.getString("discordWebhookQuitMessage")).thenReturn("**{player}** left.");
        when(configService.getString("discordWebhookUrl")).thenReturn("");

        // Should not throw
        discordWebhookService.sendQuitNotification("TestPlayer");
    }

    @Test
    public void testSendJoinNotificationHandlesInvalidUrl() {
        when(configService.getString("discordWebhookJoinMessage")).thenReturn("**{player}** joined!");
        when(configService.getString("discordWebhookUrl")).thenReturn("not-a-valid-url");

        // Should not throw - errors are logged gracefully
        discordWebhookService.sendJoinNotification("TestPlayer");
    }

    @Test
    public void testSendQuitNotificationHandlesInvalidUrl() {
        when(configService.getString("discordWebhookQuitMessage")).thenReturn("**{player}** left.");
        when(configService.getString("discordWebhookUrl")).thenReturn("not-a-valid-url");

        // Should not throw - errors are logged gracefully
        discordWebhookService.sendQuitNotification("TestPlayer");
    }
}
