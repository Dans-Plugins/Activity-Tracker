package dansplugins.activitytracker.eventhandlers;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import dansplugins.activitytracker.data.PersistentData;
import dansplugins.activitytracker.objects.ActivityRecord;
import dansplugins.activitytracker.objects.Session;
import dansplugins.activitytracker.utils.Logger;

/**
 * @author Daniel McCoy Stephenson
 */
public class QuitHandler implements Listener {

    @EventHandler()
    public void handle(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        ActivityRecord record = PersistentData.getInstance().getActivityRecord(player);
        Session currentSession = record.getMostRecentSession();
        Logger.getInstance().log(player.getName() + " has quit the server. Ending their session.");
        currentSession.endSession();
        double totalHoursSpent = record.getHoursSpentNotIncludingTheCurrentSession() + currentSession.getMinutesSpent() / 60;
        Logger.getInstance().log("Total hours spent on the server: " + totalHoursSpent);
        record.setHoursSpent(totalHoursSpent);
    }
}