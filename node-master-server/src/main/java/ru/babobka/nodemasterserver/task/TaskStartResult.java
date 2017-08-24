package ru.babobka.nodemasterserver.task;

import java.util.UUID;

public class TaskStartResult {

    private final UUID taskId;

    private final boolean failed;

    private final boolean systemError;

    private final String message;

    private TaskStartResult(UUID taskId, boolean failed, boolean systemError, String message) {
        this.taskId = taskId;
        this.failed = failed;
        this.message = message;
        this.systemError = systemError;
    }

    private TaskStartResult(UUID taskId) {
        this(taskId, false, false, null);
    }

    public static TaskStartResult ok(UUID taskId) {
        return new TaskStartResult(taskId);
    }

    public static TaskStartResult systemError(UUID taskId, String message) {
        return new TaskStartResult(taskId, true, true, message);
    }

    public static TaskStartResult failed(UUID taskId, String message) {
        return new TaskStartResult(taskId, true, false, message);
    }

    public boolean isSystemError() {
        return systemError;
    }

    public UUID getTaskId() {
        return taskId;
    }

    public boolean isFailed() {
        return failed;
    }

    public String getMessage() {
        return message;
    }

}
