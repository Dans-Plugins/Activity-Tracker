package dansplugins.activitytracker.eventhandlers;

import dansplugins.activitytracker.data.PersistentData;
import dansplugins.activitytracker.factories.SessionFactory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import dansplugins.activitytracker.objects.ActivityRecord;
import dansplugins.activitytracker.objects.Session;
import dansplugins.activitytracker.services.ActivityRecordService;

/**
 * @author Daniel McCoy Stephenson
 */
public class JoinHandler implements Listener {
    private final ActivityRecordService activityRecordService;
    private final PersistentData persistentData;
    private final SessionFactory sessionFactory;

    public JoinHandler(ActivityRecordService activityRecordService, PersistentData persistentData, SessionFactory sessionFactory) {
        this.activityRecordService = activityRecordService;
        this.persistentData = persistentData;
        this.sessionFactory = sessionFactory;
    }

    @EventHandler()
    public void handle(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        boolean assignmentNeeded = activityRecordService.assignActivityRecordToPlayerIfNecessary(player);
        if (!assignmentNeeded) {
            ActivityRecord record = persistentData.getActivityRecord(player);
            if (record == null) {
                // This should not happen, but handle it gracefully
                activityRecordService.assignActivityRecordToPlayerIfNecessary(player);
                return;
            }
            
            Session newSession = sessionFactory.createSession(player);
            record.getSessions().add(newSession);
            record.setMostRecentSession(newSession);
        }
    }
}