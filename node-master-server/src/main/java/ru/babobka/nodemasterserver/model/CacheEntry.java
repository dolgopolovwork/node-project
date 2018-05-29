package ru.babobka.nodemasterserver.model;

import lombok.NonNull;
import ru.babobka.nodemasterserver.task.TaskExecutionResult;
import ru.babobka.nodeserials.data.Data;

import java.io.Serializable;

/**
 * Created by 123 on 21.11.2017.
 */
public class CacheEntry implements Serializable {
    private static final long serialVersionUID = -8661075029606657886L;
    private final String taskName;
    private final Data data;
    private final TaskExecutionResult executionResult;

    public CacheEntry(@NonNull String taskName,
                      @NonNull Data data,
                      @NonNull TaskExecutionResult executionResult) {
        this.taskName = taskName;
        this.data = data;
        this.executionResult = executionResult;
    }

    public String getTaskName() {
        return taskName;
    }

    public Data getData() {
        return data;
    }

    public TaskExecutionResult getExecutionResult() {
        return executionResult;
    }
}
