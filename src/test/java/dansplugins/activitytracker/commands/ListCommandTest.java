package dansplugins.activitytracker.commands;

import org.bukkit.command.CommandSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

import dansplugins.activitytracker.data.PersistentData;
import dansplugins.activitytracker.objects.ActivityRecord;
import dansplugins.activitytracker.objects.Session;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for ListCommand.
 * Covers the paths of /at list that find no session to show; listing a
 * session resolves its player name through Ponder's UUIDChecker, which
 * needs a running server.
 *
 * @author Daniel McCoy Stephenson
 */
class ListCommandTest {

    private PersistentData persistentData;
    private CommandSender sender;
    private ListCommand listCommand;

    @BeforeEach
    void setUp() {
        persistentData = mock(PersistentData.class);
        sender = mock(CommandSender.class);
        listCommand = new ListCommand(persistentData);
    }

    @Test
    @DisplayName("Should report no sessions when there are no activity records")
    void testExecute_NoRecords_ReportsNoSessions() {
        when(persistentData.getActivityRecords()).thenReturn(new ArrayList<ActivityRecord>());

        boolean result = listCommand.execute(sender);

        assertTrue(result);
        verify(sender).sendMessage(contains("Recent Sessions (0)"));
        verify(sender).sendMessage(contains("No sessions found."));
    }

    @Test
    @DisplayName("Should skip null records and records without sessions")
    void testExecute_NullRecordsAndSessionLists_AreSkipped() {
        ActivityRecord withoutSessions = mock(ActivityRecord.class);
        when(withoutSessions.getSessions()).thenReturn(null);
        when(persistentData.getActivityRecords()).thenReturn(new ArrayList<>(Arrays.asList(null, withoutSessions)));

        boolean result = listCommand.execute(sender);

        assertTrue(result);
        verify(sender).sendMessage(contains("No sessions found."));
    }

    @Test
    @DisplayName("Should skip null sessions and sessions without a login date")
    void testExecute_SessionsWithoutLoginDate_AreSkipped() {
        Session withoutLoginDate = mock(Session.class);
        when(withoutLoginDate.getLoginDate()).thenReturn(null);
        ActivityRecord record = mock(ActivityRecord.class);
        when(record.getSessions()).thenReturn(new ArrayList<>(Arrays.asList(null, withoutLoginDate)));
        when(persistentData.getActivityRecords()).thenReturn(new ArrayList<>(Arrays.asList(record)));

        boolean result = listCommand.execute(sender);

        assertTrue(result);
        verify(sender).sendMessage(contains("Recent Sessions (0)"));
        verify(sender).sendMessage(contains("No sessions found."));
    }

    @Test
    @DisplayName("Should ignore arguments and report the same sessions")
    void testExecute_WithArguments_DelegatesToSenderOnlyOverload() {
        when(persistentData.getActivityRecords()).thenReturn(new ArrayList<ActivityRecord>());

        boolean result = listCommand.execute(sender, new String[]{"ignored"});

        assertTrue(result);
        verify(sender).sendMessage(contains("No sessions found."));
    }
}
