package dansplugins.activitytracker.data;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import dansplugins.activitytracker.objects.ActivityRecord;
import dansplugins.activitytracker.objects.Session;
import dansplugins.activitytracker.utils.Logger;

/**
 * @author Daniel McCoy Stephenson
 */
public class PersistentData {
    private final Logger logger;

    private final ArrayList<ActivityRecord> activityRecords = new ArrayList<>();

    public PersistentData(Logger logger) {
        this.logger = logger;
    }

    public ArrayList<ActivityRecord> getActivityRecords() {
        return activityRecords;
    }

    public ActivityRecord getActivityRecord(Player player) {
        return getActivityRecord(player.getUniqueId());
    }

    public ActivityRecord getActivityRecord(UUID playerUUID) {
        for (ActivityRecord record : activityRecords) {
            if (record.getPlayerUUID().equals(playerUUID)) {
                return record;
            }
        }
        return null;
    }

    public void addRecord(ActivityRecord recordToAdd) {
        if (!activityRecords.contains(recordToAdd)) {
            activityRecords.add(recordToAdd);
        }
    }

    public void removeRecord(ActivityRecord recordToRemove) {
        activityRecords.remove(recordToRemove);
    }

    public Session getSession(int sessionID) {
        for (ActivityRecord record : activityRecords) {
            Session session = record.getSession(sessionID);
            if (session != null) {
                return session;
            }
        }
        return null;
    }

    public void endCurrentSessions() {
        logger.log("Ending current sessions.");
        for (Player player : Bukkit.getOnlinePlayers()) {
            ActivityRecord record = getActivityRecord(player);
            try {
                Session currentSession = record.getMostRecentSession();
                currentSession.endSession();
                double totalHoursSpent = record.getHoursSpentNotIncludingTheCurrentSession() + currentSession.getMinutesSpent() / 60;
                logger.log(player.getName() + "'s session ended. Total hours spent on the server: " + totalHoursSpent);
                record.setHoursSpent(totalHoursSpent);
            }
            catch (NullPointerException e) {
                logger.log("No session found for " + player.getName() + ".");
            }
        }
    }

    public int getTotalNumberOfLogins() {
        int count = 0;
        for (ActivityRecord record : activityRecords) {
            count += record.getSessions().size();
        }
        return count;
    }
}