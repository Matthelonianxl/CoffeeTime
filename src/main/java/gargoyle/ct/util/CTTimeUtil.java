package gargoyle.ct.util;

import org.jetbrains.annotations.NotNull;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("StaticMethodOnlyUsedInOneClass")
public final class CTTimeUtil {
    private static final String HH_MM_SS = "HH:mm:ss";
    private static final String MM = "mm";
    private static final String MM_SS = "mm:ss";
    private static final String SS = "ss";

    private CTTimeUtil() {
    }

    public static long convert(TimeUnit unitTo, TimeUnit unitFrom, long cnt) {
        return unitTo.convert(unitFrom.toMillis(cnt), TimeUnit.MILLISECONDS);
    }

    public static long currentTimeMillis() {
        return new Date().getTime();
    }

    public static String formatHHMMSS(long currentMillis) {
        return format(HH_MM_SS, currentMillis);
    }

    private static String format(@NotNull String format, long currentMillis) {
        return Instant.ofEpochMilli(currentMillis).atOffset(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern(format));
    }

    public static String formatMM(long currentMillis) {
        return format(MM, currentMillis);
    }

    public static String formatMMSS(long currentMillis) {
        return format(MM_SS, currentMillis);
    }

    public static String formatSS(long currentMillis) {
        return format(SS, currentMillis);
    }

    public static boolean isBetween(long currentMillis, long startMillis, long endMillis) {
        return currentMillis >= startMillis && currentMillis <= endMillis;
    }

    public static boolean isBetween(@NotNull TimeUnit unit, long current, long start, long end) {
        return toMillis(unit, current) >= toMillis(unit, start) && toMillis(unit, current) <= toMillis(unit, end);
    }

    public static long toMillis(TimeUnit unit, long duration) {
        return unit.toMillis(duration);
    }

    public static boolean isInPeriod(@NotNull TimeUnit unit, long currentMillis, int period, int delay) {
        return fromMillis(unit, currentMillis) % period < delay;
    }

    public static long fromMillis(TimeUnit unit, long millis) {
        return unit.convert(millis, TimeUnit.MILLISECONDS);
    }

    public static long make() {
        return LocalDateTime.now().withNano(0).toInstant(ZoneOffset.UTC).toEpochMilli();
    }

    public static long parseHHMMSS(String string) {
        String[] pair = string.split(":");
        return make(Integer.parseInt(pair[0]), Integer.parseInt(pair[1]), Integer.parseInt(pair[2]));
    }

    public static long make(int hours, int minutes, int seconds) {
        return LocalDateTime.of(LocalDate.now(), LocalTime.of(hours, minutes, seconds))
                .withNano(0)
                .toInstant(ZoneOffset.UTC)
                .toEpochMilli();
    }

    public static long timeElapsedFrom(@NotNull TimeUnit unit, long currentMillis, long begin) {
        return fromMillis(unit, timeElapsedFrom(currentMillis, begin));
    }

    private static long timeElapsedFrom(long currentMillis, long begin) {
        return currentMillis - begin;
    }

    public static long timeRemainsTo(@NotNull TimeUnit unit, long currentMillis, long end) {
        return fromMillis(unit, timeRemainsTo(currentMillis, end));
    }

    public static long timeRemainsTo(long currentMillis, long end) {
        return end - currentMillis;
    }

    public static long toBase(long startMillis, long currentMillis, long baseMillis) {
        long low = downTo(currentMillis, baseMillis);
        long high = upTo(currentMillis, baseMillis);
        long ret = startMillis;
        while (ret <= low) {
            ret += baseMillis;
        }
        while (ret >= high) {
            ret -= baseMillis;
        }
        return ret;
    }

    public static long downTo(long currentMillis, long baseMillis) {
        return currentMillis / baseMillis * baseMillis;
    }

    public static long upTo(long currentMillis, long baseMillis) {
        return downTo(currentMillis, baseMillis) + baseMillis;
    }
}
