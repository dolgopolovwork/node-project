package ru.babobka.nodetask.model;

import ru.babobka.nodeserials.NodeRequest;

/**
 * Created by 123 on 20.06.2017.
 */
public abstract class TaskExecutor {

    public ExecutionResult execute(NodeRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("cannot execute null request");
        }
        return executeImpl(request);
    }

    protected abstract ExecutionResult executeImpl(NodeRequest request);

    public abstract void stopCurrentTask();

}
