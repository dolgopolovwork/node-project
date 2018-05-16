package ru.babobka.nodemasterserver.validation.config.rule;

import ru.babobka.nodemasterserver.server.config.FolderConfig;
import ru.babobka.nodemasterserver.server.config.MasterServerConfig;
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
        if (loggerFolder == null) {
            throw new IllegalArgumentException("path to log folder was not specified. you can hard code 'loggerFolder' " +
                    "in the config or use 'loggerFolderEnv' as an environment variable for getting path to log folder.");
        } else {
            File loggerFolderFile = new File(loggerFolder);
            if (!loggerFolderFile.exists() && !loggerFolderFile.mkdirs()) {
                throw new IllegalArgumentException("cannot create folder " + loggerFolder);
            }
        }
        String taskFolder = folderConfig.getTasksFolder();
        if (taskFolder == null) {
            throw new IllegalArgumentException("path to tasks folder was not specified. You can hard code 'tasksFolder' " +
                    "in the config or use 'tasksFolderEnv' as an environment variable for getting path to tasks folder.");
        } else if (!new File(taskFolder).exists()) {
            throw new IllegalArgumentException("folder " + taskFolder + " doesn't exist");
        }
    }
}
