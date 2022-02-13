package dansplugins.activitytracker.commands;

import dansplugins.activitytracker.services.ConfigManager;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class ConfigCommand implements ICommand {

    @Override
    public boolean execute(CommandSender sender) {
        // unused
        return false;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Sub-commands: show, set");
            return false;
        }

        if (args[0].equalsIgnoreCase("show")) {
            ConfigManager.getInstance().sendConfigList(sender);
            return true;
        }
        else if (args[0].equalsIgnoreCase("set")) {
            if (args.length < 3) {
                sender.sendMessage(ChatColor.RED + "Usage: /c config set (option) (value)");
                return false;
            }
            String option = args[1];

            String value = args[2];

            ConfigManager.getInstance().setConfigOption(option, value, sender);
            return true;
        }
        else {
            sender.sendMessage(ChatColor.RED + "Sub-commands: show, set");
            return false;
        }
    }

}
