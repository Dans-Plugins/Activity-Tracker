package dansplugins.activitytracker.commands;

import org.bukkit.command.CommandSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for HelpCommand.
 * Characterizes the command list sent by /at help.
 *
 * @author Daniel McCoy Stephenson
 */
class HelpCommandTest {

    private CommandSender sender;
    private HelpCommand helpCommand;

    @BeforeEach
    void setUp() {
        sender = mock(CommandSender.class);
        helpCommand = new HelpCommand();
    }

    @Test
    @DisplayName("Should list every /at sub-command")
    void testExecute_ListsEverySubCommand() {
        boolean result = helpCommand.execute(sender);

        assertTrue(result);
        verify(sender).sendMessage(contains("/at help §7"));
        verify(sender).sendMessage(contains("/at info §7"));
        verify(sender).sendMessage(contains("/at info (playerName) §7"));
        verify(sender).sendMessage(contains("/at list §7"));
        verify(sender).sendMessage(contains("/at top §7"));
        verify(sender).sendMessage(contains("/at top (number) §7"));
        verify(sender).sendMessage(contains("/at stats §7"));
        verify(sender).sendMessage(contains("/at average [player] [days] §7"));
        verify(sender).sendMessage(contains("/at config §7"));
    }

    @Test
    @DisplayName("Should send a blank line, header, nine command lines and footer")
    void testExecute_SendsTwelveLines() {
        helpCommand.execute(sender);

        verify(sender, times(12)).sendMessage(anyString());
        verify(sender).sendMessage(contains(" ─ Commands"));
    }

    @Test
    @DisplayName("Should ignore arguments and send the same command list")
    void testExecute_WithArguments_DelegatesToSenderOnlyOverload() {
        boolean result = helpCommand.execute(sender, new String[]{"ignored"});

        assertTrue(result);
        verify(sender, times(12)).sendMessage(anyString());
        verify(sender).sendMessage(contains("/at config §7"));
    }
}
