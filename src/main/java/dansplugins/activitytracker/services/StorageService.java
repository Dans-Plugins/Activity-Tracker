package dansplugins.activitytracker.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import dansplugins.activitytracker.ActivityTracker;
import dansplugins.activitytracker.data.PersistentData;
import dansplugins.activitytracker.exceptions.NoSessionException;
import dansplugins.activitytracker.objects.ActivityRecord;
import dansplugins.activitytracker.objects.Session;
import dansplugins.activitytracker.utils.Logger;

/**
 * @author Daniel McCoy Stephenson
 */
public class StorageService {
    private final ConfigService configService;
    private final ActivityTracker activityTracker;
    private final PersistentData persistentData;
    private final Logger logger;

    private final static String FILE_PATH = "./plugins/ActivityTracker/";
    private final static String ACTIVITY_RECORDS_FILE_NAME = "activityRecords.json";
    private final static String SESSIONS_FILE_NAME = "sessions.json";
    private final static String TEMPORARY_FILE_SUFFIX = ".tmp";
    private final static String PRESERVED_FILE_SUFFIX = ".unreadable";

    private final static Type LIST_MAP_TYPE = new TypeToken<ArrayList<HashMap<String, String>>>(){}.getType();

    private Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private boolean dataFilesPreserved = false;

    public StorageService(ConfigService configService, ActivityTracker activityTracker, PersistentData persistentData, Logger logger) {
        this.configService = configService;
        this.activityTracker = activityTracker;
        this.persistentData = persistentData;
        this.logger = logger;
    }

    public void save() {
        saveActivityRecords();
        saveSessions();
        if (configService.hasBeenAltered()) {
            activityTracker.saveConfig();
        }
    }

    public void load() {
        dataFilesPreserved = false;
        loadActivityRecords();
        loadSessions();
        validateAndRepairData();
        closeSessionsLeftActive();
    }

    /**
     * Writes to a temporary file and renames it over the data file, so a server
     * stopping mid-save leaves the previous save in place rather than a partial one.
     */
    public void writeOutFiles(List<Map<String, String>> saveData, String fileName) {
        try {
            File parentFolder = new File(FILE_PATH);
            parentFolder.mkdir();
            File file = new File(FILE_PATH, fileName);
            File temporaryFile = new File(FILE_PATH, fileName + TEMPORARY_FILE_SUFFIX);
            try (FileOutputStream outputStream = new FileOutputStream(temporaryFile);
                 OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8)) {
                outputStreamWriter.write(gson.toJson(saveData));
                outputStreamWriter.flush();
                outputStream.getFD().sync();
            }
            Files.move(temporaryFile.toPath(), file.toPath(), StandardCopyOption.ATOMIC_MOVE);
        } catch (IOException e) {
            logger.error("Failed to save " + fileName + ": " + e);
        }
    }

    public ArrayList<HashMap<String, String>> loadDataFromFilename(String filename) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (JsonReader reader = new JsonReader(new InputStreamReader(new FileInputStream(filename), StandardCharsets.UTF_8))) {
            ArrayList<HashMap<String, String>> data = gson.fromJson(reader, LIST_MAP_TYPE);
            if (data != null) {
                return data;
            }
            reportUnreadableFile(filename, "it has no content");
        } catch (FileNotFoundException e) {
            // Fail silently because this can actually happen in normal use
        } catch (IOException | JsonParseException e) {
            reportUnreadableFile(filename, e.getMessage());
        }
        return new ArrayList<>();
    }

    /**
     * Loading carries on without the file, and the next save replaces it. Both
     * data files are copied first: with the records gone, that save would also
     * drop every stored session, because sessions are saved through their records.
     */
    private void reportUnreadableFile(String filename, String reason) {
        logger.error("Could not read " + new File(filename).getName() + ": " + reason);
        if (dataFilesPreserved) {
            return;
        }
        dataFilesPreserved = true;
        preserveFile(ACTIVITY_RECORDS_FILE_NAME);
        preserveFile(SESSIONS_FILE_NAME);
    }

    private void preserveFile(String fileName) {
        File file = new File(FILE_PATH, fileName);
        if (!file.exists()) {
            return;
        }
        try {
            File copy = findUnusedPreservedFile(fileName);
            Files.copy(file.toPath(), copy.toPath());
            logger.error("Kept a copy of " + fileName + " as " + copy.getName() + ".");
        } catch (IOException e) {
            logger.error("Failed to keep a copy of " + fileName + ": " + e);
        }
    }

    private File findUnusedPreservedFile(String fileName) {
        File candidate = new File(FILE_PATH, fileName + PRESERVED_FILE_SUFFIX);
        for (int number = 1; candidate.exists(); number++) {
            candidate = new File(FILE_PATH, fileName + PRESERVED_FILE_SUFFIX + "." + number);
        }
        return candidate;
    }

    private void saveActivityRecords() {
        List<Map<String, String>> activityRecords = new ArrayList<>();
        for (ActivityRecord record : persistentData.getActivityRecords()){
            activityRecords.add(record.save());
        }

        writeOutFiles(activityRecords, ACTIVITY_RECORDS_FILE_NAME);
    }

    private void saveSessions() {
        List<Map<String, String>> sessions = new ArrayList<>();
        for (ActivityRecord record : persistentData.getActivityRecords()){
            for (Session session : record.getSessions()) {
                sessions.add(session.save());
            }
        }
        writeOutFiles(sessions, SESSIONS_FILE_NAME);
    }

    private void loadActivityRecords() {
        persistentData.getActivityRecords().clear();

        ArrayList<HashMap<String, String>> data = loadDataFromFilename(FILE_PATH + ACTIVITY_RECORDS_FILE_NAME);

        int successfullyLoaded = 0;
        int failedToLoad = 0;

        for (Map<String, String> activityRecordsData : data){
            try {
                ActivityRecord record = new ActivityRecord(activityRecordsData);
                
                // Validate the loaded record
                if (record.getPlayerUUID() == null) {
                    logger.log("WARNING: Skipping ActivityRecord with null player UUID");
                    failedToLoad++;
                    continue;
                }
                
                if (record.getHoursSpentNotIncludingTheCurrentSession() < 0) {
                    logger.log("WARNING: ActivityRecord for " + record.getPlayerUUID() + " has negative hours spent. Resetting to 0.");
                    record.setHoursSpent(0);
                }
                
                persistentData.addRecord(record);
                successfullyLoaded++;
            } catch (Exception e) {
                logger.error("Failed to load ActivityRecord: " + e.getMessage());
                failedToLoad++;
            }
        }
        
        logger.log("Loaded " + successfullyLoaded + " activity records successfully. " + failedToLoad + " records failed to load.");
    }

    private void loadSessions() {
        ArrayList<HashMap<String, String>> data = loadDataFromFilename(FILE_PATH + SESSIONS_FILE_NAME);

        if (data.size() == 0) {
            return;
        }

        int successfullyLoaded = 0;
        int failedToLoad = 0;

        for (Map<String, String> sessionsData : data) {
            try {
                Session session = new Session(sessionsData, logger);
                UUID playerUUID = session.getPlayerUUID();
                ActivityRecord record = persistentData.getActivityRecord(playerUUID);
                
                if (record == null) {
                    logger.error("Could not find ActivityRecord for player UUID " + playerUUID + " when loading session. Session will be skipped.");
                    failedToLoad++;
                    continue;
                }
                
                record.addSession(session);
                successfullyLoaded++;
            } catch (Exception e) {
                logger.error("Failed to load session: " + e.getMessage());
                failedToLoad++;
            }
        }
        
        logger.log("Loaded " + successfullyLoaded + " sessions successfully. " + failedToLoad + " sessions failed to load.");
    }

    private void validateAndRepairData() {
        logger.log("Validating and repairing data integrity...");
        int repairedRecords = 0;
        
        for (ActivityRecord record : persistentData.getActivityRecords()) {
            try {
                // Check if the most recent session exists
                record.getMostRecentSession();
            } catch (NoSessionException e) {
                // The most recent session reference is broken
                if (record.getSessions().size() > 0) {
                    // Repair by setting the last session as most recent
                    Session lastSession = record.getSessions().get(record.getSessions().size() - 1);
                    record.setMostRecentSession(lastSession);
                    logger.log("Repaired mostRecentSession reference for player " + record.getPlayerUUID());
                    repairedRecords++;
                } else {
                    logger.log("WARNING: ActivityRecord for player " + record.getPlayerUUID() + " has no sessions");
                }
            }
        }
        
        if (repairedRecords > 0) {
            logger.log("Repaired " + repairedRecords + " activity records with broken session references.");
        } else {
            logger.log("Data validation complete. No repairs needed.");
        }
    }

    /**
     * A clean shutdown ends every session before saving, so a session stored as
     * active belongs to a server that stopped without one, and its player is no
     * longer in it.
     */
    private void closeSessionsLeftActive() {
        int closedSessions = 0;

        for (ActivityRecord record : persistentData.getActivityRecords()) {
            for (Session session : record.getSessions()) {
                if (session.closeUnfinished()) {
                    closedSessions++;
                }
            }
        }

        if (closedSessions > 0) {
            logger.log("Closed " + closedSessions + " sessions left active by an unclean shutdown.");
        }
    }
}