package dansplugins.activitytracker.commands;

import dansplugins.activitytracker.services.LocalConfigService;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import preponderous.ponder.minecraft.bukkit.abs.AbstractPluginCommand;

import java.util.ArrayList;
import java.util.Arrays;

public class ConfigCommand extends AbstractPluginCommand {

    public ConfigCommand() {
        super(new ArrayList<>(Arrays.asList("config")), new ArrayList<>(Arrays.asList("at.config")));
    }

    @Override
    public boolean execute(CommandSender sender) {
        sender.sendMessage(ChatColor.RED + "Sub-commands: show, set");
        return false;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (args[0].equalsIgnoreCase("show")) {
            LocalConfigService.getInstance().sendConfigList(sender);
            return true;
        }
        else if (args[0].equalsIgnoreCase("set")) {
            if (args.length < 3) {
                sender.sendMessage(ChatColor.RED + "Usage: /c config set (option) (value)");
                return false;
            }
            String option = args[1];

            String value = args[2];

            LocalConfigService.getInstance().setConfigOption(option, value, sender);
            return true;
        }
        else {
            sender.sendMessage(ChatColor.RED + "Sub-commands: show, set");
            return false;
        }
    }
}