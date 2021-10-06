package dansplugins.activitytracker.objects;

import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.UUID;

public interface IActivityRecord {
    UUID getPlayerUUID();
    ArrayList<Session> getSessions();
    Session getMostRecentSession();
    void setMostRecentSession(Session newSession);
    double getHoursSpent();
    void setHoursSpent(double number);
    Session getSession(int ID);
    void sendInfoToSender(CommandSender sender);
}