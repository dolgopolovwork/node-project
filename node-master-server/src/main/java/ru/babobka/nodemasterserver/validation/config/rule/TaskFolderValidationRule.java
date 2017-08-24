package ru.babobka.nodemasterserver.validation.config.rule;

import ru.babobka.nodemasterserver.server.MasterServerConfig;
import ru.babobka.nodeutils.validation.ValidationRule;

import java.io.File;

/**
 * Created by 123 on 26.07.2017.
 */
public class TaskFolderValidationRule implements ValidationRule<MasterServerConfig> {
    @Override
    public void validate(MasterServerConfig data) {
        if (data.getTasksFolder() == null) {
            throw new IllegalArgumentException("'tasksFolder' must not be null");
        } else if (!new File(data.getTasksFolder()).exists()) {
            throw new IllegalArgumentException("'tasksFolder' " + data.getTasksFolder() + " doesn't exist");
        }
    }
}
