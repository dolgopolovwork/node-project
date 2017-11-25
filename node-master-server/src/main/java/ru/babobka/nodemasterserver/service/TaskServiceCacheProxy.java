package ru.babobka.nodemasterserver.service;

import ru.babobka.nodemasterserver.exception.TaskExecutionException;
import ru.babobka.nodemasterserver.listener.CacheRequestListener;
import ru.babobka.nodemasterserver.task.TaskExecutionResult;
import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodeutils.container.Container;

import java.util.UUID;

/**
 * Created by 123 on 20.11.2017.
 */
public class TaskServiceCacheProxy implements TaskService {

    private TaskService taskService = Container.getInstance().get(TaskService.class);
    private CacheRequestListener cacheRequestListener = Container.getInstance().get(CacheRequestListener.class);

    @Override
    public TaskExecutionResult executeTask(NodeRequest request, int maxNodes) throws TaskExecutionException {
        TaskExecutionResult cachedResult = cacheRequestListener.onRequest(request);
        if (cachedResult != null) {
            return cachedResult;
        } else {
            TaskExecutionResult result = taskService.executeTask(request, maxNodes);
            cacheRequestListener.afterRequest(request, result);
            return result;
        }
    }

    @Override
    public TaskExecutionResult executeTask(NodeRequest request) throws TaskExecutionException {
        return executeTask(request, 0);
    }

    @Override
    public boolean cancelTask(UUID taskId) throws TaskExecutionException {
        return taskService.cancelTask(taskId);
    }
}
