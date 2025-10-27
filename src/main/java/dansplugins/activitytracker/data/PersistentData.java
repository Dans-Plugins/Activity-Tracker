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
        if (playerUUID == null) {
            logger.log("WARNING: Attempted to get ActivityRecord with null UUID");
            return null;
        }
        
        for (ActivityRecord record : activityRecords) {
            if (record.getPlayerUUID().equals(playerUUID)) {
                return record;
            }
        }
        return null;
    }

    public void addRecord(ActivityRecord recordToAdd) {
        if (recordToAdd == null) {
            logger.log("WARNING: Attempted to add null ActivityRecord");
            return;
        }
        
        if (recordToAdd.getPlayerUUID() == null) {
            logger.log("WARNING: Attempted to add ActivityRecord with null player UUID");
            return;
        }
        
        // Check if a record for this player already exists
        ActivityRecord existing = getActivityRecord(recordToAdd.getPlayerUUID());
        if (existing != null) {
            logger.log("WARNING: ActivityRecord for player " + recordToAdd.getPlayerUUID() + " already exists. Not adding duplicate.");
            return;
        }
        
        activityRecords.add(recordToAdd);
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
            if (record == null) {
                logger.log("WARNING: No activity record found for online player " + player.getName());
                continue;
            }
            
            try {
                Session currentSession = record.getMostRecentSession();
                if (currentSession.isActive()) {
                    currentSession.endSession();
                    double totalHoursSpent = record.getHoursSpentNotIncludingTheCurrentSession() + currentSession.getMinutesSpent() / 60;
                    logger.log(player.getName() + "'s session ended. Total hours spent on the server: " + totalHoursSpent);
                    record.setHoursSpent(totalHoursSpent);
                } else {
                    logger.log("Session for " + player.getName() + " was already ended.");
                }
            }
            catch (NullPointerException e) {
                logger.log("ERROR: No session found for " + player.getName() + ": " + e.getMessage());
            }
            catch (Exception e) {
                logger.log("ERROR: Failed to end session for " + player.getName() + ": " + e.getMessage());
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