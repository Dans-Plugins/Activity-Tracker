package dansplugins.activitytracker.eventhandlers;

import dansplugins.activitytracker.data.PersistentData;
import dansplugins.activitytracker.factories.SessionFactory;
import dansplugins.activitytracker.managers.ActivityRecordManager;
import dansplugins.activitytracker.objects.ActivityRecord;
import dansplugins.activitytracker.objects.Session;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinHandler implements Listener {

    @EventHandler()
    public void handle(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        boolean assignmentNeeded = ActivityRecordManager.getInstance().assignActivityRecordToPlayerIfNecessary(player);
        if (!assignmentNeeded) {
            ActivityRecord record = PersistentData.getInstance().getActivityRecord(player);
            Session newSession = SessionFactory.getInstance().createSession(player);
            record.getSessions().add(newSession); // TODO: replace with better method
            record.setMostRecentSession(newSession);
        }
    }

}
