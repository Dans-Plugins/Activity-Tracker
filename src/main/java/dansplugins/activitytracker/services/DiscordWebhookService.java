package dansplugins.activitytracker.services;

import java.io.IOException;
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
     * @return true if enabled and a webhook URL is set.
     */
    public boolean isEnabled() {
        if (!configService.getBoolean("discordWebhookEnabled")) {
            return false;
        }
        String url = configService.getString("discordWebhookUrl");
        return url != null && !url.isEmpty();
    }

    /**
     * Checks if webhooks should only fire for staff members.
     * @return true if staff-only mode is active.
     */
    public boolean isStaffOnly() {
        return configService.getBoolean("discordWebhookStaffOnly");
    }

    /**
     * Sends a player join notification to the configured Discord webhook.
     * @param playerName The name of the player who joined.
     */
    public void sendJoinNotification(String playerName) {
        String template = configService.getString("discordWebhookJoinMessage");
        if (template == null || template.isEmpty()) {
            return;
        }
        String message = template.replace("{player}", playerName);
        sendWebhookMessage(message);
    }

    /**
     * Sends a player quit notification to the configured Discord webhook.
     * @param playerName The name of the player who quit.
     */
    public void sendQuitNotification(String playerName) {
        String template = configService.getString("discordWebhookQuitMessage");
        if (template == null || template.isEmpty()) {
            return;
        }
        String message = template.replace("{player}", playerName);
        sendWebhookMessage(message);
    }

    /**
     * Sends a message to the configured Discord webhook URL.
     * @param content The message content to send.
     */
    private void sendWebhookMessage(String content) {
        String webhookUrl = configService.getString("discordWebhookUrl");
        if (webhookUrl == null || webhookUrl.isEmpty()) {
            return;
        }

        try {
            URL url = new URL(webhookUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(10000);
            connection.setDoOutput(true);

            String jsonPayload = "{\"content\": \"" + escapeJson(content) + "\"}";

            OutputStream os = connection.getOutputStream();
            byte[] input = jsonPayload.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
            os.close();

            int responseCode = connection.getResponseCode();
            if (responseCode < 200 || responseCode >= 300) {
                logger.log("Discord webhook returned error code: " + responseCode);
            }
        } catch (IOException e) {
            logger.log("Failed to send Discord webhook message: " + e.getMessage());
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
                        sb.append(String.format("\\u%04x", (int) c));
                    } else {
                        sb.append(c);
                    }
                    break;
            }
        }
        return sb.toString();
    }
}
