package dansplugins.activitytracker.commands;

import org.bukkit.command.CommandSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import dansplugins.activitytracker.data.PersistentData;
import dansplugins.activitytracker.objects.ActivityRecord;
import dansplugins.activitytracker.services.ActivityRecordService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for AverageCommand.
 * Covers the argument validation of /at average that runs before the
 * player name is resolved through Ponder's UUIDChecker.
 *
 * @author Daniel McCoy Stephenson
 */
class AverageCommandTest {

    private PersistentData persistentData;
    private ActivityRecordService activityRecordService;
    private CommandSender sender;
    private AverageCommand averageCommand;

    @BeforeEach
    void setUp() {
        persistentData = mock(PersistentData.class);
        activityRecordService = mock(ActivityRecordService.class);
        sender = mock(CommandSender.class);
        averageCommand = new AverageCommand(persistentData, activityRecordService);
    }

    @Test
    @DisplayName("Should send usage to a non-player sender")
    void testExecute_NonPlayerSender_SendsUsage() {
        boolean result = averageCommand.execute(sender);

        assertFalse(result);
        verify(sender).sendMessage(contains("Usage: /at average [playerName] [days]"));
    }

    @Test
    @DisplayName("Should send usage to a non-player sender when no arguments are given")
    void testExecute_NoArguments_DelegatesToSenderOnlyOverload() {
        boolean result = averageCommand.execute(sender, new String[]{});

        assertFalse(result);
        verify(sender).sendMessage(contains("Usage: /at average [playerName] [days]"));
    }

    @Test
    @DisplayName("Should reject a non-numeric number of days")
    void testExecute_NonNumericDays_IsRejected() {
        boolean result = averageCommand.execute(sender, new String[]{"Steve", "abc"});

        assertFalse(result);
        verify(sender).sendMessage(contains("Invalid number of days."));
        verifyNoCalculation();
    }

    @Test
    @DisplayName("Should reject zero days")
    void testExecute_ZeroDays_IsRejected() {
        boolean result = averageCommand.execute(sender, new String[]{"Steve", "0"});

        assertFalse(result);
        verify(sender).sendMessage(contains("Days must be a positive number."));
        verifyNoCalculation();
    }

    @Test
    @DisplayName("Should reject a negative number of days")
    void testExecute_NegativeDays_IsRejected() {
        boolean result = averageCommand.execute(sender, new String[]{"Steve", "-3"});

        assertFalse(result);
        verify(sender).sendMessage(contains("Days must be a positive number."));
        verifyNoCalculation();
    }

    private void verifyNoCalculation() {
        verify(activityRecordService, never()).calculateAverageDailyActivity(any(ActivityRecord.class), anyInt());
        verify(activityRecordService, never()).calculateTotalHoursInPeriod(any(ActivityRecord.class), anyInt());
    }
}
