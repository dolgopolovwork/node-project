package ru.babobka.nodebusiness.monitoring;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by 123 on 10.02.2018.
 */
public class TaskMonitoringService implements TaskMonitoringServiceMBean {

    private final AtomicInteger executedTasks = new AtomicInteger();
    private final AtomicInteger failedTasks = new AtomicInteger();
    private final AtomicInteger startedTasks = new AtomicInteger();
    private final AtomicInteger canceledTasks = new AtomicInteger();
    private final AtomicInteger cacheHitCount = new AtomicInteger();

    public void clear() {
        executedTasks.set(0);
        failedTasks.set(0);
        startedTasks.set(0);
        canceledTasks.set(0);
        cacheHitCount.set(0);
    }

    public void incrementExecutedTasksCount() {
        executedTasks.incrementAndGet();
    }

    public void incrementFailedTasksCount() {
        failedTasks.incrementAndGet();
    }

    public void incrementStartedTasksCount() {
        startedTasks.incrementAndGet();
    }

    public void incrementCanceledTasksCount() {
        canceledTasks.incrementAndGet();
    }

    public void incrementCacheHitCount() {
        cacheHitCount.incrementAndGet();
    }

    @Override
    public int getStartedTasksCount() {
        return startedTasks.get();
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

    @Override
    public int getCacheHitCount() {
        return cacheHitCount.get();
    }

    public TaskMonitoringData getTaskMonitoringData() {
        return new TaskMonitoringData(
                executedTasks.get(),
                failedTasks.get(),
                startedTasks.get(),
                canceledTasks.get(),
                cacheHitCount.get());
    }
}
