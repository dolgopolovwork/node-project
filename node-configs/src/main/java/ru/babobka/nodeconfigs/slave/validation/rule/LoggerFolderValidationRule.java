package ru.babobka.nodeconfigs.slave.validation.rule;

import ru.babobka.nodeconfigs.slave.SlaveServerConfig;
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
            throw new IllegalArgumentException("path to log folder was not specified.");
        } else {
            File loggerFolderFile = new File(loggerFolder);
            if (!loggerFolderFile.exists() && !loggerFolderFile.mkdirs()) {
                throw new IllegalArgumentException("cannot create folder " + loggerFolder);
            }
        }
    }
}
