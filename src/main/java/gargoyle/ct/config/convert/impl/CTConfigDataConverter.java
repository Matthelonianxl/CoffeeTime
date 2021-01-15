package gargoyle.ct.config.convert.impl;

import gargoyle.ct.config.convert.CTUnitConverter;
import gargoyle.ct.util.CTTimeUtil;
import gargoyle.ct.util.Defend;
import org.jetbrains.annotations.NotNull;

import java.text.MessageFormat;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class CTConfigDataConverter implements CTUnitConverter<long[]> {
    private static final String COMMENTS = "#;'";
    private static final String MSG_CANNOT_PARSE_LINE_0 = "Cannot parse line: {0}";
    private static final String MSG_CANNOT_PARSE_LINE_0_INVALID_TIME_UNIT_1 =
            "Cannot parse line: {0}, invalid time " + "unit {1}";
    private static final String MSG_CANNOT_PARSE_LINE_INVALID_TIME_UNIT_0 =
            "Cannot parse line, invalid time unit " + "{0}";
    private static final String MSG_COMMENTED_LINE_0 = "Commented line: {0}";
    private static final String MSG_EMPTY_LINE = "Empty line";
    private static final String PATTERN_FORMAT = "{0}{1}/{2}{3}/{4}{5}";
    private static final Pattern PATTERN_PARSE = Pattern.compile(
            "(?:([0-9]+)([a-zA-Z]+))/(?:([0-9]+)([a-zA-Z]+))[^a-zA-Z0-9]+(?:([0-9]+)([a-zA-Z]+))",
            Pattern.CASE_INSENSITIVE);
    private static final String UNIT_HOURS = "H";
    private static final String UNIT_MINUTES = "M";
    private static final String UNIT_SECONDS = "S";

    @NotNull
    @Override
    public String format(@NotNull TimeUnit unit, long... data) {
        String unitChar;
        switch (unit) {
            case HOURS:
                unitChar = UNIT_HOURS;
                break;
            case MINUTES:
                unitChar = UNIT_MINUTES;
                break;
            case SECONDS:
                unitChar = UNIT_SECONDS;
                break;
            case DAYS:
            case MILLISECONDS:
            case MICROSECONDS:
            case NANOSECONDS:
            default:
                throw new UnsupportedOperationException(MessageFormat.format(MSG_CANNOT_PARSE_LINE_INVALID_TIME_UNIT_0,
                        unit.name()));
        }
        return MessageFormat.format(PATTERN_FORMAT,
                CTTimeUtil.fromMillis(unit, data[0]),
                unitChar,
                CTTimeUtil.fromMillis(unit, data[1]),
                unitChar,
                CTTimeUtil.fromMillis(unit, data[2]),
                unitChar);
    }

    @NotNull
    @Override
    public long[] parse(@NotNull String data) {
        Defend.notEmptyTrimmed(data, MSG_EMPTY_LINE);
        String trimmedLine = data.trim();
        Defend.isFalse(COMMENTS.contains(trimmedLine.substring(0, 1)), MessageFormat.format(MSG_COMMENTED_LINE_0,
                data));
        long[] longs = new long[3];
        Matcher m = PATTERN_PARSE.matcher(trimmedLine);
        if (m.find()) {
            int groupCount = m.groupCount();
            Defend.equals(groupCount, 6, MessageFormat.format(MSG_CANNOT_PARSE_LINE_0, data));
            for (int g = 1; g <= groupCount; g += 2) {
                String q = m.group(g);
                String u = m.group(g + 1);
                TimeUnit unit;
                switch (u) {
                    case UNIT_HOURS:
                        unit = TimeUnit.HOURS;
                        break;
                    case UNIT_MINUTES:
                        unit = TimeUnit.MINUTES;
                        break;
                    case UNIT_SECONDS:
                        unit = TimeUnit.SECONDS;
                        break;
                    default:
                        throw new IllegalArgumentException(MessageFormat.format(
                                MSG_CANNOT_PARSE_LINE_0_INVALID_TIME_UNIT_1,
                                data,
                                u));
                }
                try {
                    longs[g / 2] = CTTimeUtil.toMillis(unit, Long.parseLong(q));
                } catch (NumberFormatException ex) {
                    throw new IllegalArgumentException(MessageFormat.format(MSG_CANNOT_PARSE_LINE_0, data), ex);
                }
            }
        } else {
            throw new IllegalArgumentException(data);
        }
        return longs;
    }
}
