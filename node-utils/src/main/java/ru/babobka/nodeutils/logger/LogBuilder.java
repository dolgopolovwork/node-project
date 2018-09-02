package ru.babobka.nodeutils.logger;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;

/**
 * Created by dolgopolov.a on 12.01.16.
 */
public class LogBuilder {

    private static final int LOG_FILE_LIMIT_BYTES = 5 * 1024 * 1024;
    private static final int MAX_FILES_TO_CREATE = 15;

    private LogBuilder() {

    }

    public static Logger buildRegular(String loggerName, String runningFolder, boolean debugMode) throws IOException {
        return build(loggerName, runningFolder, true, true, debugMode);
    }

    public static Logger buildNoConsole(String loggerName, String runningFolder, boolean debugMode) throws IOException {
        return build(loggerName, runningFolder, false, true, debugMode);
    }

    public static Logger buildConsole(String loggerName) throws IOException {
        return build(loggerName, null, true, false, true);
    }

    private static Logger build(String loggerName,
                                String runningFolder,
                                boolean writeConsole,
                                boolean writeFile,
                                boolean debugMode) throws IOException {
        String uniqueLoggerName = loggerName + "_" + UUID.randomUUID();
        Logger logger = Logger.getLogger(uniqueLoggerName);

        LogFormatter formatter = new LogFormatter(debugMode);
        if (writeConsole) {
            Handler consoleHandler = new ConsoleHandler();
            consoleHandler.setFormatter(formatter);
            logger.addHandler(consoleHandler);
        }
        if (writeFile) {
            File folder = new File(runningFolder + File.separator);
            if (!folder.exists() && !folder.mkdirs()) {
                throw new IOException("cannot create folder " + folder);
            }
            String fileName = folder.getAbsolutePath() + File.separator + uniqueLoggerName + ".log";
            FileHandler fileHandler = new FileHandler(fileName, LOG_FILE_LIMIT_BYTES, MAX_FILES_TO_CREATE);
            fileHandler.setFormatter(formatter);
            logger.addHandler(fileHandler);
        }
        logger.setUseParentHandlers(false);
        return logger;
    }
}