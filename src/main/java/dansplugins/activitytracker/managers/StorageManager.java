package dansplugins.activitytracker.managers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import dansplugins.activitytracker.data.PersistentData;
import dansplugins.activitytracker.objects.ActivityRecord;
import dansplugins.activitytracker.objects.ISession;
import dansplugins.activitytracker.objects.Session;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StorageManager {

    private static StorageManager instance;

    private final static String FILE_PATH = "./plugins/ActivityTracker/";
    private final static String ACTIVITY_RECORDS_FILE_NAME = "activityRecords.json";
    private final static String SESSIONS_FILE_NAME = "sessions.json";

    private final static Type LIST_MAP_TYPE = new TypeToken<ArrayList<HashMap<String, String>>>(){}.getType();

    private Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private StorageManager() {

    }

    public static StorageManager getInstance() {
        if (instance == null) {
            instance = new StorageManager();
        }
        return instance;
    }

    public void save() {
        saveActivityRecords();
        saveSessions();
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

    private void writeOutFiles(List<Map<String, String>> saveData, String fileName) {
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

    public void load() {
        loadActivityRecords();
        loadSessions();
    }

    private void loadActivityRecords() {
        // TODO: implement
    }

    private void loadSessions() {
        // TODO: implement
    }

    private ArrayList<HashMap<String, String>> loadDataFromFilename(String filename) {
        try{
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            JsonReader reader = new JsonReader(new InputStreamReader(new FileInputStream(filename), StandardCharsets.UTF_8));
            return gson.fromJson(reader, LIST_MAP_TYPE);
        } catch (FileNotFoundException e) {
            // Fail silently because this can actually happen in normal use
        }
        return new ArrayList<>();
    }

}