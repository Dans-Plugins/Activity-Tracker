package dansplugins.activitytracker.eventhandlers;

import dansplugins.activitytracker.utils.Logger;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import dansplugins.activitytracker.data.PersistentData;
import dansplugins.activitytracker.objects.ActivityRecord;
import dansplugins.activitytracker.objects.Session;

/**
 * @author Daniel McCoy Stephenson
 */
public class QuitHandler implements Listener {
    private final PersistentData persistentData;
    private final Logger logger;

    public QuitHandler(PersistentData persistentData, Logger logger) {
        this.persistentData = persistentData;
        this.logger = logger;
    }

    @EventHandler()
    public void handle(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        ActivityRecord record = persistentData.getActivityRecord(player);
        Session currentSession;
        try {
            currentSession = record.getMostRecentSession();
        }
        catch (NullPointerException e) {
            logger.log("The most recent session was null.");
            return;
        }
        logger.log(player.getName() + " has quit the server. Ending their session.");
        currentSession.endSession();
        double totalHoursSpent = record.getHoursSpentNotIncludingTheCurrentSession() + currentSession.getMinutesSpent() / 60;
        logger.log("Total hours spent on the server: " + totalHoursSpent);
        record.setHoursSpent(totalHoursSpent);
    }
}