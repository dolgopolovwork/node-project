package ru.babobka.nodetask.model;

import net.jodah.expiringmap.ExpiringMap;
import ru.babobka.nodeserials.NodeRequest;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Created by 123 on 29.10.2017.
 */
public class StoppedTasks {
    private final Map<UUID, Long> stoppedTasksMap = ExpiringMap.builder()
            .expirationPolicy(ExpiringMap.ExpirationPolicy.CREATED).expiration(10, TimeUnit.MINUTES).build();

    public synchronized void add(UUID taskId, long timeStamp) {
        stoppedTasksMap.put(taskId, timeStamp);
    }

    public synchronized void add(NodeRequest request) {
        add(request.getTaskId(), request.getTimeStamp());
    }

    public synchronized boolean wasStopped(NodeRequest request) {
        Long stoppedRequestTimeStamp = stoppedTasksMap.get(request.getTaskId());
        return stoppedRequestTimeStamp != null && stoppedRequestTimeStamp > request.getTimeStamp();
    }

    public synchronized void clear() {
        stoppedTasksMap.clear();
    }

}
