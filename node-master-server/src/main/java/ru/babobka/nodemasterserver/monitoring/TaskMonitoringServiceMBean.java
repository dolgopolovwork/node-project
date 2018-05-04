package ru.babobka.nodemasterserver.monitoring;

/**
 * Created by 123 on 10.02.2018.
 */
public interface TaskMonitoringServiceMBean {
    int getStartedTasksCount();

    int getExecutedTasksCount();

    int getFailedTasksCount();

    int getCanceledTasksCount();

    int getCacheHitCount();
}
