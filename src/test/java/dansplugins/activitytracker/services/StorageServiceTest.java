package dansplugins.activitytracker.services;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import dansplugins.activitytracker.data.PersistentData;
import dansplugins.activitytracker.exceptions.NoSessionException;
import dansplugins.activitytracker.objects.ActivityRecord;
import dansplugins.activitytracker.objects.Session;
import dansplugins.activitytracker.utils.Logger;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeFalse;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for StorageService.
 * Covers the JSON save/load round trip and the validation and repair applied on load.
 *
 * StorageService reads and writes the fixed relative directory ./plugins/ActivityTracker/,
 * so these tests run only when ./plugins does not already exist and remove it afterwards.
 *
 * @author Daniel McCoy Stephenson
 */
class StorageServiceTest {

    private static final File PLUGINS_DIR = new File("./plugins");
    private static final File DATA_DIR = new File(PLUGINS_DIR, "ActivityTracker");
    private static final String ACTIVITY_RECORDS_FILE_NAME = "activityRecords.json";
    private static final String SESSIONS_FILE_NAME = "sessions.json";

    private Logger logger;
    private ConfigService configService;
    private PersistentData persistentData;
    private StorageService storageService;
    private boolean createdPluginsDir;

    @BeforeEach
    void setUp() {
        createdPluginsDir = false;
        assumeFalse(PLUGINS_DIR.exists(), "./plugins already exists; refusing to overwrite it");
        assertTrue(PLUGINS_DIR.mkdir());
        createdPluginsDir = true;

        logger = mock(Logger.class);
        configService = mock(ConfigService.class);
        when(configService.hasBeenAltered()).thenReturn(false);
        persistentData = new PersistentData(logger);
        // ActivityTracker is final and cannot be mocked; it is only used when the config has been altered.
        storageService = new StorageService(configService, null, persistentData, logger);
    }

    @AfterEach
    void tearDown() {
        // @AfterEach still runs when the assumption aborts a test, so only delete what setUp created
        if (createdPluginsDir) {
            deleteRecursively(PLUGINS_DIR);
        }
    }

    @Test
    @DisplayName("Should return an empty list when the file does not exist")
    void testLoadDataFromFilename_MissingFile() {
        ArrayList<HashMap<String, String>> result =
                storageService.loadDataFromFilename(new File(DATA_DIR, "missing.json").getPath());

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should parse a JSON array of string maps")
    void testLoadDataFromFilename_ParsesListOfMaps(@TempDir Path tempDir) throws IOException {
        Path file = tempDir.resolve("data.json");
        Files.write(file, "[{\"a\":\"1\",\"b\":\"two\"},{\"c\":\"3\"}]".getBytes(StandardCharsets.UTF_8));

        ArrayList<HashMap<String, String>> result = storageService.loadDataFromFilename(file.toString());

        assertEquals(2, result.size());
        assertEquals("1", result.get(0).get("a"));
        assertEquals("two", result.get(0).get("b"));
        assertEquals("3", result.get(1).get("c"));
    }

    @Test
    @DisplayName("Should create the data directory and both JSON files on save")
    void testSave_WritesFiles() {
        persistentData.addRecord(recordWithEndedSession(UUID.randomUUID(), 1, 2.5));

        storageService.save();

        assertTrue(new File(DATA_DIR, ACTIVITY_RECORDS_FILE_NAME).isFile());
        assertTrue(new File(DATA_DIR, SESSIONS_FILE_NAME).isFile());
        verify(configService).hasBeenAltered();
    }

    @Test
    @DisplayName("Should write nothing when the ./plugins parent directory is missing")
    void testSave_NoPluginsDirectory() {
        assertTrue(PLUGINS_DIR.delete());
        persistentData.addRecord(recordWithEndedSession(UUID.randomUUID(), 1, 2.5));

        storageService.save();

        // writeOutFiles uses File.mkdir (not mkdirs), so the data directory is not created
        assertFalse(DATA_DIR.exists());
    }

    @Test
    @DisplayName("Should restore records, hours and sessions after a save/load round trip")
    void testSaveThenLoad_RoundTrip() throws NoSessionException {
        UUID first = UUID.randomUUID();
        UUID second = UUID.randomUUID();
        ActivityRecord firstRecord = recordWithEndedSession(first, 1, 4.25);
        Session secondSession = new Session(logger, 2, first);
        firstRecord.addSession(secondSession);
        firstRecord.setMostRecentSession(secondSession);
        persistentData.addRecord(firstRecord);
        persistentData.addRecord(recordWithEndedSession(second, 3, 1.0));

        storageService.save();
        persistentData.getActivityRecords().clear();
        storageService.load();

        assertEquals(2, persistentData.getActivityRecords().size());

        ActivityRecord loadedFirst = persistentData.getActivityRecord(first);
        assertNotNull(loadedFirst);
        assertEquals(4.25, loadedFirst.getHoursSpentNotIncludingTheCurrentSession(), 0.001);
        assertEquals(2, loadedFirst.getSessions().size());
        assertEquals(2, loadedFirst.getMostRecentSession().getID());
        assertTrue(loadedFirst.getMostRecentSession().isActive());
        assertFalse(loadedFirst.getSession(1).isActive());
        assertNotNull(loadedFirst.getSession(1).getLogoutDate());

        ActivityRecord loadedSecond = persistentData.getActivityRecord(second);
        assertNotNull(loadedSecond);
        assertEquals(1.0, loadedSecond.getHoursSpentNotIncludingTheCurrentSession(), 0.001);
        assertEquals(1, loadedSecond.getSessions().size());

        verify(logger).log("Loaded 2 activity records successfully. 0 records failed to load.");
        verify(logger).log("Loaded 3 sessions successfully. 0 sessions failed to load.");
    }

    @Test
    @DisplayName("Should replace in-memory records with the stored ones on load")
    void testLoad_ClearsExistingRecords() {
        UUID stored = UUID.randomUUID();
        UUID inMemoryOnly = UUID.randomUUID();
        persistentData.addRecord(recordWithEndedSession(stored, 1, 1.0));
        storageService.save();
        persistentData.addRecord(recordWithEndedSession(inMemoryOnly, 2, 1.0));

        storageService.load();

        assertEquals(1, persistentData.getActivityRecords().size());
        assertNotNull(persistentData.getActivityRecord(stored));
        assertNull(persistentData.getActivityRecord(inMemoryOnly));
    }

    @Test
    @DisplayName("Should load nothing when no data files exist")
    void testLoad_NoFiles() {
        storageService.load();

        assertTrue(persistentData.getActivityRecords().isEmpty());
        verify(logger).log("Loaded 0 activity records successfully. 0 records failed to load.");
        verify(logger).log("Data validation complete. No repairs needed.");
    }

    @Test
    @DisplayName("Should reset negative stored hours to zero and log a warning")
    void testLoad_NegativeHoursReset() {
        UUID playerUUID = UUID.randomUUID();
        Map<String, String> recordData = recordWithEndedSession(playerUUID, 1, 0).save();
        recordData.put("hoursSpent", "-3.5");
        storageService.writeOutFiles(Collections.singletonList(recordData), ACTIVITY_RECORDS_FILE_NAME);

        storageService.load();

        ActivityRecord loaded = persistentData.getActivityRecord(playerUUID);
        assertNotNull(loaded);
        assertEquals(0, loaded.getHoursSpentNotIncludingTheCurrentSession(), 0.001);
        verify(logger).log("WARNING: ActivityRecord for " + playerUUID + " has negative hours spent. Resetting to 0.");
    }

    @Test
    @DisplayName("Should skip a stored record without a player UUID and count it as failed")
    void testLoad_RecordWithoutUUIDSkipped() {
        UUID validUUID = UUID.randomUUID();
        Map<String, String> valid = recordWithEndedSession(validUUID, 1, 1.0).save();
        Map<String, String> invalid = recordWithEndedSession(UUID.randomUUID(), 2, 1.0).save();
        invalid.remove("playerUUID");
        List<Map<String, String>> data = new ArrayList<>();
        data.add(invalid);
        data.add(valid);
        storageService.writeOutFiles(data, ACTIVITY_RECORDS_FILE_NAME);

        storageService.load();

        // UUID.fromString(null) throws, so the record is rejected by the catch block
        // rather than by the null-UUID check that follows construction
        assertEquals(1, persistentData.getActivityRecords().size());
        assertNotNull(persistentData.getActivityRecord(validUUID));
        verify(logger).log(contains("ERROR: Failed to load ActivityRecord"));
        verify(logger, never()).log("WARNING: Skipping ActivityRecord with null player UUID");
        verify(logger).log("Loaded 1 activity records successfully. 1 records failed to load.");
    }

    @Test
    @DisplayName("Should skip sessions whose player has no stored record")
    void testLoad_OrphanSessionSkipped() {
        UUID owner = UUID.randomUUID();
        UUID orphanOwner = UUID.randomUUID();
        ActivityRecord record = recordWithEndedSession(owner, 1, 1.0);
        storageService.writeOutFiles(Collections.singletonList(record.save()), ACTIVITY_RECORDS_FILE_NAME);
        List<Map<String, String>> sessions = new ArrayList<>();
        sessions.add(record.getSessions().get(0).save());
        sessions.add(new Session(logger, 7, orphanOwner).save());
        storageService.writeOutFiles(sessions, SESSIONS_FILE_NAME);

        storageService.load();

        assertEquals(1, persistentData.getActivityRecord(owner).getSessions().size());
        assertNull(persistentData.getActivityRecord(orphanOwner));
        verify(logger).log("WARNING: Could not find ActivityRecord for player UUID " + orphanOwner
                + " when loading session. Session will be skipped.");
        verify(logger).log("Loaded 1 sessions successfully. 1 sessions failed to load.");
    }

    @Test
    @DisplayName("Should skip a malformed session and keep loading the rest")
    void testLoad_MalformedSessionSkipped() {
        UUID owner = UUID.randomUUID();
        ActivityRecord record = recordWithEndedSession(owner, 1, 1.0);
        storageService.writeOutFiles(Collections.singletonList(record.save()), ACTIVITY_RECORDS_FILE_NAME);
        Map<String, String> malformed = record.getSessions().get(0).save();
        malformed.put("loginDate", "\"not-a-date\"");
        List<Map<String, String>> sessions = new ArrayList<>();
        sessions.add(malformed);
        sessions.add(record.getSessions().get(0).save());
        storageService.writeOutFiles(sessions, SESSIONS_FILE_NAME);

        storageService.load();

        assertEquals(1, persistentData.getActivityRecord(owner).getSessions().size());
        verify(logger).log(contains("ERROR: Failed to load session"));
        verify(logger).log("Loaded 1 sessions successfully. 1 sessions failed to load.");
    }

    @Test
    @DisplayName("Should resolve a dangling mostRecentSessionID to the last session without logging a repair")
    void testLoad_DanglingMostRecentSessionID() throws NoSessionException {
        UUID playerUUID = UUID.randomUUID();
        ActivityRecord record = recordWithEndedSession(playerUUID, 1, 1.0);
        record.addSession(new Session(logger, 2, playerUUID));
        Map<String, String> recordData = record.save();
        recordData.put("mostRecentSessionID", "99");
        storageService.writeOutFiles(Collections.singletonList(recordData), ACTIVITY_RECORDS_FILE_NAME);
        List<Map<String, String>> sessions = new ArrayList<>();
        for (Session session : record.getSessions()) {
            sessions.add(session.save());
        }
        storageService.writeOutFiles(sessions, SESSIONS_FILE_NAME);

        storageService.load();

        // ActivityRecord.getMostRecentSession recovers on its own, so validateAndRepairData
        // never reaches its repair branch for a record that has sessions
        assertEquals(2, persistentData.getActivityRecord(playerUUID).getMostRecentSession().getID());
        verify(logger).log("Data validation complete. No repairs needed.");
        verify(logger, never()).log("Repaired mostRecentSessionID reference for player " + playerUUID);
    }

    @Test
    @DisplayName("Should warn about a stored record that has no sessions")
    void testLoad_RecordWithoutSessions() {
        UUID playerUUID = UUID.randomUUID();
        storageService.writeOutFiles(
                Collections.singletonList(recordWithEndedSession(playerUUID, 1, 1.0).save()),
                ACTIVITY_RECORDS_FILE_NAME);

        storageService.load();

        ActivityRecord loaded = persistentData.getActivityRecord(playerUUID);
        assertNotNull(loaded);
        assertTrue(loaded.getSessions().isEmpty());
        verify(logger).log("WARNING: ActivityRecord for player " + playerUUID + " has no sessions");
    }

    private ActivityRecord recordWithEndedSession(UUID playerUUID, int sessionID, double hours) {
        Session session = new Session(logger, sessionID, playerUUID);
        session.endSession();
        ActivityRecord record = new ActivityRecord(playerUUID, session);
        record.setHoursSpent(hours);
        return record;
    }

    private static void deleteRecursively(File file) {
        File[] children = file.listFiles();
        if (children != null) {
            for (File child : children) {
                deleteRecursively(child);
            }
        }
        file.delete();
    }
}
