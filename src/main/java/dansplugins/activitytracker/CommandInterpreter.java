package dansplugins.activitytracker;

import dansplugins.activitytracker.commands.HelpCommand;
import dansplugins.activitytracker.commands.InfoCommand;
import dansplugins.activitytracker.commands.TopCommand;
import dansplugins.activitytracker.utils.ArgumentParser;
import dansplugins.activitytracker.utils.PermissionChecker;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class CommandInterpreter implements ICommandInterpreter {

    @Override
    public boolean interpretCommand(CommandSender sender, String label, String[] args) {
        if (label.equalsIgnoreCase("activitytracker") || label.equalsIgnoreCase("at")) {

            if (args.length == 0) {
                sender.sendMessage(ChatColor.AQUA + "Activity Tracker " + ActivityTracker.getInstance().getVersion());
                sender.sendMessage(ChatColor.AQUA + "Developer: DanTheTechMan");
                sender.sendMessage(ChatColor.AQUA + "Wiki: https://github.com/dmccoystephenson/Activity-Tracker/wiki");
                return false;
            }

            String secondaryLabel = args[0];
            String[] arguments = ArgumentParser.getInstance().dropFirstArgument(args);

            if (secondaryLabel.equalsIgnoreCase("help")) {
                if (!PermissionChecker.getInstance().checkPermission(sender, "at.help")) { return false; }
                HelpCommand command = new HelpCommand();
                return command.execute(sender);
            }

            if (secondaryLabel.equalsIgnoreCase("info")) {
                if (!PermissionChecker.getInstance().checkPermission(sender, "at.info")) { return false; }
                InfoCommand command = new InfoCommand();
                return command.execute(sender, arguments);
            }

            if (secondaryLabel.equalsIgnoreCase("at.top")) {
                if (!PermissionChecker.getInstance().checkPermission(sender, "at.top")) { return false; }
                TopCommand command = new TopCommand();
                return command.execute(sender);
            }

            sender.sendMessage(ChatColor.RED + "Activity Tracker doesn't recognize that command.");
        }
        return false;
    }

}