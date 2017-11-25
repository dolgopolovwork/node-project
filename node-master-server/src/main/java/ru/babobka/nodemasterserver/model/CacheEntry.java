package ru.babobka.nodemasterserver.model;

import ru.babobka.nodemasterserver.task.TaskExecutionResult;
import ru.babobka.nodeutils.util.ArrayUtil;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by 123 on 21.11.2017.
 */
public class CacheEntry implements Serializable {
    private static final long serialVersionUID = -8661075029606657886L;
    private final String taskName;
    private final Map<String, Serializable> data;
    private final TaskExecutionResult executionResult;

    public CacheEntry(String taskName, Map<String, Serializable> data, TaskExecutionResult executionResult) {
        if (ArrayUtil.isNull(taskName, data, executionResult)) {
            throw new IllegalArgumentException("All values must be non null");
        }
        this.taskName = taskName;
        this.data = data;
        this.executionResult = executionResult;
    }

    public String getTaskName() {
        return taskName;
    }

    public Map<String, Serializable> getData() {
        return data;
    }

    public TaskExecutionResult getExecutionResult() {
        return executionResult;
    }
}
