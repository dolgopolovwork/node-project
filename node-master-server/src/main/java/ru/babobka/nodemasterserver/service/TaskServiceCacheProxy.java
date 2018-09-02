package ru.babobka.nodemasterserver.service;

import lombok.NonNull;
import ru.babobka.nodemasterserver.exception.TaskExecutionException;
import ru.babobka.nodemasterserver.key.MasterServerKey;
import ru.babobka.nodemasterserver.listener.CacheRequestListener;
import ru.babobka.nodemasterserver.monitoring.TaskMonitoringService;
import ru.babobka.nodemasterserver.task.TaskExecutionResult;
import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodetask.TaskPool;
import ru.babobka.nodetask.model.SubTask;
import ru.babobka.nodeutils.container.Container;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by 123 on 20.11.2017.
 */
public class TaskServiceCacheProxy implements TaskService {

    private final TaskPool taskPool = Container.getInstance().get(MasterServerKey.MASTER_SERVER_TASK_POOL);
    private final TaskService taskService;
    private final CacheRequestListener cacheRequestListener = Container.getInstance().get(CacheRequestListener.class);
    private final TaskMonitoringService taskMonitoringService = Container.getInstance().get(TaskMonitoringService.class);

    public TaskServiceCacheProxy(@NonNull TaskService taskService) {
        this.taskService = taskService;
    }

    @Override
    public TaskExecutionResult executeTask(@NonNull NodeRequest request, int maxNodes) throws TaskExecutionException {
        boolean canBeCached = canBeCached(request);
        if (canBeCached) {
            TaskExecutionResult cachedResult = cacheRequestListener.onRequest(request);
            if (cachedResult != null) {
                taskMonitoringService.incrementCacheHitCount();
                return cachedResult;
            }
        }
        TaskExecutionResult result = taskService.executeTask(request, maxNodes);
        if (canBeCached) {
            cacheRequestListener.afterRequest(request, result);
        }
        return result;
    }

    boolean canBeCached(NodeRequest request) throws TaskExecutionException {
        SubTask task;
        try {
            task = taskPool.get(request.getTaskName());
        } catch (IOException e) {
            throw new TaskExecutionException(e);
        }
        return task.enableCache() && !task.isSingleNodeTask(request);
    }

    @Override
    public TaskExecutionResult executeTask(NodeRequest request) throws TaskExecutionException {
        return executeTask(request, 0);
    }

    @Override
    public boolean cancelTask(@NonNull UUID taskId) throws TaskExecutionException {
        return taskService.cancelTask(taskId);
    }
}
