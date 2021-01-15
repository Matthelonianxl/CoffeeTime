package gargoyle.ct;

import gargoyle.ct.task.helper.CTTimeHelper;
import gargoyle.ct.task.helper.impl.CTTimeHelperImpl;
import gargoyle.ct.util.CTTimeUtil;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CTTimeHelperTest {
    @Nullable
    private CTTimeHelper helper;

    @BeforeEach
    public void setUp() {
        helper = new CTTimeHelperImpl();
    }

    @AfterEach
    public void tearDown() {
        helper = null;
    }

    @Test
    public void testFakeTime() {
        helper = new CTTimeHelperImpl();
        long fakeTime = CTTimeUtil.currentTimeMillis();
        helper.setFakeTime(fakeTime);
        assertEquals(fakeTime, helper.getFakeTime(), "wrong fake time");
        long expected = CTTimeUtil.currentTimeMillis();
        long actual = helper.currentTimeMillis();
        assertTrue(expected - actual <= 0, "no fake");
    }
}
