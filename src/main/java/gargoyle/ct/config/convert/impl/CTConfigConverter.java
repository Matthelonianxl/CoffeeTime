package gargoyle.ct.config.convert.impl;

import gargoyle.ct.config.CTConfig;
import gargoyle.ct.config.convert.CTUnitConverter;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

public final class CTConfigConverter implements CTUnitConverter<CTConfig> {
    private final CTConfigDataConverter configDataConverter = new CTConfigDataConverter();

    @NotNull
    @Override
    public String format(TimeUnit unit, @NotNull CTConfig data) {
        return configDataConverter.format(TimeUnit.MINUTES, data.getWhole(), data.getBlock(), data.getWarn());
    }

    @NotNull
    @Override
    public CTConfig parse(@NotNull String data) {
        long[] parsed = configDataConverter.parse(data);
        return new CTConfig(parsed[0], parsed[1], parsed[2]);
    }
}
