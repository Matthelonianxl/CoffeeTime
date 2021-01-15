package gargoyle.ct.log;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Logger {

    String ROOT_LOGGER_NAME = "";

    void log(@NotNull Level level, @Nullable Throwable exception, @Nullable String pattern, Object... arguments);

    boolean isLoggable(@NotNull Level level);

    default boolean isTraceEnabled() {
        return isLoggable(Level.TRACE);
    }

    default boolean isDebugEnabled() {
        return isLoggable(Level.DEBUG);
    }

    default boolean isInfoEnabled() {
        return isLoggable(Level.INFO);
    }

    default boolean isWarningEnabled() {
        return isLoggable(Level.WARNING);
    }

    default boolean isErrorEnabled() {
        return isLoggable(Level.ERROR);
    }

    default void trace(Throwable exception, String pattern, Object... arguments) {
        log(Level.TRACE, exception, pattern, arguments);
    }

    default void trace(String pattern, Object... arguments) {
        log(Level.TRACE, null, pattern, arguments);
    }

    default void debug(Throwable exception, String pattern, Object... arguments) {
        log(Level.DEBUG, exception, pattern, arguments);
    }

    default void debug(String pattern, Object... arguments) {
        log(Level.DEBUG, null, pattern, arguments);
    }

    default void error(Throwable exception, String pattern, Object... arguments) {
        log(Level.ERROR, exception, pattern, arguments);
    }

    default void error(String pattern, Object... arguments) {
        log(Level.ERROR, null, pattern, arguments);
    }

    default void info(Throwable exception, String pattern, Object... arguments) {
        log(Level.INFO, exception, pattern, arguments);
    }

    default void info(String pattern, Object... arguments) {
        log(Level.INFO, null, pattern, arguments);
    }

    default void log(@NotNull Level level, String pattern, Object... arguments) {
        log(level, null, pattern, arguments);
    }

    default void warn(Throwable exception, String pattern, Object... arguments) {
        log(Level.WARNING, exception, pattern, arguments);
    }

    default void warn(String pattern, Object... arguments) {
        log(Level.WARNING, null, pattern, arguments);
    }
}
