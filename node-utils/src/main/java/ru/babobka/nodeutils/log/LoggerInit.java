package ru.babobka.nodeutils.log;

import org.apache.log4j.PropertyConfigurator;
import ru.babobka.nodeutils.enums.LogProperties;
import ru.babobka.nodeutils.util.TextUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.UUID;
import java.util.function.UnaryOperator;

/**
 * Created by 123 on 21.02.2019.
 */
public class LoggerInit {

    private static final String LOGGER_FILE_PROP = "log4j.appender.file.File";

    private LoggerInit() {
    }

    public static void initConsoleLogger() {
        initProperties(LogProperties.CONSOLE.getPropertiesFile(), p -> p);
    }

    public static void initPersistentConsoleDebugLogger(String loggerFolder, String loggerName) {
        initFileBasedLogger(LogProperties.PERSISTENT_CONSOLE_DEBUG.getPropertiesFile(), loggerFolder, loggerName);
    }

    public static void initPersistentConsoleLogger(String loggerFolder, String loggerName) {
        initFileBasedLogger(LogProperties.PERSISTENT_CONSOLE.getPropertiesFile(), loggerFolder, loggerName);
    }

    public static void initPersistentNoConsoleLogger(String loggerFolder, String loggerName) {
        initFileBasedLogger(LogProperties.PERSISTENT_NO_CONSOLE.getPropertiesFile(), loggerFolder, loggerName);
    }

    private static void initFileBasedLogger(InputStream loggerConfigStream, String loggerFolder, String loggerName) {
        checkFolderAndLoggerName(loggerFolder, loggerName);
        initProperties(loggerConfigStream, p -> {
            p.setProperty(LOGGER_FILE_PROP, getLoggerFile(loggerFolder, loggerName));
            return p;
        });
    }

    private static String getLoggerFile(String loggerFolder, String loggerName) {
        return loggerFolder + File.separator + loggerName + "_" + UUID.randomUUID() + ".log";
    }

    private static void checkFolderAndLoggerName(String loggerFolder, String loggerName) {
        if (TextUtil.isEmpty(loggerFolder)) {
            throw new IllegalArgumentException("cannot init logger. loggerFolder was not set");
        } else if (loggerName == null) {
            throw new IllegalArgumentException("cannot init logger. loggerName was not set");
        }
    }

    private static void initProperties(InputStream loggerConfigStream, UnaryOperator<Properties> logPropertiesInitFunc) {
        Properties p = new Properties();
        try (InputStream inputStream = loggerConfigStream) {
            p.load(inputStream);
            logPropertiesInitFunc.apply(p);
        } catch (IOException e) {
            throw new RuntimeException("cannot init log", e);
        }
        PropertyConfigurator.configure(p);
    }
}
