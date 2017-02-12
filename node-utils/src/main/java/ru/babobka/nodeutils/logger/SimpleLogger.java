package ru.babobka.nodeutils.logger;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Logger;

public class SimpleLogger {

    private final Logger logger;

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
	logger.warning(getStringFromException(e));
    }

    public void warning(String message, Exception e) {
	logger.warning(message + "\t" + getStringFromException(e));
    }

    public void error(String message) {
	logger.severe(message);
    }

    public void error(Exception e) {
	logger.severe(getStringFromException(e));
    }

    public void error(String message, Exception e) {
	logger.severe(message + "\t" + getStringFromException(e));
    }

    private static String getStringFromException(Exception ex) {
	StringWriter errors = new StringWriter();
	ex.printStackTrace(new PrintWriter(errors));
	return errors.toString();
    }

}
