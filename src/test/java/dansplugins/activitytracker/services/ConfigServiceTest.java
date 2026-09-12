package dansplugins.activitytracker.services;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for ConfigService.
 * Covers the usage-reporting getters, which must read through to the jar's
 * bundled config.yml defaults on servers whose config.yml predates the block.
 *
 * ActivityTracker is final and cannot be mocked, so the configuration is
 * substituted by overriding {@link ConfigService#getConfig()}.
 */
class ConfigServiceTest {
    private FileConfiguration config;
    private ConfigService configService;

    @BeforeEach
    void setUp() {
        config = mock(FileConfiguration.class);
        configService = new ConfigService(null) {
            @Override
            public FileConfiguration getConfig() {
                return config;
            }
        };
    }

    @Test
    void usageReporting_readsThroughToTheBundledDefaultsWhenTheFileHasNoBlock() {
        // A server upgraded from before usage reporting has no usage-reporting
        // block in its config.yml. Bukkit's one-argument getters fall through
        // to the jar's defaults; the two-argument ones would return their
        // fallback and turn reporting off on every existing installation.
        when(config.getBoolean("usage-reporting.enabled")).thenReturn(true);
        when(config.getString("usage-reporting.endpoint")).thenReturn("https://trace.danielstephenson.dev");
        when(config.getString("usage-reporting.key")).thenReturn("bundled-key");

        assertTrue(configService.isUsageReportingEnabled());
        assertEquals("https://trace.danielstephenson.dev", configService.getUsageReportingEndpoint());
        assertEquals("bundled-key", configService.getUsageReportingKey());
        verify(config, never()).getString(eq("usage-reporting.key"), anyString());
        verify(config, never()).getString(eq("usage-reporting.endpoint"), anyString());
        verify(config, never()).getBoolean(eq("usage-reporting.enabled"), anyBoolean());
    }

    @Test
    void usageReporting_bundledDefaultsReachAnUpgradedConfigFile() throws Exception {
        // The same wiring JavaPlugin.reloadConfig() does: the on-disk file (here one
        // written by a version before usage reporting existed) is loaded, and the
        // jar's config.yml is registered as its defaults. Measured against the real
        // YamlConfiguration rather than a mock.
        YamlConfiguration onDisk = new YamlConfiguration();
        onDisk.loadFromString("version: v1.3.0\ndebugMode: false\nrestApiEnabled: false\n");
        try (InputStream bundled = getClass().getClassLoader().getResourceAsStream("config.yml")) {
            onDisk.setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(bundled, StandardCharsets.UTF_8)));
        }
        ConfigService upgraded = new ConfigService(null) {
            @Override
            public FileConfiguration getConfig() {
                return onDisk;
            }
        };

        assertTrue(upgraded.isUsageReportingEnabled());
        assertEquals("https://trace.danielstephenson.dev", upgraded.getUsageReportingEndpoint());
        assertEquals("KyNSegoT_nZHAj3gCynhLWV1KWwOwK1Fy4qHqt3PeGc", upgraded.getUsageReportingKey());
        // ...whereas the two-argument getter, which the code must not use, hides the default.
        assertEquals("", onDisk.getString("usage-reporting.key", ""));
    }

    @Test
    void usageReporting_isOffWithNoKeyAnywhere() {
        when(config.getString("usage-reporting.key")).thenReturn(null);
        when(config.getString("usage-reporting.endpoint")).thenReturn(null);

        assertEquals("", configService.getUsageReportingKey(), "no key anywhere must read as off, not as null");
        assertEquals("https://trace.danielstephenson.dev", configService.getUsageReportingEndpoint());
    }

    @Test
    void usageReporting_readsTheConfiguredValues() {
        when(config.getBoolean("usage-reporting.enabled")).thenReturn(false);
        when(config.getString("usage-reporting.endpoint")).thenReturn("http://localhost:8080");
        when(config.getString("usage-reporting.key")).thenReturn("abc");

        assertFalse(configService.isUsageReportingEnabled());
        assertEquals("http://localhost:8080", configService.getUsageReportingEndpoint());
        assertEquals("abc", configService.getUsageReportingKey());
    }
}
