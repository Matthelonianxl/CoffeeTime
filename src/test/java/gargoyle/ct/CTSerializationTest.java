package gargoyle.ct;

import gargoyle.ct.config.CTConfig;
import gargoyle.ct.config.CTConfigs;
import gargoyle.ct.config.CTStandardConfigs;
import gargoyle.ct.config.convert.impl.CTConfigsConverter;
import gargoyle.ct.util.CTSerializationUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static gargoyle.ct.config.CTStandardConfigs.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class CTSerializationTest {
    private CTConfigsConverter configsConverter;

    @BeforeEach
    public void setUp() {
    }

    @Test
    public void testConfigSerialization() throws IOException {
        CTConfig orig = new CTConfig(TimeUnit.MINUTES, WHOLE_1H, BLOCK_10M, WARN_3M);
        CTConfig restored = CTSerializationUtil.pipe(orig);
        assertEquals(orig, restored, "config serialization piping failed");
    }

    @Test
    public void testConfigsSerialization() throws IOException {
        CTConfigs orig = new CTStandardConfigs();
        CTConfigs restored = CTSerializationUtil.pipe(orig);
        assertEquals(orig, restored, "configs serialization piping failed");
    }

    @Test
    public void testLoadH() {
        configsConverter = new CTConfigsConverter();
        CTConfigs parsed = configsConverter.parse("60H/10H/3H\n30H/5H/3H\n120H/20H/3H\n"); //NON-NLS
        assertNotNull(parsed, "not parsed");
        assertNotNull(parsed.getConfig("3600/600"), "wrong parsing (with hours)");
    }

    @Test
    public void testLoadM() {
        configsConverter = new CTConfigsConverter();
        CTConfigs parsed = configsConverter.parse("60M/10M/3M\n30M/5M/3M\n120M/20M/3M\n"); //NON-NLS
        assertNotNull(parsed);
        assertNotNull(parsed.getConfig("60/10"), "wrong parsing (with minutes)");
    }

    @Test
    public void testLoadS() {
        configsConverter = new CTConfigsConverter();
        CTConfigs parsed = configsConverter.parse("3600S/600S/3S\n1800S/300S/3S\n120S/20S/3S\n"); //NON-NLS
        assertNotNull(parsed);
        assertNotNull(parsed.getConfig("60/10"), "wrong parsing (with seconds)");
    }
}
