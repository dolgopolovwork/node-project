package ru.babobka.nodeutils.logger;

import java.io.IOException;

/**
 * Created by 123 on 26.05.2018.
 */
public interface SimpleLoggerFactory {
    static SimpleLogger defaultLogger(String loggerName, String runningFolder) throws IOException {
        return new SimpleLogger(LogBuilder.buildRegular(loggerName, runningFolder, false), false);
    }

    static SimpleLogger debugLogger(String loggerName, String runningFolder) throws IOException {
        return new SimpleLogger(LogBuilder.buildRegular(loggerName, runningFolder, true), true);
    }

    static SimpleLogger silentLogger(String loggerName, String runningFolder) throws IOException {
        return new SimpleLogger(LogBuilder.buildNoConsole(loggerName, runningFolder, true), true);
    }

    static SimpleLogger consoleLogger(String loggerName) throws IOException {
        return new SimpleLogger(LogBuilder.buildConsole(loggerName), true);

    }
}
