package ru.babobka.nodeslaveserver.validator.config.rule;

import ru.babobka.nodeslaveserver.server.SlaveServerConfig;
import ru.babobka.nodeutils.validation.ValidationRule;

import java.io.File;

/**
 * Created by 123 on 03.09.2017.
 */
public class LoggerFolderValidationRule implements ValidationRule<SlaveServerConfig> {
    @Override
    public void validate(SlaveServerConfig data) {
        String loggerFolder = data.getLoggerFolder();
        if (loggerFolder == null) {
            throw new IllegalArgumentException("path to log folder was not specified. you can hard code 'loggerFolder' in the config or use 'loggerFolderEnv' as an environment variable for getting path to log folder.");
        } else {
            File loggerFolderFile = new File(loggerFolder);
            if (!loggerFolderFile.exists() && !loggerFolderFile.mkdirs()) {
                throw new IllegalArgumentException("cannot create folder " + loggerFolder);
            }
        }
    }
}
