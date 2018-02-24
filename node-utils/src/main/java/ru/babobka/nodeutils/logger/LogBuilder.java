package ru.babobka.nodeutils.logger;

import java.io.File;
import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;

/**
 * Created by dolgopolov.a on 12.01.16.
 */
interface LogBuilder {

    static Logger buildRegular(String loggerName, String runningFolder, boolean debugMode) throws IOException {
        return buil(loggerName, runningFolder, true, debugMode);
    }

    static Logger buildNoConsole(String loggerName, String runningFolder, boolean debugMode) throws IOException {
        return buil(loggerName, runningFolder, false, debugMode);
    }

    static Logger buil(String loggerName, String runningFolder, boolean writeConsole, boolean debugMode) throws IOException {

        Logger logger = Logger.getLogger(loggerName + "_" + System.currentTimeMillis());

        File folder = new File(runningFolder + File.separator);
        if (!folder.exists() && !folder.mkdirs()) {
            throw new IOException("Can not create folder " + folder);
        }
        String fileName = folder.getAbsolutePath() + File.separator + "_" + System.currentTimeMillis()
                + ".log";
        FileHandler fh = new FileHandler(fileName);
        LogFormatter formatter = new LogFormatter(debugMode);
        if (writeConsole) {
            Handler ch = new ConsoleHandler();
            ch.setFormatter(formatter);
            logger.addHandler(ch);
        }
        fh.setFormatter(formatter);
        logger.addHandler(fh);
        logger.setUseParentHandlers(false);
        return logger;
    }
}
