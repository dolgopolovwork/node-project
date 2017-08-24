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
        if (data.getLoggerFolder() == null) {
            throw new IllegalArgumentException("'loggerFolder' is null");
        } else {
            File loggerFolderFile = new File(data.getLoggerFolder());
            if (!loggerFolderFile.exists() && !loggerFolderFile.mkdirs()) {
                throw new IllegalArgumentException("Can not create logger folder " + loggerFolderFile);
            }
        }
    }
}
