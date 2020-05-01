package ru.babobka.nodemasterserver.service;

import lombok.NonNull;
import ru.babobka.nodebusiness.monitoring.TaskMonitoringService;
import ru.babobka.nodemasterserver.key.MasterServerKey;
import ru.babobka.nodemasterserver.listener.CacheRequestListener;
import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodeserials.enumerations.ResponseStatus;
import ru.babobka.nodetask.TaskPool;
import ru.babobka.nodetask.exception.TaskExecutionException;
import ru.babobka.nodetask.model.SubTask;
import ru.babobka.nodetask.service.TaskExecutionResult;
import ru.babobka.nodetask.service.TaskService;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.func.Callback;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by 123 on 20.11.2017.
 */
public class MasterTaskServiceCacheProxy implements TaskService {

    private final TaskPool taskPool = Container.getInstance().get(MasterServerKey.MASTER_SERVER_TASK_POOL);
    private final MasterTaskService taskService;
    private final CacheRequestListener cacheRequestListener = Container.getInstance().get(CacheRequestListener.class);
    private final TaskMonitoringService taskMonitoringService = Container.getInstance().get(TaskMonitoringService.class);

    public MasterTaskServiceCacheProxy(@NonNull MasterTaskService taskService) {
        this.taskService = taskService;
    }

    @Override
    public void executeTask(@NonNull NodeRequest request, int maxNodes,
                            @NonNull Callback<TaskExecutionResult> onTaskExecutedCallback,
                            @NonNull Callback<TaskExecutionException> onError) {
        try {
            boolean canBeCached = canBeCached(request);
            if (canBeCached) {
                TaskExecutionResult cachedResult = cacheRequestListener.onRequest(request);
                if (cachedResult != null) {
                    taskMonitoringService.incrementCacheHitCount();
                    onTaskExecutedCallback.callback(cachedResult);
                    return;
                }
            }
            taskService.executeTask(request, maxNodes, result -> {
                if (canBeCached) {
                    cacheRequestListener.afterRequest(request, result);
                }
                onTaskExecutedCallback.callback(result);
            }, onError);
        } catch (TaskExecutionException e) {
            onError.callback(e);
        }
    }

    boolean canBeCached(NodeRequest request) throws TaskExecutionException {
        SubTask task;
        try {
            task = taskPool.get(request.getTaskName());
        } catch (IOException e) {
            throw new TaskExecutionException(ResponseStatus.SYSTEM_ERROR, e);
        }
        return task.enableCache() && !task.isSingleNodeTask(request);
    }

    @Override
    public void executeTask(NodeRequest request,
                            @NonNull Callback<TaskExecutionResult> onTaskExecutedCallback,
                            @NonNull Callback<TaskExecutionException> onError) {
        executeTask(request, 0, onTaskExecutedCallback, onError);
    }

    @Override
    public void cancelTask(@NonNull UUID taskId,
                           @NonNull Callback<Boolean> onTaskCanceledCallback,
                           @NonNull Callback<TaskExecutionException> onError) {
        taskService.cancelTask(taskId, onTaskCanceledCallback, onError);
    }
}
