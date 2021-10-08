package dansplugins.activitytracker.commands;

import dansplugins.activitytracker.managers.ActivityRecordManager;
import dansplugins.activitytracker.objects.ActivityRecord;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;

public class TopCommand implements ICommand {

    @Override
    public boolean execute(CommandSender sender) {
        ArrayList<ActivityRecord> records = ActivityRecordManager.getInstance().getTopTenRecords();
        return false;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        // unused
        return false;
    }
}
