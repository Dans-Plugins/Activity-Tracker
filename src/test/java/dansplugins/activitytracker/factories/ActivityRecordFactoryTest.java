package dansplugins.activitytracker.factories;

import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import dansplugins.activitytracker.exceptions.NoSessionException;
import dansplugins.activitytracker.objects.ActivityRecord;
import dansplugins.activitytracker.objects.Session;
import dansplugins.activitytracker.utils.Logger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ActivityRecordFactory.
 * Covers creation of a new activity record seeded with the player's first session.
 *
 * @author Daniel McCoy Stephenson
 */
class ActivityRecordFactoryTest {

    private Logger logger;
    private SessionFactory sessionFactory;
    private ActivityRecordFactory activityRecordFactory;
    private Player player;
    private UUID playerUUID;

    @BeforeEach
    void setUp() {
        logger = mock(Logger.class);
        sessionFactory = mock(SessionFactory.class);
        activityRecordFactory = new ActivityRecordFactory(logger, sessionFactory);

        playerUUID = UUID.randomUUID();
        player = mock(Player.class);
        when(player.getName()).thenReturn("Steve");
        when(player.getUniqueId()).thenReturn(playerUUID);
    }

    @Test
    @DisplayName("Should create a record for the player's UUID seeded with the factory's session")
    void testCreateActivityRecord_SeedsWithNewSession() throws NoSessionException {
        Session session = new Session(logger, 42, playerUUID);
        when(sessionFactory.createSession(player)).thenReturn(session);

        ActivityRecord record = activityRecordFactory.createActivityRecord(player);

        assertEquals(playerUUID, record.getPlayerUUID());
        assertEquals(1, record.getSessions().size());
        assertSame(session, record.getSessions().get(0));
        assertSame(session, record.getMostRecentSession());
        assertEquals(0, record.getHoursSpentNotIncludingTheCurrentSession(), 0.001);
        verify(sessionFactory, times(1)).createSession(player);
    }

    @Test
    @DisplayName("Should log the player's name when creating a record")
    void testCreateActivityRecord_LogsPlayerName() {
        when(sessionFactory.createSession(player)).thenReturn(new Session(logger, 1, playerUUID));

        activityRecordFactory.createActivityRecord(player);

        verify(logger).log("Creating activity record for Steve");
    }
}
