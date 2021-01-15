package gargoyle.ct.log.jul;

import gargoyle.ct.log.ILoggerFactory;
import gargoyle.ct.log.Logger;
import gargoyle.ct.log.LoggerFactoryConfig;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.LogManager;

public class JULLoggerFactory implements ILoggerFactory {

    @Override
    public Logger getLogger(String name) {
        return new JULLogger(name);
    }

    @Override
    public void configure(InputStream stream) throws IOException {
        if (stream == null) {
            java.util.logging.Logger.getGlobal().warning((LoggerFactoryConfig.MSG_NOT_FOUND));
        }
        else {
            LogManager.getLogManager().readConfiguration(stream);
        }
    }
}
