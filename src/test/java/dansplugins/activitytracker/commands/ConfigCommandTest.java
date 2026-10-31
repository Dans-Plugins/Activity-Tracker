package dansplugins.activitytracker.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import dansplugins.activitytracker.services.ConfigService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

/**
 * Unit tests for ConfigCommand.
 * Characterizes how /at config routes its show and set sub-commands to ConfigService.
 *
 * @author Daniel McCoy Stephenson
 */
class ConfigCommandTest {

    private ConfigService configService;
    private CommandSender sender;
    private ConfigCommand configCommand;

    @BeforeEach
    void setUp() {
        configService = mock(ConfigService.class);
        sender = mock(CommandSender.class);
        configCommand = new ConfigCommand(configService);
    }

    @Test
    @DisplayName("Should list the sub-commands when none is given")
    void testExecute_NoArguments_SendsSubCommands() {
        boolean result = configCommand.execute(sender);

        assertFalse(result);
        verify(sender).sendMessage(contains("Sub-commands: show, set"));
        verifyNoInteractions(configService);
    }

    @Test
    @DisplayName("Should send the config list for show, ignoring case")
    void testExecute_Show_SendsConfigList() {
        boolean result = configCommand.execute(sender, new String[]{"SHOW"});

        assertTrue(result);
        verify(configService).sendConfigList(sender);
    }

    @Test
    @DisplayName("Should pass the option and value of set to ConfigService")
    void testExecute_SetWithOptionAndValue_SetsOption() {
        boolean result = configCommand.execute(sender, new String[]{"set", "debugMode", "true"});

        assertTrue(result);
        verify(configService).setConfigOption("debugMode", "true", sender);
    }

    @Test
    @DisplayName("Should pass every word of a multi-word value to ConfigService")
    void testExecute_SetWithMultiWordValue_JoinsValue() {
        boolean result = configCommand.execute(sender,
                new String[]{"set", "discordWebhookJoinMessage", "Welcome", "back", "{player}!"});

        assertTrue(result);
        verify(configService).setConfigOption("discordWebhookJoinMessage", "Welcome back {player}!", sender);
    }

    @Test
    @DisplayName("Should send usage when set is missing its value")
    void testExecute_SetWithoutValue_SendsUsage() {
        boolean result = configCommand.execute(sender, new String[]{"set", "debugMode"});

        assertFalse(result);
        verify(sender).sendMessage(ChatColor.RED + "Usage: /at config set (option) (value)");
        verify(configService, never()).setConfigOption(anyString(), anyString(), any(CommandSender.class));
    }

    @Test
    @DisplayName("Should list the sub-commands for an unknown sub-command")
    void testExecute_UnknownSubCommand_SendsSubCommands() {
        boolean result = configCommand.execute(sender, new String[]{"reset"});

        assertFalse(result);
        verify(sender).sendMessage(contains("Sub-commands: show, set"));
        verifyNoInteractions(configService);
    }
}
