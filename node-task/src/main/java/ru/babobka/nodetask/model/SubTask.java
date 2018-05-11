package ru.babobka.nodetask.model;

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

    public abstract DataValidators getDataValidators();

    public abstract boolean isRequestDataTooSmall(NodeRequest request);

    public boolean isRequestDataTooBig(NodeRequest request) {
        return false;
    }

    public abstract RequestDistributor getDistributor();

    public abstract String getDescription();

    public String getName() {
        return this.getClass().getName();
    }

    public abstract boolean isRaceStyle();

    public abstract Reducer getReducer();

    public boolean isStopped() {
        return stopped;
    }

}
