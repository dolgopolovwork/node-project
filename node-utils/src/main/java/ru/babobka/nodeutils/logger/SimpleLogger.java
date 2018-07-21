package ru.babobka.nodeutils.logger;

import ru.babobka.nodeutils.util.TextUtil;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * Created by 123 on 25.05.2018.
 */
public class SimpleLogger implements NodeLogger {
    private final Logger logger;
    private final boolean debugMode;

    public SimpleLogger(Logger logger, boolean debugMode) throws IOException {
        this.logger = logger;
        this.debugMode = debugMode;
    }

    public void info(Object o) {
        info(o.toString());
    }

    public void info(String message) {
        logger.info(message);
    }

    public void warning(String message) {
        logger.warning(message);
    }

    public void warning(Exception e) {
        logger.warning(TextUtil.getStringFromExceptionOneLine(e));
    }

    public void warning(String message, Exception e) {
        logger.warning(message + "\t" + TextUtil.getStringFromExceptionOneLine(e));
    }

    public void debug(String message) {
        if (debugMode) {
            info(message);
        }
    }

    public void error(String message) {
        logger.severe(message);
    }

    public void error(Exception e) {
        logger.severe(TextUtil.getStringFromExceptionOneLine(e));
    }

    public void error(String message, Exception e) {
        logger.severe(message + "\t" + TextUtil.getStringFromExceptionOneLine(e));
    }
}
