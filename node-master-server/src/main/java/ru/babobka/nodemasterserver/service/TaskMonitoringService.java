package ru.babobka.nodemasterserver.service;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by 123 on 10.02.2018.
 */
public class TaskMonitoringService implements TaskMonitoringServiceMBean {

    private final AtomicInteger executedTasks = new AtomicInteger();
    private final AtomicInteger failedTasks = new AtomicInteger();
    private final AtomicInteger statedTasks = new AtomicInteger();
    private final AtomicInteger canceledTasks = new AtomicInteger();

    public void incrementExecutedTasksCount() {
        executedTasks.incrementAndGet();
    }

    public void incrementFailedTasksCount() {
        failedTasks.incrementAndGet();
    }

    public void incrementStartedTasksCount() {
        statedTasks.incrementAndGet();
    }

    public void incrementCanceledTasksCount() {
        canceledTasks.incrementAndGet();
    }

    @Override
    public int getStartedTasksCount() {
        return statedTasks.get();
    }

    @Override
    public int getExecutedTasksCount() {
        return executedTasks.get();
    }

    @Override
    public int getFailedTasksCount() {
        return failedTasks.get();
    }

    @Override
    public int getCanceledTasksCount() {
        return canceledTasks.get();
    }
}
