package dansplugins.activitytracker.managers;

public class StorageManager {

    private static StorageManager instance;

    private final static String FILE_PATH = "./plugins/ActivityTracker/";
    private final static String ACTIVITY_RECORDS_FILE_NAME = "activityRecords.json";
    private final static String SESSIONS_FILE_NAME = "sessions.json";

    private StorageManager() {

    }

    public static StorageManager getInstance() {
        if (instance == null) {
            instance = new StorageManager();
        }
        return instance;
    }

    public void save() {
        // TODO: implement
    }

    public void load() {
        // TODO: implement
    }

}