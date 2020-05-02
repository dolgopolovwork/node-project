package ru.babobka.nodebusiness.monitoring;

import java.util.Set;

/**
 * Created by 123 on 10.02.2018.
 */
public interface TaskMonitoringServiceMBean {
    int getStartedTasksCount();

    int getExecutedTasksCount();

    int getFailedTasksCount();

    int getCanceledTasksCount();

    int getCacheHitCount();

    Set<String> registeredTasks();

}
