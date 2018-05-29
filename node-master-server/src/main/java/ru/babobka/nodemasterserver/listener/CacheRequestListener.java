package ru.babobka.nodemasterserver.listener;

import lombok.NonNull;
import ru.babobka.nodebusiness.service.ResponseCacheService;
import ru.babobka.nodemasterserver.model.CacheEntry;
import ru.babobka.nodemasterserver.task.TaskExecutionResult;
import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodeutils.container.Container;

/**
 * Created by 123 on 21.11.2017.
 */
public class CacheRequestListener implements AfterRequestListener<TaskExecutionResult>, OnRequestListener<TaskExecutionResult> {

    private final ResponseCacheService responseCacheService = Container.getInstance().get(ResponseCacheService.class);

    @Override
    public TaskExecutionResult onRequest(@NonNull NodeRequest request) {
        CacheEntry cacheEntry = responseCacheService.get(request.cacheKey());
        if (cacheEntry == null) {
            return null;
        } else if (cacheEntry.getTaskName().equals(request.getTaskName())
                && cacheEntry.getData().equals(request.getData())) {
            return cacheEntry.getExecutionResult();
        }
        return null;
    }

    @Override
    public void afterRequest(@NonNull NodeRequest request, @NonNull TaskExecutionResult result) {
        if (!result.wasStopped()) {
            CacheEntry cacheEntry = new CacheEntry(request.getTaskName(), request.getData(), result);
            responseCacheService.put(request.cacheKey(), cacheEntry);
        }
    }
}
