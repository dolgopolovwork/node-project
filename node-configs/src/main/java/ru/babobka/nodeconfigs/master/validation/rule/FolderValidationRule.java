package ru.babobka.nodeconfigs.master.validation.rule;

import ru.babobka.nodeconfigs.master.FolderConfig;
import ru.babobka.nodeconfigs.master.MasterServerConfig;
import ru.babobka.nodeutils.util.TextUtil;
import ru.babobka.nodeutils.validation.ValidationRule;

import java.io.File;

/**
 * Created by 123 on 13.05.2018.
 */
public class FolderValidationRule implements ValidationRule<MasterServerConfig> {
    @Override
    public void validate(MasterServerConfig config) {
        FolderConfig folderConfig = config.getFolders();
        if (folderConfig == null) {
            throw new IllegalArgumentException("folderConfig was not set");
        }
        String loggerFolder = folderConfig.getLoggerFolder();
        if (TextUtil.isEmpty(loggerFolder)) {
            throw new IllegalArgumentException("path to log folder was not specified.");
        } else {
            File loggerFolderFile = new File(loggerFolder);
            if (!loggerFolderFile.exists() && !loggerFolderFile.mkdirs()) {
                throw new IllegalArgumentException("cannot create folder " + loggerFolder);
            }
        }
        String taskFolder = folderConfig.getTasksFolder();
        if (TextUtil.isEmpty(taskFolder)) {
            throw new IllegalArgumentException("path to tasks folder was not specified");
        } else if (!new File(taskFolder).exists()) {
            throw new IllegalArgumentException("folder '" + taskFolder + "' doesn't exist");
        }
    }
}
