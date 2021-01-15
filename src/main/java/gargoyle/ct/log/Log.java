package gargoyle.ct.log;

import gargoyle.ct.messages.util.UTF8Control;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;

public final class Log {
    private static final String LOCATION_ERRORS = "messages/errors";

    private Log() {
    }

    public static void debug(Throwable exception, String pattern, Object... arguments) {
        _log(Level.DEBUG, exception, pattern, arguments);
    }

    private static void _log(@NotNull Level level, @Nullable Throwable exception, @Nullable String pattern, Object... arguments) {
        StackTraceElement ste = findCaller();
        if (ste != null) {
            Logger logger = LoggerFactory.getLogger(ste.getClassName());
            if (logger.isLoggable(level)) {
                logger.log(level, exception, pattern, arguments);
            }
        }
    }

    private static ResourceBundle getResourceBundle() {
        return ResourceBundle.getBundle(LOCATION_ERRORS, Locale.getDefault(), UTF8Control.getControl());
    }

    @Nullable
    private static StackTraceElement findCaller() {
        StackTraceElement[] trace = Thread.currentThread().getStackTrace();
        int length = trace.length;
        for (int i = 1; i < length; i++) {
            StackTraceElement ste = trace[i];
            if (!Objects.equals(Log.class.getName(), ste.getClassName())) {
                return ste;
            }
        }
        return null;
    }

    public static void trace(String pattern, Object... arguments) {
        _log(Level.TRACE, null, pattern, arguments);
    }

    public static void debug(String pattern, Object... arguments) {
        _log(Level.DEBUG, null, pattern, arguments);
    }

    public static void error(Throwable exception, String pattern, Object... arguments) {
        _log(Level.ERROR, exception, pattern, arguments);
    }

    public static void error(String pattern, Object... arguments) {
        _log(Level.ERROR, null, pattern, arguments);
    }

    public static void info(Throwable exception, String pattern, Object... arguments) {
        _log(Level.INFO, exception, pattern, arguments);
    }

    public static void info(String pattern, Object... arguments) {
        _log(Level.INFO, null, pattern, arguments);
    }

    public static void log(@NotNull Level level, Throwable exception, String pattern, Object... arguments) {
        _log(level, exception, pattern, arguments);
    }

    public static void log(@NotNull Level level, String pattern, Object... arguments) {
        _log(level, null, pattern, arguments);
    }

    public static void warn(Throwable exception, String pattern, Object... arguments) {
        _log(Level.WARNING, exception, pattern, arguments);
    }

    public static void warn(String pattern, Object... arguments) {
        _log(Level.WARNING, null, pattern, arguments);
    }
}
