package dansplugins.activitytracker.commands;

import org.bukkit.command.CommandSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for DefaultCommand.
 * Covers the at.default permission check on the no-argument /at path.
 *
 * @author Daniel McCoy Stephenson
 */
class DefaultCommandTest {

    private CommandSender sender;
    private DefaultCommand defaultCommand;

    @BeforeEach
    void setUp() {
        sender = mock(CommandSender.class);
        defaultCommand = new DefaultCommand("v2.0.0");
    }

    @Test
    @DisplayName("Should display plugin information when the sender has at.default")
    void testExecute_WithPermission_DisplaysInformation() {
        when(sender.hasPermission("at.default")).thenReturn(true);

        assertTrue(defaultCommand.execute(sender));

        verify(sender).sendMessage(contains("Activity Tracker"));
        verify(sender).sendMessage(contains("Daniel McCoy Stephenson"));
    }

    @Test
    @DisplayName("Should refuse and explain when the sender lacks at.default")
    void testExecute_WithoutPermission_IsRefused() {
        when(sender.hasPermission("at.default")).thenReturn(false);

        assertFalse(defaultCommand.execute(sender));

        verify(sender).sendMessage(contains("You don't have permission to do that."));
        verify(sender, never()).sendMessage(contains("Activity Tracker"));
    }

    @Test
    @DisplayName("Should apply the same permission check to the argument-taking overload")
    void testExecute_WithArguments_HonoursPermission() {
        when(sender.hasPermission("at.default")).thenReturn(false);

        assertFalse(defaultCommand.execute(sender, new String[]{}));

        verify(sender).sendMessage(contains("You don't have permission to do that."));
    }

    @Test
    @DisplayName("Should include the plugin version in the header")
    void testExecute_WithPermission_IncludesVersion() {
        when(sender.hasPermission("at.default")).thenReturn(true);

        defaultCommand.execute(sender);

        verify(sender).sendMessage(contains("v2.0.0"));
    }
}
