package ru.babobka.nodeutils.logger;

import ru.babobka.nodeutils.util.TextUtil;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SimpleLogger {

    private final Logger logger;

    SimpleLogger(Logger logger) {
        this.logger = logger;
    }

    public SimpleLogger(String loggerName, String runningFolder, String prefix) throws IOException {
        logger = LogBuilder.build(loggerName, runningFolder, prefix);
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
        logger.warning(TextUtil.getStringFromException(e));
    }

    public void warning(String message, Exception e) {
        logger.warning(message + "\t" + TextUtil.getStringFromException(e));
    }

    public void debug(String message) {
        logger.log(Level.CONFIG, message);
    }

    public void error(String message) {
        logger.severe(message);
    }

    public void error(Exception e) {
        logger.severe(TextUtil.getStringFromException(e));
    }

    public void error(String message, Exception e) {
        logger.severe(message + "\t" + TextUtil.getStringFromException(e));
    }


}
