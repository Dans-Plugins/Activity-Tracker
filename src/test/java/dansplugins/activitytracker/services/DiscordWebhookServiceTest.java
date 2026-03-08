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
    public void testIsEnabledReturnsFalseWhenUrlIsWhitespaceOnly() {
        when(configService.getBoolean("discordWebhookEnabled")).thenReturn(true);
        when(configService.getString("discordWebhookUrl")).thenReturn("   ");

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
    public void testSendJoinNotificationSkipsWhenDisabled() {
        when(configService.getBoolean("discordWebhookEnabled")).thenReturn(false);

        discordWebhookService.sendJoinNotification("TestPlayer");

        // Should not attempt to read join message template when disabled
        verify(configService, never()).getString("discordWebhookJoinMessage");
    }

    @Test
    public void testSendQuitNotificationSkipsWhenDisabled() {
        when(configService.getBoolean("discordWebhookEnabled")).thenReturn(false);

        discordWebhookService.sendQuitNotification("TestPlayer");

        // Should not attempt to read quit message template when disabled
        verify(configService, never()).getString("discordWebhookQuitMessage");
    }

    @Test
    public void testSendJoinNotificationSkipsWhenTemplateIsEmpty() {
        when(configService.getBoolean("discordWebhookEnabled")).thenReturn(true);
        when(configService.getString("discordWebhookUrl")).thenReturn("https://discord.com/api/webhooks/test");
        when(configService.getString("discordWebhookJoinMessage")).thenReturn("");

        discordWebhookService.sendJoinNotification("TestPlayer");

        // Should check the template but not attempt to fetch URL for sending
        verify(configService).getString("discordWebhookJoinMessage");
        verify(configService, times(1)).getString("discordWebhookUrl");
    }

    @Test
    public void testSendJoinNotificationSkipsWhenTemplateIsNull() {
        when(configService.getBoolean("discordWebhookEnabled")).thenReturn(true);
        when(configService.getString("discordWebhookUrl")).thenReturn("https://discord.com/api/webhooks/test");
        when(configService.getString("discordWebhookJoinMessage")).thenReturn(null);

        discordWebhookService.sendJoinNotification("TestPlayer");

        verify(configService).getString("discordWebhookJoinMessage");
        verify(configService, times(1)).getString("discordWebhookUrl");
    }

    @Test
    public void testSendQuitNotificationSkipsWhenTemplateIsEmpty() {
        when(configService.getBoolean("discordWebhookEnabled")).thenReturn(true);
        when(configService.getString("discordWebhookUrl")).thenReturn("https://discord.com/api/webhooks/test");
        when(configService.getString("discordWebhookQuitMessage")).thenReturn("");

        discordWebhookService.sendQuitNotification("TestPlayer");

        verify(configService).getString("discordWebhookQuitMessage");
        verify(configService, times(1)).getString("discordWebhookUrl");
    }

    @Test
    public void testSendQuitNotificationSkipsWhenTemplateIsNull() {
        when(configService.getBoolean("discordWebhookEnabled")).thenReturn(true);
        when(configService.getString("discordWebhookUrl")).thenReturn("https://discord.com/api/webhooks/test");
        when(configService.getString("discordWebhookQuitMessage")).thenReturn(null);

        discordWebhookService.sendQuitNotification("TestPlayer");

        verify(configService).getString("discordWebhookQuitMessage");
        verify(configService, times(1)).getString("discordWebhookUrl");
    }

    @Test
    public void testSendJoinNotificationHandlesInvalidUrl() {
        when(configService.getBoolean("discordWebhookEnabled")).thenReturn(true);
        when(configService.getString("discordWebhookUrl")).thenReturn("not-a-valid-url");
        when(configService.getString("discordWebhookJoinMessage")).thenReturn("**{player}** joined!");

        discordWebhookService.sendJoinNotification("TestPlayer");

        // Error should be logged gracefully
        verify(logger).log(contains("Failed to send Discord webhook message"));
    }

    @Test
    public void testSendQuitNotificationHandlesInvalidUrl() {
        when(configService.getBoolean("discordWebhookEnabled")).thenReturn(true);
        when(configService.getString("discordWebhookUrl")).thenReturn("not-a-valid-url");
        when(configService.getString("discordWebhookQuitMessage")).thenReturn("**{player}** left.");

        discordWebhookService.sendQuitNotification("TestPlayer");

        // Error should be logged gracefully
        verify(logger).log(contains("Failed to send Discord webhook message"));
    }
}
