package gargoyle.ct;

import gargoyle.ct.config.CTConfig;
import gargoyle.ct.util.CTSerializationUtil;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static gargoyle.ct.config.CTStandardConfigs.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SerializationUtilsTest {
    private static final String STRING = "string";

    @Test
    public void testPipe() throws IOException {
        test(STRING);
        test(Color.BLACK);
        test(new CTConfig(TimeUnit.MINUTES, WHOLE_1H, BLOCK_10M, WARN_3M));
    }

    private void test(Object obj) throws IOException {
        assertEquals(obj, CTSerializationUtil.pipe(obj), String.format("%s piped (de)serialization wrong", obj));
    }
}
