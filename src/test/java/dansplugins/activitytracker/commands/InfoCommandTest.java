package dansplugins.activitytracker.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

import dansplugins.activitytracker.data.PersistentData;
import dansplugins.activitytracker.objects.ActivityRecord;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for InfoCommand.
 * Covers the sender-only overload of /at info; the named-player overload
 * resolves names through Ponder's UUIDChecker, which needs a running server.
 *
 * @author Daniel McCoy Stephenson
 */
class InfoCommandTest {

    private PersistentData persistentData;
    private InfoCommand infoCommand;

    @BeforeEach
    void setUp() {
        persistentData = mock(PersistentData.class);
        infoCommand = new InfoCommand(persistentData);
    }

    @Test
    @DisplayName("Should send usage to a non-player sender")
    void testExecute_NonPlayerSender_SendsUsage() {
        CommandSender console = mock(CommandSender.class);

        boolean result = infoCommand.execute(console);

        assertFalse(result);
        verify(console).sendMessage(contains("Usage: /at info (playerName)"));
    }

    @Test
    @DisplayName("Should report a missing record for a player without one")
    void testExecute_PlayerWithoutRecord_IsRejected() {
        UUID uuid = UUID.randomUUID();
        Player player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(uuid);
        when(persistentData.getActivityRecord(uuid)).thenReturn(null);

        boolean result = infoCommand.execute(player);

        assertFalse(result);
        verify(player).sendMessage(contains("You don't have an activity record."));
        verify(persistentData, never()).getPlayerRank(any(UUID.class));
    }

    @Test
    @DisplayName("Should send the player's own record with rank and total players")
    void testExecute_PlayerWithRecord_SendsInfoWithRankAndTotal() {
        UUID uuid = UUID.randomUUID();
        Player player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(uuid);
        ActivityRecord record = mock(ActivityRecord.class);
        when(persistentData.getActivityRecord(uuid)).thenReturn(record);
        when(persistentData.getPlayerRank(uuid)).thenReturn(2);
        when(persistentData.getActivityRecords()).thenReturn(new ArrayList<>(Arrays.asList(
                record, mock(ActivityRecord.class), mock(ActivityRecord.class), mock(ActivityRecord.class))));

        boolean result = infoCommand.execute(player);

        assertTrue(result);
        verify(record).sendInfoToSender(player, 2, 4);
    }
}
