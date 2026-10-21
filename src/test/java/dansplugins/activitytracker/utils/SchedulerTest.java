package dansplugins.activitytracker.utils;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.lang.reflect.Field;

import dansplugins.activitytracker.services.StorageService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for Scheduler.
 * Covers the hourly autosave: the timing it is registered with and what the scheduled task does when it runs.
 *
 * Bukkit.getScheduler() reads a static Server, so a mock Server is installed into Bukkit's
 * static field for each test and the previous value is restored afterwards.
 * ActivityTracker is final and cannot be mocked; Scheduler only passes it through to Bukkit,
 * so null stands in for it.
 *
 * @author Daniel McCoy Stephenson
 */
class SchedulerTest {

    private static final long TICKS_PER_HOUR = 60L * 60L * 20L;

    private Logger logger;
    private StorageService storageService;
    private BukkitScheduler bukkitScheduler;
    private Scheduler scheduler;
    private Server previousServer;

    @BeforeEach
    void setUp() throws Exception {
        logger = mock(Logger.class);
        storageService = mock(StorageService.class);
        bukkitScheduler = mock(BukkitScheduler.class);
        Server server = mock(Server.class);
        when(server.getScheduler()).thenReturn(bukkitScheduler);

        previousServer = Bukkit.getServer();
        setBukkitServer(server);

        scheduler = new Scheduler(logger, null, storageService);
    }

    @AfterEach
    void tearDown() throws Exception {
        setBukkitServer(previousServer);
    }

    @Test
    @DisplayName("Should register a repeating task that first runs after one hour and repeats hourly")
    void scheduleAutosave_registersHourlyRepeatingTask() {
        scheduler.scheduleAutosave();

        verify(bukkitScheduler).scheduleSyncRepeatingTask(any(), any(Runnable.class), eq(TICKS_PER_HOUR), eq(TICKS_PER_HOUR));
    }

    @Test
    @DisplayName("Should not save when the autosave is only scheduled")
    void scheduleAutosave_doesNotSaveImmediately() {
        scheduler.scheduleAutosave();

        verify(storageService, never()).save();
    }

    @Test
    @DisplayName("Should save once each time the scheduled task runs")
    void scheduledTask_savesEachTimeItRuns() {
        Runnable task = captureScheduledTask();

        task.run();
        verify(storageService, times(1)).save();

        task.run();
        verify(storageService, times(2)).save();
    }

    @Test
    @DisplayName("Should log when scheduling and each time the scheduled task runs")
    void scheduleAutosave_logsSchedulingAndEachSave() {
        Runnable task = captureScheduledTask();

        verify(logger).log("Scheduling hourly autosave.");
        verify(logger, never()).log("Saving. This will happen hourly.");

        task.run();
        verify(logger).log("Saving. This will happen hourly.");
    }

    private Runnable captureScheduledTask() {
        scheduler.scheduleAutosave();
        ArgumentCaptor<Runnable> captor = ArgumentCaptor.forClass(Runnable.class);
        verify(bukkitScheduler).scheduleSyncRepeatingTask((Plugin) any(), captor.capture(), anyLong(), anyLong());
        return captor.getValue();
    }

    private static void setBukkitServer(Server server) throws Exception {
        Field field = Bukkit.class.getDeclaredField("server");
        field.setAccessible(true);
        field.set(null, server);
    }
}
