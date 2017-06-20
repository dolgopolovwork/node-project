package ru.babobka.subtask.model;

import ru.babobka.nodeserials.NodeRequest;

/**
 * Created by dolgopolov.a on 08.12.15.
 */
public abstract class SubTask {

    private volatile boolean stopped;

    public final ExecutionResult execute(NodeRequest request) {
        return execute(Runtime.getRuntime().availableProcessors(), request);
    }

    public abstract ExecutionResult execute(int threads, NodeRequest request);

    protected abstract void stopCurrentTask();

    public final synchronized void stopProcess() {
        stopped = true;
        stopCurrentTask();
    }

    public abstract ValidationResult validateRequest(NodeRequest request);

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
