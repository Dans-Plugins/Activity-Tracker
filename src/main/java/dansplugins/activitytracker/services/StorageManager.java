package dansplugins.activitytracker.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import dansplugins.activitytracker.ActivityTracker;
import dansplugins.activitytracker.data.PersistentData;
import dansplugins.activitytracker.objects.ActivityRecord;
import dansplugins.activitytracker.objects.Session;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class StorageManager implements IStorageService {

    private static StorageManager instance;

    private final static String FILE_PATH = "./plugins/ActivityTracker/";
    private final static String ACTIVITY_RECORDS_FILE_NAME = "activityRecords.json";
    private final static String SESSIONS_FILE_NAME = "sessions.json";

    private final static Type LIST_MAP_TYPE = new TypeToken<ArrayList<HashMap<String, String>>>(){}.getType();

    private Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private StorageManager() {

    }

    public static IStorageService getInstance() {
        if (instance == null) {
            instance = new StorageManager();
        }
        return instance;
    }

    @Override
    public void save() {
        saveActivityRecords();
        saveSessions();
        if (ConfigManager.getInstance().hasBeenAltered()) {
            ActivityTracker.getInstance().saveConfig();
        }
    }

    @Override
    public void load() {
        loadActivityRecords();
        loadSessions();
    }

    @Override
    public void writeOutFiles(List<Map<String, String>> saveData, String fileName) {
        try {
            File parentFolder = new File(FILE_PATH);
            parentFolder.mkdir();
            File file = new File(FILE_PATH, fileName);
            file.createNewFile();
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8);
            outputStreamWriter.write(gson.toJson(saveData));
            outputStreamWriter.close();
        } catch(IOException e) {
            System.out.println("ERROR: " + e.toString());
        }
    }

    @Override
    public ArrayList<HashMap<String, String>> loadDataFromFilename(String filename) {
        try{
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            JsonReader reader = new JsonReader(new InputStreamReader(new FileInputStream(filename), StandardCharsets.UTF_8));
            return gson.fromJson(reader, LIST_MAP_TYPE);
        } catch (FileNotFoundException e) {
            // Fail silently because this can actually happen in normal use
        }
        return new ArrayList<>();
    }

    private void saveActivityRecords() {
        List<Map<String, String>> activityRecords = new ArrayList<>();
        for (ActivityRecord record : PersistentData.getInstance().getActivityRecords()){
            activityRecords.add(record.save());
        }

        writeOutFiles(activityRecords, ACTIVITY_RECORDS_FILE_NAME);
    }

    private void saveSessions() {
        List<Map<String, String>> sessions = new ArrayList<>();
        for (ActivityRecord record : PersistentData.getInstance().getActivityRecords()){
            for (Session session : record.getSessions()) {
                sessions.add(session.save());
            }
        }
        writeOutFiles(sessions, SESSIONS_FILE_NAME);
    }

    private void loadActivityRecords() {
        PersistentData.getInstance().getActivityRecords().clear();

        ArrayList<HashMap<String, String>> data = loadDataFromFilename(FILE_PATH + ACTIVITY_RECORDS_FILE_NAME);

        for (Map<String, String> activityRecordsData : data){
            ActivityRecord record = new ActivityRecord(activityRecordsData);
            PersistentData.getInstance().addRecord(record);
        }
    }

    private void loadSessions() {
        ArrayList<HashMap<String, String>> data = loadDataFromFilename(FILE_PATH + SESSIONS_FILE_NAME);

        if (data.size() == 0) {
            return;
        }

        for (Map<String, String> sessionsData : data) {
            Session session = new Session(sessionsData);
            UUID playerUUID = session.getPlayerUUID();
            ActivityRecord record = PersistentData.getInstance().getActivityRecord(playerUUID);
            record.getSessions().add(session);
        }
    }



}