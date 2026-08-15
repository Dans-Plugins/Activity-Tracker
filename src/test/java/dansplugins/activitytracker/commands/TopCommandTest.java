package dansplugins.activitytracker.commands;

import org.bukkit.command.CommandSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import dansplugins.activitytracker.objects.ActivityRecord;
import dansplugins.activitytracker.services.ActivityRecordService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for TopCommand.
 * Covers the optional number argument added to /at top.
 *
 * @author Daniel McCoy Stephenson
 */
class TopCommandTest {

    private ActivityRecordService activityRecordService;
    private CommandSender sender;
    private TopCommand topCommand;

    @BeforeEach
    void setUp() {
        activityRecordService = mock(ActivityRecordService.class);
        sender = mock(CommandSender.class);
        when(activityRecordService.getTopRecords(anyInt())).thenReturn(new ArrayList<ActivityRecord>());
        topCommand = new TopCommand(activityRecordService);
    }

    @Test
    @DisplayName("Should request ten records when no argument is given")
    void testExecute_NoArguments_UsesDefaultOfTen() {
        topCommand.execute(sender, new String[]{});

        verify(activityRecordService).getTopRecords(10);
    }

    @Test
    @DisplayName("Should request ten records when the sender-only overload is used")
    void testExecute_SenderOnly_UsesDefaultOfTen() {
        topCommand.execute(sender);

        verify(activityRecordService).getTopRecords(10);
    }

    @Test
    @DisplayName("Should request the requested number of records when a valid number is given")
    void testExecute_ValidNumber_UsesRequestedCount() {
        topCommand.execute(sender, new String[]{"25"});

        verify(activityRecordService).getTopRecords(25);
    }

    @Test
    @DisplayName("Should accept the maximum allowed number of records")
    void testExecute_MaximumNumber_UsesRequestedCount() {
        topCommand.execute(sender, new String[]{"100"});

        verify(activityRecordService).getTopRecords(100);
    }

    @Test
    @DisplayName("Should reject a non-numeric argument")
    void testExecute_NonNumericArgument_IsRejected() {
        boolean result = topCommand.execute(sender, new String[]{"abc"});

        assertFalse(result);
        verify(sender).sendMessage(contains("Invalid number of players"));
        verify(activityRecordService, never()).getTopRecords(anyInt());
    }

    @Test
    @DisplayName("Should reject zero")
    void testExecute_Zero_IsRejected() {
        boolean result = topCommand.execute(sender, new String[]{"0"});

        assertFalse(result);
        verify(sender).sendMessage(contains("must be a positive number"));
        verify(activityRecordService, never()).getTopRecords(anyInt());
    }

    @Test
    @DisplayName("Should reject a negative number")
    void testExecute_NegativeNumber_IsRejected() {
        boolean result = topCommand.execute(sender, new String[]{"-5"});

        assertFalse(result);
        verify(sender).sendMessage(contains("must be a positive number"));
        verify(activityRecordService, never()).getTopRecords(anyInt());
    }

    @Test
    @DisplayName("Should reject a number above the maximum")
    void testExecute_NumberAboveMaximum_IsRejected() {
        boolean result = topCommand.execute(sender, new String[]{"101"});

        assertFalse(result);
        verify(sender).sendMessage(contains("at most 100 players"));
        verify(activityRecordService, never()).getTopRecords(anyInt());
    }
}
