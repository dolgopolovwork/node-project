package ru.babobka.nodebusiness.monitoring;

public class TaskMonitoringData {
    private final int executedTasks;
    private final int failedTasks;
    private final int startedTasks;
    private final int canceledTasks;
    private final int cacheHitCount;

    public TaskMonitoringData(
            int executedTasks,
            int failedTasks,
            int startedTasks,
            int canceledTasks,
            int cacheHitCount) {
        this.executedTasks = executedTasks;
        this.failedTasks = failedTasks;
        this.startedTasks = startedTasks;
        this.canceledTasks = canceledTasks;
        this.cacheHitCount = cacheHitCount;
    }

    public int getExecutedTasks() {
        return executedTasks;
    }

    public int getFailedTasks() {
        return failedTasks;
    }

    public int getStartedTasks() {
        return startedTasks;
    }

    public int getCanceledTasks() {
        return canceledTasks;
    }

    public int getCacheHitCount() {
        return cacheHitCount;
    }
}
