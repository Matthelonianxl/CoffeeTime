package gargoyle.ct;

import gargoyle.ct.config.CTConfig;
import gargoyle.ct.config.convert.impl.CTConfigConverter;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static gargoyle.ct.config.CTStandardConfigs.BLOCK_10M;
import static gargoyle.ct.config.CTStandardConfigs.WARN_3M;
import static gargoyle.ct.config.CTStandardConfigs.WHOLE_1H;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CTConfigTimeConverterTest {
    @Nullable
    private CTConfigConverter converter;

    @BeforeEach
    public void setUp() {
    }

    @AfterEach
    public void tearDown() {
        converter = null;
    }

    @Test
    public void testConvert() {
        converter = new CTConfigConverter();
        CTConfig config = new CTConfig(TimeUnit.MINUTES, WHOLE_1H, BLOCK_10M, WARN_3M);
        assertAll(
                () -> assertEquals(config, converter.parse(converter.format(TimeUnit.MINUTES, config)), "conversion failed"),
                () -> assertEquals(config, converter.parse(converter.format(TimeUnit.HOURS, config)), "conversion failed"),
                () -> assertEquals(config, converter.parse(converter.format(TimeUnit.SECONDS, config)), "conversion failed")
        );
    }
}
