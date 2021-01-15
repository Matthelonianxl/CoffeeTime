package gargoyle.ct;

import gargoyle.ct.config.CTConfig;
import gargoyle.ct.task.impl.CTTask;
import gargoyle.ct.util.CTTimeUtil;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.text.MessageFormat;
import java.util.concurrent.TimeUnit;

import static gargoyle.ct.config.CTStandardConfigs.BLOCK_10M;
import static gargoyle.ct.config.CTStandardConfigs.WARN_3M;
import static gargoyle.ct.config.CTStandardConfigs.WHOLE_1H;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SuppressWarnings("MagicNumber")
public class CTTaskTest {
    private static final String TEST_BLOCKED_FAILED_0_1 = "testBlocked failed: {0} {1}";
    private static final String TEST_SLEEPING_FAILED_0_1 = "testSleeping failed: {0} {1}";
    private static final String TEST_WARN_FAILED_0_1 = "testWarn failed: {0} {1}";
    private long actual;
    @Nullable
    private CTTask task;

    @BeforeEach
    public void setUp() {
    }

    @AfterEach
    public void tearDown() {
        task = null;
    }

    @Test
    public void testBlockedFalse() {
        task = new CTTask();
        long current = CTTimeUtil.make(10, 45, 10);
        assertFalse(task.isBlocked(current), "should not block");
        task.setConfig(new CTConfig(TimeUnit.MINUTES, WHOLE_1H, BLOCK_10M, WARN_3M));
        long started = CTTimeUtil.downTo(current, CTTimeUtil.toMillis(TimeUnit.HOURS, 1));
        task.setStarted(started);
        assertFalse(task.isBlocked(current), //NON-NLS
                MessageFormat.format(TEST_BLOCKED_FAILED_0_1, CTTimeUtil.formatHHMMSS(current),
                        CTTimeUtil.formatHHMMSS(started)));
    }

    @Test
    public void testBlockedTrue() {
        task = new CTTask();
        task.setConfig(new CTConfig(TimeUnit.MINUTES, WHOLE_1H, BLOCK_10M, WARN_3M));
        long current = CTTimeUtil.make(10, 55, 10);
        long started = CTTimeUtil.downTo(current, CTTimeUtil.toMillis(TimeUnit.HOURS, 1));
        task.setStarted(started);
        assertTrue(task.isBlocked(current), //NON-NLS
                MessageFormat.format(TEST_BLOCKED_FAILED_0_1, CTTimeUtil.formatHHMMSS(current),
                        CTTimeUtil.formatHHMMSS(started)));
    }

    @Test
    public void testSleepingFalse() {
        task = new CTTask();
        long current = CTTimeUtil.make(10, 51, 10);
        assertFalse(task.isWarn(current), "no warnings here");
        task.setConfig(new CTConfig(TimeUnit.MINUTES, WHOLE_1H, BLOCK_10M, WARN_3M));
        long started = CTTimeUtil.downTo(current, CTTimeUtil.toMillis(TimeUnit.HOURS, 1));
        task.setStarted(started);
        assertFalse(task.isSleeping(current), //NON-NLS
                MessageFormat.format(TEST_SLEEPING_FAILED_0_1, CTTimeUtil.formatHHMMSS(current),
                        CTTimeUtil.formatHHMMSS(started)));
    }

    @Test
    public void testSleepingTrue() {
        task = new CTTask();
        task.setConfig(new CTConfig(TimeUnit.MINUTES, WHOLE_1H, BLOCK_10M, WARN_3M));
        long current = CTTimeUtil.make(10, 30, 10);
        long started = CTTimeUtil.downTo(current, CTTimeUtil.toMillis(TimeUnit.HOURS, 1));
        task.setStarted(started);
        assertTrue(task.isSleeping(current), //NON-NLS
                MessageFormat.format(TEST_SLEEPING_FAILED_0_1, CTTimeUtil.formatHHMMSS(current),
                        CTTimeUtil.formatHHMMSS(started)));
    }

    @Test
    public void testStarted() {
        task = new CTTask();
        long started = CTTimeUtil.make();
        task.setStarted(started);
        actual = task.getStarted();
        assertEquals(started, actual, "dumb");
    }

    @Test
    public void testStartedUnits() {
        task = new CTTask();
        long started = CTTimeUtil.make();
        TimeUnit unit = TimeUnit.SECONDS;
        task.setStarted(unit, started);
        actual = task.getStarted(unit);
        assertEquals(started, actual, " start in sec failed");
    }

    @Test
    public void testWarnFalse() {
        task = new CTTask();
        task.setConfig(new CTConfig(TimeUnit.MINUTES, WHOLE_1H, BLOCK_10M, WARN_3M));
        long current = CTTimeUtil.make(10, 45, 10);
        long started = CTTimeUtil.downTo(current, CTTimeUtil.toMillis(TimeUnit.HOURS, 1));
        task.setStarted(started);
        assertFalse(task.isWarn(current), //NON-NLS
                MessageFormat.format(TEST_WARN_FAILED_0_1, CTTimeUtil.formatHHMMSS(current),
                        CTTimeUtil.formatHHMMSS(started)));
    }

    @Test
    public void testWarnTrue() {
        task = new CTTask();
        task.setConfig(new CTConfig(TimeUnit.MINUTES, WHOLE_1H, BLOCK_10M, WARN_3M));
        long current = CTTimeUtil.make(10, 49, 10);
        long started = CTTimeUtil.downTo(current, CTTimeUtil.toMillis(TimeUnit.HOURS, 1));
        task.setStarted(started);
        assertTrue(task.isWarn(current), //NON-NLS
                MessageFormat.format(TEST_WARN_FAILED_0_1, CTTimeUtil.formatHHMMSS(current),
                        CTTimeUtil.formatHHMMSS(started)));
    }
}
