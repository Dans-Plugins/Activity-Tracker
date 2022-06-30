package dansplugins.activitytracker.factories;

import dansplugins.activitytracker.objects.ActivityRecord;
import dansplugins.activitytracker.objects.Session;
import dansplugins.activitytracker.utils.Logger;
import org.bukkit.entity.Player;

/**
 * @author Daniel McCoy Stephenson
 */
public class ActivityRecordFactory {
    private final Logger logger;
    private final SessionFactory sessionFactory;

    public ActivityRecordFactory(Logger logger, SessionFactory sessionFactory) {
        this.logger = logger;
        this.sessionFactory = sessionFactory;
    }

    public ActivityRecord createActivityRecord(Player player) {
        logger.log("Creating activity record for " + player.getName());
        Session session = sessionFactory.createSession(player);
        ActivityRecord newRecord = new ActivityRecord(player.getUniqueId(), session);
        return newRecord;
    }
}