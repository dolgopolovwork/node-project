package ru.babobka.nodetask.service;

import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodetask.exception.TaskExecutionException;
import ru.babobka.nodeutils.func.Callback;

import java.util.UUID;

public interface TaskService {

    void executeTask(NodeRequest request,
                     int maxNodes,
                     Callback<TaskExecutionResult> onTaskExecutedCallback,
                     Callback<TaskExecutionException> onError);

    void executeTask(NodeRequest request,
                     Callback<TaskExecutionResult> onTaskExecutedCallback,
                     Callback<TaskExecutionException> onError);

    void cancelTask(UUID taskId,
                    Callback<Boolean> onTaskCanceledCallback,
                    Callback<TaskExecutionException> onError);
}
