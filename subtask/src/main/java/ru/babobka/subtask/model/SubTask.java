package ru.babobka.subtask.model;

import ru.babobka.nodeserials.NodeRequest;

/**
 * Created by dolgopolov.a on 08.12.15.
 */
public abstract class SubTask {

    private volatile boolean stopped;

    public abstract TaskExecutor getTaskExecutor();

    public final synchronized void stopProcess() {
        stopped = true;
        getTaskExecutor().stopCurrentTask();
    }
    public abstract RequestValidator getRequestValidator();

    public abstract boolean isRequestDataTooSmall(NodeRequest request);

    public abstract RequestDistributor getDistributor();

    public abstract String getDescription();

    public abstract String getName();

    public abstract boolean isRaceStyle();

    public abstract Reducer getReducer();

    public abstract SubTask newInstance();

    public boolean isStopped() {
        return stopped;
    }

}
