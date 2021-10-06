package dansplugins.activitytracker.utils;

import java.util.UUID;

public interface IUUIDChecker {
    String findPlayerNameBasedOnUUID(UUID playerUUID);

    UUID findUUIDBasedOnPlayerName(String playerName);
}
