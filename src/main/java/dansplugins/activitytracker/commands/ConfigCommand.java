package dansplugins.activitytracker.commands;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import dansplugins.activitytracker.services.ConfigService;
import preponderous.ponder.minecraft.bukkit.abs.AbstractPluginCommand;

/**
 * @author Daniel McCoy Stephenson
 */
public class ConfigCommand extends AbstractPluginCommand {
    private final ConfigService configService;

    public ConfigCommand(ConfigService configService) {
        super(new ArrayList<>(Arrays.asList("config")), new ArrayList<>(Arrays.asList("at.config")));
        this.configService = configService;
    }

    @Override
    public boolean execute(CommandSender sender) {
        sender.sendMessage(ChatColor.RED + "Sub-commands: show, set");
        return false;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (args[0].equalsIgnoreCase("show")) {
            configService.sendConfigList(sender);
            return true;
        }
        else if (args[0].equalsIgnoreCase("set")) {
            if (args.length < 3) {
                sender.sendMessage(ChatColor.RED + "Usage: /c config set (option) (value)");
                return false;
            }
            String option = args[1];

            String value = args[2];

            configService.setConfigOption(option, value, sender);
            return true;
        }
        else {
            sender.sendMessage(ChatColor.RED + "Sub-commands: show, set");
            return false;
        }
    }
}