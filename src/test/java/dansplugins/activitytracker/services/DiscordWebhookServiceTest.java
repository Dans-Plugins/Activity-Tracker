package dansplugins.activitytracker.services;

import dansplugins.activitytracker.utils.Logger;
import org.junit.After;
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
    private AutoCloseable mocks;

    @Before
    public void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
        discordWebhookService = new DiscordWebhookService(configService, logger);
    }

    @After
    public void tearDown() throws Exception {
        mocks.close();
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
    public void testGetWebhookUrlReturnsTrimmedUrl() {
        when(configService.getString("discordWebhookUrl")).thenReturn("  https://discord.com/api/webhooks/test  ");

        assertEquals("https://discord.com/api/webhooks/test", discordWebhookService.getWebhookUrl());
    }

    @Test
    public void testGetWebhookUrlReturnsNullWhenEmpty() {
        when(configService.getString("discordWebhookUrl")).thenReturn("");

        assertNull(discordWebhookService.getWebhookUrl());
    }

    @Test
    public void testGetWebhookUrlReturnsNullWhenNull() {
        when(configService.getString("discordWebhookUrl")).thenReturn(null);

        assertNull(discordWebhookService.getWebhookUrl());
    }

    @Test
    public void testGetWebhookUrlReturnsNullWhenWhitespaceOnly() {
        when(configService.getString("discordWebhookUrl")).thenReturn("   ");

        assertNull(discordWebhookService.getWebhookUrl());
    }

    @Test
    public void testPrepareJoinMessageReplacesPlayerName() {
        when(configService.getString("discordWebhookJoinMessage")).thenReturn("**{player}** joined!");

        assertEquals("**TestPlayer** joined!", discordWebhookService.prepareJoinMessage("TestPlayer"));
    }

    @Test
    public void testPrepareJoinMessageReturnsNullWhenTemplateIsEmpty() {
        when(configService.getString("discordWebhookJoinMessage")).thenReturn("");

        assertNull(discordWebhookService.prepareJoinMessage("TestPlayer"));
    }

    @Test
    public void testPrepareJoinMessageReturnsNullWhenTemplateIsNull() {
        when(configService.getString("discordWebhookJoinMessage")).thenReturn(null);

        assertNull(discordWebhookService.prepareJoinMessage("TestPlayer"));
    }

    @Test
    public void testPrepareQuitMessageReplacesPlayerName() {
        when(configService.getString("discordWebhookQuitMessage")).thenReturn("**{player}** left.");

        assertEquals("**TestPlayer** left.", discordWebhookService.prepareQuitMessage("TestPlayer"));
    }

    @Test
    public void testPrepareQuitMessageReturnsNullWhenTemplateIsEmpty() {
        when(configService.getString("discordWebhookQuitMessage")).thenReturn("");

        assertNull(discordWebhookService.prepareQuitMessage("TestPlayer"));
    }

    @Test
    public void testPrepareQuitMessageReturnsNullWhenTemplateIsNull() {
        when(configService.getString("discordWebhookQuitMessage")).thenReturn(null);

        assertNull(discordWebhookService.prepareQuitMessage("TestPlayer"));
    }

    @Test
    public void testSendWebhookMessageSkipsWhenUrlIsNull() {
        discordWebhookService.sendWebhookMessage(null, "test message");

        // No HTTP call should be attempted, no errors logged
        verifyNoInteractions(logger);
    }

    @Test
    public void testSendWebhookMessageSkipsWhenUrlIsEmpty() {
        discordWebhookService.sendWebhookMessage("", "test message");

        // No HTTP call should be attempted, no errors logged
        verifyNoInteractions(logger);
    }

    @Test
    public void testSendWebhookMessageHandlesInvalidUrl() {
        discordWebhookService.sendWebhookMessage("not-a-valid-url", "**TestPlayer** joined!");

        // Error should be logged gracefully
        verify(logger).log(contains("Failed to send Discord webhook message"));
    }
}
