package gargoyle.ct.log.jul;

import gargoyle.ct.log.Level;
import gargoyle.ct.log.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.MessageFormat;
import java.util.logging.LogRecord;

public class JULLogger implements Logger {

    private final java.util.logging.Logger logger;

    public JULLogger(String name) {
        logger = java.util.logging.Logger.getLogger(name);
    }

    @Override
    public void log(@NotNull Level level, @Nullable Throwable exception, @Nullable String pattern, Object... arguments) {
        LogRecord record = new LogRecord(getLevel(level),
                pattern == null ? exception == null ? null :
                        exception.getLocalizedMessage() :
                        MessageFormat.format(pattern, arguments));
        record.setThrown(exception);
        logger.log(record);
    }

    private java.util.logging.Level getLevel(Level level) {
        switch (level) {
            case DEBUG:
                return java.util.logging.Level.FINE;
            case INFO:
                return java.util.logging.Level.INFO;
            case WARNING:
                return java.util.logging.Level.WARNING;
            case ERROR:
                return java.util.logging.Level.SEVERE;
        }
        return java.util.logging.Level.ALL;
    }

    @Override
    public boolean isLoggable(@NotNull Level level) {
        return logger.isLoggable(getLevel(level));
    }
}
