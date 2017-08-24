package ru.babobka.nodemasterserver.validation.config.rule;

import ru.babobka.nodemasterserver.server.MasterServerConfig;
import ru.babobka.nodeutils.validation.ValidationRule;

import java.io.File;

/**
 * Created by 123 on 26.07.2017.
 */
public class LoggerFolderValidationRule implements ValidationRule<MasterServerConfig> {
    @Override
    public void validate(MasterServerConfig data) {
        if (data.getLoggerFolder() == null) {
            throw new IllegalArgumentException("'loggerFolder' must not be null");
        } else {
            File loggerFolderFile = new File(data.getLoggerFolder());
            if (!loggerFolderFile.exists() && !loggerFolderFile.mkdirs()) {
                throw new IllegalArgumentException("Can not create folder for " + loggerFolderFile);
            }
        }
    }
}
