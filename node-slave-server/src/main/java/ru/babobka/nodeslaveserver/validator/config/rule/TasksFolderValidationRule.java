package ru.babobka.nodeslaveserver.validator.config.rule;

import ru.babobka.nodeslaveserver.server.SlaveServerConfig;
import ru.babobka.nodeutils.validation.ValidationRule;

import java.io.File;

/**
 * Created by 123 on 03.09.2017.
 */
public class TasksFolderValidationRule implements ValidationRule<SlaveServerConfig> {
    @Override
    public void validate(SlaveServerConfig data) {
        String taskFolder = data.getTasksFolder();
        if (taskFolder == null) {
            throw new IllegalArgumentException("Path to tasks folder was not specified. You can hard code 'tasksFolder' in the config or use 'tasksFolderEnv' as an environment variable for getting path to tasks folder.");
        } else if (!new File(taskFolder).exists()) {
            throw new IllegalArgumentException("Folder " + taskFolder + " doesn't exist");
        }
    }
}
