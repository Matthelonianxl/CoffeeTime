package gargoyle.ct.config.convert.impl;

import gargoyle.ct.config.convert.CTUnitConverter;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

public final class CTConfigsDataConverter implements CTUnitConverter<String[]> {
    private static final String ENV_LINE_SEPARATOR = "line.separator";
    private static final String NEWLINE = System.getProperty(ENV_LINE_SEPARATOR, "\n");

    @NotNull
    @Override
    public String format(TimeUnit unit, @NotNull String... data) {
        StringBuilder ret = new StringBuilder();
        for (String string : data) {
            ret.append(string).append(NEWLINE);
        }
        return ret.toString();
    }

    @NotNull
    @Override
    public String[] parse(String data) {
        return data.split(NEWLINE);
    }
}
