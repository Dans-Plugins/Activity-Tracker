package dansplugins.activitytracker.utils;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Logger.
 * Covers that log() prints to stdout only when debug mode is enabled.
 *
 * ActivityTracker is final and cannot be mocked, so the debug flag is supplied directly
 * through the BooleanSupplier constructor. System.out is replaced with a buffer for each test
 * and the previous stream is restored afterwards.
 *
 * @author Daniel McCoy Stephenson
 */
class LoggerTest {

    private AtomicBoolean debugEnabled;
    private Logger logger;
    private PrintStream previousOut;
    private ByteArrayOutputStream output;

    @BeforeEach
    void setUp() {
        debugEnabled = new AtomicBoolean(false);
        logger = new Logger(debugEnabled::get);

        previousOut = System.out;
        output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output, true));
    }

    @AfterEach
    void tearDown() {
        System.setOut(previousOut);
    }

    @Test
    @DisplayName("log prints the prefixed message when debug mode is enabled")
    void logPrintsWhenDebugEnabled() {
        debugEnabled.set(true);

        logger.log("Saving data.");

        assertEquals("[Activity Tracker] Saving data." + System.lineSeparator(), output.toString());
    }

    @Test
    @DisplayName("log prints nothing when debug mode is disabled")
    void logPrintsNothingWhenDebugDisabled() {
        logger.log("Saving data.");

        assertEquals("", output.toString());
    }

    @Test
    @DisplayName("log reads debug mode on every call rather than once at construction")
    void logReadsDebugModeOnEachCall() {
        logger.log("first");
        debugEnabled.set(true);
        logger.log("second");

        assertEquals("[Activity Tracker] second" + System.lineSeparator(), output.toString());
    }
}
