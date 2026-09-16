package dansplugins.activitytracker;

import dansplugins.activitytracker.trace.TraceClient;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The one console line printed on every startup: it names what is sent, where, and every way to
 * turn it off, or says why reporting is off.
 */
class UsageReportingNoticeTest {

    @Test
    void saysWhatIsSentAndWhereToTurnItOff() {
        String on = ActivityTracker.usageReportingNotice("ActivityTracker", null);

        assertTrue(on.startsWith("Usage reporting is on: ActivityTracker sends its name, version and command names to https://trace.danielstephenson.dev"), on);
        assertTrue(on.contains("usage-reporting.enabled: false"), on);
        assertTrue(on.contains("plugins/trace/config.yml"), on);
        assertTrue(on.endsWith("Details: https://github.com/Stephenson-Software/trace#usage-reporting"), on);
    }

    @Test
    void saysWhyReportingIsOff() {
        assertEquals("Usage reporting is off (config.yml).",
                ActivityTracker.usageReportingNotice("ActivityTracker", TraceClient.REASON_CONFIG));
        assertEquals("Usage reporting is off (server-wide config: plugins/trace/config.yml).",
                ActivityTracker.usageReportingNotice("ActivityTracker", TraceClient.REASON_SERVER_WIDE));
        assertEquals("Usage reporting is off (environment).",
                ActivityTracker.usageReportingNotice("ActivityTracker", TraceClient.REASON_ENVIRONMENT));
    }
}
