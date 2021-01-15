package gargoyle.ct.log;

import gargoyle.ct.log.jul.JULLoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public final class LoggerFactoryConfig {

    public static final String MSG_NOT_FOUND = "configuration {0} not found";
    static final String LOGGING_PROPERTIES = "/config/logging.properties";

    private LoggerFactoryConfig() {
    }

    public static Class<? extends ILoggerFactory> getLoggerFactoryClass() {
        return JULLoggerFactory.class;
    }

    public static InputStream getConfiguration() throws IOException {
        InputStream stream = LoggerFactory.class.getResourceAsStream(LOGGING_PROPERTIES);
        if (stream == null)
            throw new FileNotFoundException(LOGGING_PROPERTIES);
        return stream;
    }
}
