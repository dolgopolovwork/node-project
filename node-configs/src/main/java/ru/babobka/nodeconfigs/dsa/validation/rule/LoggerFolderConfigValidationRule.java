package ru.babobka.nodeconfigs.dsa.validation.rule;

import ru.babobka.nodeconfigs.dsa.DSAServerConfig;
import ru.babobka.nodeutils.validation.ValidationRule;

import java.io.File;

public class LoggerFolderConfigValidationRule implements ValidationRule<DSAServerConfig> {
    @Override
    public void validate(DSAServerConfig config) {
        String loggerFolder = config.getLoggerFolder();
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
