package ru.babobka.subtask.model;

import ru.babobka.nodeserials.NodeRequest;

/**
 * Created by 123 on 20.06.2017.
 */
public abstract class TaskExecutor {

    public abstract ExecutionResult execute(int threads, NodeRequest request);

    public abstract void stopCurrentTask();

    public final ExecutionResult execute(NodeRequest request) {
        return execute(Runtime.getRuntime().availableProcessors(), request);
    }
}
