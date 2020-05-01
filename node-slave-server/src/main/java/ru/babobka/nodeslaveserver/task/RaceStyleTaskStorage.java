package ru.babobka.nodeslaveserver.task;

import lombok.NonNull;
import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodeserials.enumerations.RequestStatus;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Created by 123 on 30.03.2019.
 */
public class RaceStyleTaskStorage {
    private final Set<UUID> raceStyleTasks = new HashSet<>();

    public synchronized boolean isRepeated(@NonNull NodeRequest request) {
        if (request.getRequestStatus() != RequestStatus.RACE) {
            return false;
        }
        if (raceStyleTasks.contains(request.getTaskId())) {
            return true;
        }
        raceStyleTasks.add(request.getTaskId());
        return false;
    }

    public synchronized void unregister(@NonNull NodeRequest request) {
        if (request.getRequestStatus() == RequestStatus.RACE) {
            raceStyleTasks.remove(request.getTaskId());
        }
    }

    public synchronized void clear() {
        raceStyleTasks.clear();
    }
}
