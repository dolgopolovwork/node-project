package ru.babobka.nodeutils.enums;

import ru.babobka.nodeutils.log.LoggerInit;

import java.io.InputStream;

/**
 * Created by 123 on 22.02.2019.
 */
public enum LogProperties {

    PERSISTENT_CONSOLE("persistent.console.properties"),
    PERSISTENT_NO_CONSOLE("persistent.properties"),
    CONSOLE("console.properties"),
    PERSISTENT_CONSOLE_DEBUG("persistent.console.debug.properties");

    private final String propertiesFile;

    LogProperties(String propertiesFile) {
        this.propertiesFile = propertiesFile;
    }

    public InputStream getPropertiesFile() {
        return LoggerInit.class.getResourceAsStream("/log/" + propertiesFile);
    }
}
