package dansplugins.activitytracker.services;

import com.sun.net.httpserver.HttpServer;
import dansplugins.activitytracker.utils.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

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

    private HttpServer server;
    private final List<String> receivedBodies = new CopyOnWriteArrayList<>();
    private final List<String> receivedMethods = new CopyOnWriteArrayList<>();
    private final List<String> receivedContentTypes = new CopyOnWriteArrayList<>();

    @Before
    public void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
        discordWebhookService = new DiscordWebhookService(configService, logger);
    }

    @After
    public void tearDown() throws Exception {
        if (server != null) {
            server.stop(0);
        }
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

    @Test
    public void testSendWebhookMessagePostsJsonPayload() throws Exception {
        String url = startServer(204);

        discordWebhookService.sendWebhookMessage(url, "**TestPlayer** joined!");

        assertEquals(1, receivedBodies.size());
        assertEquals("POST", receivedMethods.get(0));
        assertEquals("application/json", receivedContentTypes.get(0));
        assertEquals("{\"content\": \"**TestPlayer** joined!\"}", receivedBodies.get(0));
        verifyNoInteractions(logger);
    }

    @Test
    public void testSendWebhookMessageEscapesJsonSpecialCharacters() throws Exception {
        String url = startServer(204);

        discordWebhookService.sendWebhookMessage(url, "say \"hi\" \\ line1\nline2\r\tend");

        assertEquals(1, receivedBodies.size());
        assertEquals("{\"content\": \"say \\\"hi\\\" \\\\ line1\\nline2\\r\\tend\"}", receivedBodies.get(0));
    }

    @Test
    public void testSendWebhookMessageEscapesControlCharacters() throws Exception {
        String url = startServer(204);

        discordWebhookService.sendWebhookMessage(url, "end\u0001");

        assertEquals(1, receivedBodies.size());
        assertEquals("{\"content\": \"end\\u0001\"}", receivedBodies.get(0));
    }

    @Test
    public void testSendWebhookMessageSendsEmptyContentForNullMessage() throws Exception {
        String url = startServer(204);

        discordWebhookService.sendWebhookMessage(url, null);

        assertEquals(1, receivedBodies.size());
        assertEquals("{\"content\": \"\"}", receivedBodies.get(0));
    }

    @Test
    public void testSendWebhookMessageLogsNonSuccessResponseCode() throws Exception {
        String url = startServer(400);

        discordWebhookService.sendWebhookMessage(url, "**TestPlayer** joined!");

        assertEquals(1, receivedBodies.size());
        verify(logger).log("Discord webhook returned error code: 400");
    }

    /**
     * Starts an in-process loopback server that records each request and answers with the given status.
     * @return the URL to post to.
     */
    private String startServer(final int status) throws IOException {
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/webhook", exchange -> {
            receivedMethods.add(exchange.getRequestMethod());
            receivedContentTypes.add(exchange.getRequestHeaders().getFirst("Content-Type"));
            receivedBodies.add(readAll(exchange.getRequestBody()));
            if (status == 204) {
                exchange.sendResponseHeaders(status, -1);
            } else {
                byte[] body = "{\"message\": \"error\"}".getBytes(StandardCharsets.UTF_8);
                exchange.sendResponseHeaders(status, body.length);
                exchange.getResponseBody().write(body);
            }
            exchange.close();
        });
        server.start();
        return "http://127.0.0.1:" + server.getAddress().getPort() + "/webhook";
    }

    private static String readAll(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int n;
        while ((n = in.read(buffer)) != -1) {
            out.write(buffer, 0, n);
        }
        return new String(out.toByteArray(), StandardCharsets.UTF_8);
    }
}
