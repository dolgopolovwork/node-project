package ru.babobka.nodemasterserver.service;

import ru.babobka.nodemasterserver.exception.TaskExecutionException;
import ru.babobka.nodemasterserver.task.TaskExecutionResult;
import ru.babobka.nodeserials.NodeRequest;

import java.util.UUID;

public interface TaskService {

    TaskExecutionResult executeTask(NodeRequest request, int maxNodes)
            throws TaskExecutionException;

    TaskExecutionResult executeTask(NodeRequest request)
            throws TaskExecutionException;

    void cancelTask(UUID taskId) throws TaskExecutionException;

}
