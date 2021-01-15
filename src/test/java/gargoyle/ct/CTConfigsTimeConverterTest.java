package gargoyle.ct;

import gargoyle.ct.config.CTConfigs;
import gargoyle.ct.config.CTStandardConfigs;
import gargoyle.ct.config.convert.impl.CTConfigsConverter;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CTConfigsTimeConverterTest {
    @Nullable
    private CTConfigsConverter converter;

    @BeforeEach
    public void setUp() {
    }

    @AfterEach
    public void tearDown() {
        converter = null;
    }

    @Test
    public void testConvert() {
        converter = new CTConfigsConverter();
        CTConfigs config = new CTConfigs(new CTStandardConfigs().getConfigs());
        assertAll(
                () -> assertEquals(config, converter.parse(converter.format(TimeUnit.MINUTES, config)), "conversion failed"),
                () -> assertEquals(config, converter.parse(converter.format(TimeUnit.HOURS, config)), "conversion failed"),
                () -> assertEquals(config, converter.parse(converter.format(TimeUnit.SECONDS, config)), "conversion failed")
        );
    }
}
