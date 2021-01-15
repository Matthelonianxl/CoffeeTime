package gargoyle.ct.log;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public final class LoggerFactory {

    private static final Map<String, Logger> cache = new HashMap<>();
    private static ILoggerFactory loggerFactory;

    private LoggerFactory() {
    }

    public static Logger getLogger(String name) {
        return cache.computeIfAbsent(name, (logger) -> getLoggerFactory().getLogger(logger));
    }

    public static Logger getLogger(Class<?> clazz) {
        return getLogger(clazz.getName());
    }

    @NotNull
    private static synchronized ILoggerFactory getLoggerFactory() {
        if (loggerFactory == null)
            try {
                loggerFactory = LoggerFactoryConfig.getLoggerFactoryClass().getConstructor().newInstance();
                loggerFactory.configure(LoggerFactoryConfig.getConfiguration());
            } catch (IOException | InvocationTargetException | SecurityException | NoSuchMethodException | InstantiationException | IllegalArgumentException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        return loggerFactory;
    }
}
