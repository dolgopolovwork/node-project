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
        if (data.getTasksFolder() == null) {
            throw new IllegalArgumentException("'tasksFolder' is null");
        } else if (!new File(data.getTasksFolder()).exists()) {
            throw new IllegalArgumentException("'tasksFolder' " + data.getTasksFolder() + " doesn't exist");
        }
    }
}
