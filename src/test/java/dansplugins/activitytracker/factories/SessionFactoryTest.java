package dansplugins.activitytracker.factories;

import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import dansplugins.activitytracker.data.PersistentData;
import dansplugins.activitytracker.objects.Session;
import dansplugins.activitytracker.utils.Logger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

/**
 * Unit tests for SessionFactory.
 * Covers session creation and the bounded retry used to pick an unused session ID.
 *
 * @author Daniel McCoy Stephenson
 */
class SessionFactoryTest {

    private Logger logger;
    private PersistentData persistentData;
    private SessionFactory sessionFactory;

    @BeforeEach
    void setUp() {
        logger = mock(Logger.class);
        persistentData = mock(PersistentData.class);
        sessionFactory = new SessionFactory(logger, persistentData);
    }

    @Test
    @DisplayName("Should create an active session for the given player UUID")
    void testCreateSession_ByUUID() {
        UUID playerUUID = UUID.randomUUID();

        Session session = sessionFactory.createSession(playerUUID);

        assertEquals(playerUUID, session.getPlayerUUID());
        assertTrue(session.isActive());
        assertNotNull(session.getLoginDate());
        assertNull(session.getLogoutDate());
    }

    @Test
    @DisplayName("Should assign a session ID in the range [0, 100000)")
    void testCreateSession_IDInRange() {
        for (int i = 0; i < 50; i++) {
            int id = sessionFactory.createSession(UUID.randomUUID()).getID();
            assertTrue(id >= 0 && id < 100000, "Session ID out of range: " + id);
        }
    }

    @Test
    @DisplayName("Should check the ID only once when the first candidate is free")
    void testCreateSession_FreeIDCheckedOnce() {
        when(persistentData.getSession(anyInt())).thenReturn(null);

        sessionFactory.createSession(UUID.randomUUID());

        verify(persistentData, times(1)).getSession(anyInt());
    }

    @Test
    @DisplayName("Should return an ID that is not taken when a free one is found after collisions")
    void testCreateSession_RetriesUntilFreeID() {
        Session existing = mock(Session.class);
        when(persistentData.getSession(anyInt())).thenReturn(existing, existing, null);

        sessionFactory.createSession(UUID.randomUUID());

        verify(persistentData, times(3)).getSession(anyInt());
    }

    @Test
    @DisplayName("Should give up after 26 attempts and still return a session when every ID is taken")
    void testCreateSession_GivesUpAfterMaxAttempts() {
        when(persistentData.getSession(anyInt())).thenReturn(mock(Session.class));

        Session session = sessionFactory.createSession(UUID.randomUUID());

        assertNotNull(session);
        verify(persistentData, times(26)).getSession(anyInt());
    }

    @Test
    @DisplayName("Should use the player's UUID and log the player's name when created from a Player")
    void testCreateSession_ByPlayer() {
        UUID playerUUID = UUID.randomUUID();
        Player player = mock(Player.class);
        when(player.getName()).thenReturn("Steve");
        when(player.getUniqueId()).thenReturn(playerUUID);

        Session session = sessionFactory.createSession(player);

        assertEquals(playerUUID, session.getPlayerUUID());
        verify(logger).log("Creating session for Steve");
    }
}
