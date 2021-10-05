package dansplugins.activitytracker.eventhandlers;

import dansplugins.activitytracker.data.PersistentData;
import dansplugins.activitytracker.objects.ActivityRecord;
import dansplugins.activitytracker.objects.ISession;
import dansplugins.activitytracker.utils.Logger;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class QuitHandler implements Listener {

    @EventHandler()
    public void handle(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        ActivityRecord record = PersistentData.getInstance().getActivityRecord(player);
        ISession currentSession = record.getMostRecentSession();
        Logger.getInstance().log(player.getName() + " has quit the server. Ending their session.");
        currentSession.endSession();
    }

}