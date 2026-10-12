package dansplugins.activitytracker.commands;

import org.bukkit.command.CommandSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

import dansplugins.activitytracker.data.PersistentData;
import dansplugins.activitytracker.objects.ActivityRecord;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for StatsCommand.
 * Characterizes the unique-player and total-login counts reported by /at stats.
 *
 * @author Daniel McCoy Stephenson
 */
class StatsCommandTest {

    private PersistentData persistentData;
    private CommandSender sender;
    private StatsCommand statsCommand;

    @BeforeEach
    void setUp() {
        persistentData = mock(PersistentData.class);
        sender = mock(CommandSender.class);
        statsCommand = new StatsCommand(persistentData);
    }

    @Test
    @DisplayName("Should report the number of activity records as unique players")
    void testExecute_ReportsUniquePlayers() {
        when(persistentData.getActivityRecords()).thenReturn(new ArrayList<>(Arrays.asList(
                mock(ActivityRecord.class), mock(ActivityRecord.class), mock(ActivityRecord.class))));
        when(persistentData.getTotalNumberOfLogins()).thenReturn(0);

        boolean result = statsCommand.execute(sender);

        assertTrue(result);
        verify(sender).sendMessage(contains("Unique Players: §a3"));
    }

    @Test
    @DisplayName("Should report the total number of logins")
    void testExecute_ReportsTotalLogins() {
        when(persistentData.getActivityRecords()).thenReturn(new ArrayList<ActivityRecord>());
        when(persistentData.getTotalNumberOfLogins()).thenReturn(42);

        boolean result = statsCommand.execute(sender);

        assertTrue(result);
        verify(sender).sendMessage(contains("Total Logins:   §a42"));
    }

    @Test
    @DisplayName("Should ignore arguments and report the same statistics")
    void testExecute_WithArguments_DelegatesToSenderOnlyOverload() {
        when(persistentData.getActivityRecords()).thenReturn(new ArrayList<ActivityRecord>());
        when(persistentData.getTotalNumberOfLogins()).thenReturn(7);

        boolean result = statsCommand.execute(sender, new String[]{"ignored"});

        assertTrue(result);
        verify(sender).sendMessage(contains("Unique Players: §a0"));
        verify(sender).sendMessage(contains("Total Logins:   §a7"));
    }
}
