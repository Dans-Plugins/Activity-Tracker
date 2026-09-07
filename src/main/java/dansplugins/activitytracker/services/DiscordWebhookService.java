package dansplugins.activitytracker.services;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import dansplugins.activitytracker.utils.Logger;

/**
 * Service for sending Discord webhook notifications when players join or leave the server.
 * @author Daniel McCoy Stephenson
 */
public class DiscordWebhookService {
    private final ConfigService configService;
    private final Logger logger;

    public DiscordWebhookService(ConfigService configService, Logger logger) {
        this.configService = configService;
        this.logger = logger;
    }

    /**
     * Checks if the Discord webhook feature is enabled and configured.
     * Must be called from the main server thread.
     * @return true if enabled and a webhook URL is set.
     */
    public boolean isEnabled() {
        if (!configService.getBoolean("discordWebhookEnabled")) {
            return false;
        }
        String url = configService.getString("discordWebhookUrl");
        if (url == null) {
            return false;
        }
        String trimmed = url.trim();
        return !trimmed.isEmpty();
    }

    /**
     * Checks if webhooks should only fire for staff members.
     * Must be called from the main server thread.
     * @return true if staff-only mode is active.
     */
    public boolean isStaffOnly() {
        return configService.getBoolean("discordWebhookStaffOnly");
    }

    /**
     * Returns the configured webhook URL, trimmed.
     * Must be called from the main server thread.
     * @return the trimmed webhook URL, or null if not configured.
     */
    public String getWebhookUrl() {
        String url = configService.getString("discordWebhookUrl");
        if (url == null) {
            return null;
        }
        String trimmed = url.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    /**
     * Prepares a join notification message by applying the player name to the configured template.
     * Must be called from the main server thread.
     * @param playerName The name of the player who joined.
     * @return The formatted message, or null if the template is empty/null.
     */
    public String prepareJoinMessage(String playerName) {
        String template = configService.getString("discordWebhookJoinMessage");
        if (template == null || template.isEmpty()) {
            return null;
        }
        return template.replace("{player}", playerName);
    }

    /**
     * Prepares a quit notification message by applying the player name to the configured template.
     * Must be called from the main server thread.
     * @param playerName The name of the player who quit.
     * @return The formatted message, or null if the template is empty/null.
     */
    public String prepareQuitMessage(String playerName) {
        String template = configService.getString("discordWebhookQuitMessage");
        if (template == null || template.isEmpty()) {
            return null;
        }
        return template.replace("{player}", playerName);
    }

    /**
     * Sends a message to the specified Discord webhook URL.
     * This method performs HTTP I/O and should be called from an async task.
     * Does not access Bukkit APIs.
     * @param webhookUrl The Discord webhook URL to post to.
     * @param content The message content to send.
     */
    public void sendWebhookMessage(String webhookUrl, String content) {
        if (webhookUrl == null || webhookUrl.isEmpty()) {
            return;
        }

        HttpURLConnection connection = null;
        try {
            URL url = new URL(webhookUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(10000);
            connection.setDoOutput(true);

            String jsonPayload = "{\"content\": \"" + escapeJson(content) + "\"}";
            byte[] input = jsonPayload.getBytes(StandardCharsets.UTF_8);

            OutputStream os = connection.getOutputStream();
            try {
                os.write(input, 0, input.length);
            } finally {
                os.close();
            }

            int responseCode = connection.getResponseCode();
            if (responseCode < 200 || responseCode >= 300) {
                logger.log("Discord webhook returned error code: " + responseCode);
            }

            // Consume response stream to free up the connection
            InputStream is = (responseCode >= 200 && responseCode < 300)
                    ? connection.getInputStream()
                    : connection.getErrorStream();
            if (is != null) {
                try {
                    byte[] buffer = new byte[1024];
                    while (is.read(buffer) != -1) { }
                } finally {
                    is.close();
                }
            }
        } catch (IOException e) {
            logger.log("Failed to send Discord webhook message: " + e.getMessage());
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    /**
     * Escapes special characters for JSON string values.
     * @param text The text to escape.
     * @return The escaped text safe for JSON inclusion.
     */
    private String escapeJson(String text) {
        if (text == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            switch (c) {
                case '"':
                    sb.append("\\\"");
                    break;
                case '\\':
                    sb.append("\\\\");
                    break;
                case '\n':
                    sb.append("\\n");
                    break;
                case '\r':
                    sb.append("\\r");
                    break;
                case '\t':
                    sb.append("\\t");
                    break;
                default:
                    if (c < 0x20) {
                        sb.append(String.format("\\u%04x", c));
                    } else {
                        sb.append(c);
                    }
                    break;
            }
        }
        return sb.toString();
    }
}
