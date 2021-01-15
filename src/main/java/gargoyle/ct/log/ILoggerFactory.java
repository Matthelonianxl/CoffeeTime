package gargoyle.ct.log;

import java.io.IOException;
import java.io.InputStream;

@FunctionalInterface
public interface ILoggerFactory {
    Logger getLogger(String name);

    default void configure(InputStream stream) throws IOException {
    }
}
