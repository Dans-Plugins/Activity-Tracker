package dansplugins.activitytracker.api;

import com.google.gson.Gson;
import dansplugins.activitytracker.data.PersistentData;
import dansplugins.activitytracker.objects.ActivityRecord;
import dansplugins.activitytracker.objects.Session;
import dansplugins.activitytracker.services.ActivityRecordService;
import dansplugins.activitytracker.utils.Logger;
import org.bukkit.Bukkit;
import preponderous.ponder.minecraft.bukkit.tools.UUIDChecker;
import spark.Spark;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * REST API service for exposing player activity data
 * @author Daniel McCoy Stephenson
 */
public class RestApiService {
    private final PersistentData persistentData;
    private final ActivityRecordService activityRecordService;
    private final Logger logger;
    private final Gson gson;
    private final int port;
    private boolean running = false;

    public RestApiService(PersistentData persistentData, ActivityRecordService activityRecordService, Logger logger, int port) {
        this.persistentData = persistentData;
        this.activityRecordService = activityRecordService;
        this.logger = logger;
        this.gson = new Gson();
        this.port = port;
    }

    /**
     * Start the REST API server
     */
    public void start() {
        if (running) {
            logger.log("REST API is already running.");
            return;
        }

        try {
            Spark.port(port);
            setupRoutes();
            Spark.awaitInitialization();
            running = true;
            logger.log("REST API started on port " + port);
        } catch (Exception e) {
            logger.log("Failed to start REST API: " + e.getMessage());
        }
    }

    /**
     * Stop the REST API server
     */
    public void stop() {
        if (!running) {
            return;
        }

        try {
            Spark.stop();
            Spark.awaitStop();
            running = false;
            logger.log("REST API stopped.");
        } catch (Exception e) {
            logger.log("Error stopping REST API: " + e.getMessage());
        }
    }

    /**
     * Setup all REST API routes
     */
    private void setupRoutes() {
        // Enable CORS
        Spark.before((request, response) -> {
            response.header("Access-Control-Allow-Origin", "*");
            response.header("Access-Control-Allow-Methods", "GET, OPTIONS");
            response.header("Access-Control-Allow-Headers", "Content-Type");
            response.type("application/json");
        });

        // Handle OPTIONS requests for CORS
        Spark.options("/*", (request, response) -> {
            return "OK";
        });

        // GET /api/stats - Get server statistics
        Spark.get("/api/stats", (request, response) -> {
            int uniqueLogins = persistentData.getActivityRecords().size();
            int totalLogins = persistentData.getTotalNumberOfLogins();
            StatsResponse stats = new StatsResponse(uniqueLogins, totalLogins);
            return gson.toJson(stats);
        });

        // GET /api/leaderboard - Get top players by activity
        Spark.get("/api/leaderboard", (request, response) -> {
            ArrayList<ActivityRecord> topRecords = activityRecordService.getTopTenRecords();
            List<LeaderboardEntry> leaderboard = new ArrayList<>();
            UUIDChecker uuidChecker = new UUIDChecker();

            for (ActivityRecord record : topRecords) {
                if (record == null) {
                    continue;
                }
                String playerName = uuidChecker.findPlayerNameBasedOnUUID(record.getPlayerUUID());
                if (playerName == null) {
                    playerName = "Unknown";
                }
                LeaderboardEntry entry = new LeaderboardEntry(
                        record.getPlayerUUID().toString(),
                        playerName,
                        record.getTotalHoursSpent(),
                        record.getSessions().size()
                );
                leaderboard.add(entry);
            }

            return gson.toJson(leaderboard);
        });

        // GET /api/players - Get list of all tracked players
        Spark.get("/api/players", (request, response) -> {
            List<String> playerUuids = new ArrayList<>();
            for (ActivityRecord record : persistentData.getActivityRecords()) {
                playerUuids.add(record.getPlayerUUID().toString());
            }
            return gson.toJson(playerUuids);
        });

        // GET /api/players/:uuid - Get detailed player activity info
        Spark.get("/api/players/:uuid", (request, response) -> {
            String uuidParam = request.params(":uuid");
            UUID playerUuid;

            try {
                playerUuid = UUID.fromString(uuidParam);
            } catch (IllegalArgumentException e) {
                response.status(400);
                return gson.toJson(new ErrorResponse("Invalid UUID format"));
            }

            ActivityRecord record = persistentData.getActivityRecord(playerUuid);
            if (record == null) {
                response.status(404);
                return gson.toJson(new ErrorResponse("Player not found"));
            }

            UUIDChecker uuidChecker = new UUIDChecker();
            String playerName = uuidChecker.findPlayerNameBasedOnUUID(playerUuid);
            if (playerName == null) {
                playerName = "Unknown";
            }

            boolean online = Bukkit.getPlayer(playerUuid) != null;
            Session mostRecentSession = null;
            if (!record.getSessions().isEmpty()) {
                try {
                    mostRecentSession = record.getMostRecentSession();
                } catch (NullPointerException e) {
                    // Session list exists but mostRecentSession is null
                    logger.log("Unable to get most recent session for player " + playerUuid);
                }
            }

            String firstLogin = null;
            String lastLogin = null;
            String lastLogout = null;
            Double hoursSinceLogin = null;
            Double hoursSinceLogout = null;

            if (!record.getSessions().isEmpty()) {
                Session firstSession = record.getSessions().get(0);
                if (firstSession != null) {
                    firstLogin = firstSession.getLoginDate().toString();
                }
            }

            if (mostRecentSession != null) {
                lastLogin = mostRecentSession.getLoginDate().toString();
                if (mostRecentSession.getLogoutDate() != null) {
                    lastLogout = mostRecentSession.getLogoutDate().toString();
                }
                if (online) {
                    hoursSinceLogin = mostRecentSession.getMinutesSinceLogin() / 60.0;
                } else {
                    hoursSinceLogout = mostRecentSession.getMinutesSinceLogout() / 60.0;
                }
            }

            PlayerActivityResponse playerResponse = new PlayerActivityResponse(
                    playerUuid.toString(),
                    playerName,
                    record.getSessions().size(),
                    record.getTotalHoursSpent(),
                    online,
                    firstLogin,
                    lastLogin,
                    lastLogout,
                    hoursSinceLogin,
                    hoursSinceLogout
            );

            return gson.toJson(playerResponse);
        });

        // GET /api/health - Health check endpoint
        Spark.get("/api/health", (request, response) -> {
            return gson.toJson(new HealthResponse("OK", "Activity Tracker REST API"));
        });

        // Handle 404
        Spark.notFound((request, response) -> {
            response.type("application/json");
            return gson.toJson(new ErrorResponse("Endpoint not found"));
        });

        // Handle 500
        Spark.internalServerError((request, response) -> {
            response.type("application/json");
            return gson.toJson(new ErrorResponse("Internal server error"));
        });
    }

    /**
     * Simple error response class
     */
    private static class ErrorResponse {
        private final String error;

        public ErrorResponse(String error) {
            this.error = error;
        }

        public String getError() {
            return error;
        }
    }

    /**
     * Simple health response class
     */
    private static class HealthResponse {
        private final String status;
        private final String service;

        public HealthResponse(String status, String service) {
            this.status = status;
            this.service = service;
        }

        public String getStatus() {
            return status;
        }

        public String getService() {
            return service;
        }
    }
}
