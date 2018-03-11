package ru.babobka.nodemasterserver.model;

import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodeserials.NodeResponse;
import ru.babobka.nodeserials.enumerations.ResponseStatus;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by dolgopolov.a on 03.08.15.
 */
public class ResponseStorage {

    private final Map<UUID, Responses> responsesMap = new HashMap<>();

    public synchronized void create(UUID taskId, Responses responses) {
        responsesMap.put(taskId, responses);
    }

    public synchronized Responses get(UUID taskId) {
        return responsesMap.get(taskId);
    }

    public synchronized boolean exists(UUID taskId) {
        return responsesMap.containsKey(taskId);
    }

    public synchronized boolean addBadResponse(UUID taskId) {
        Responses responses = responsesMap.get(taskId);
        return responses != null
                && responses.add(NodeResponse.failed(taskId));
    }

    public synchronized void addBadResponse(NodeRequest request) {
        addBadResponse(request.getTaskId());
    }

    public synchronized boolean addStopResponse(UUID taskId) {
        Responses responses = responsesMap.get(taskId);
        return responses != null && responses.add(NodeResponse.stopped(taskId));
    }

    public synchronized boolean setStopAllResponses(UUID taskId) {
        Responses responses = responsesMap.get(taskId);
        if (responses == null) {
            return false;
        }
        responses.setStatus(ResponseStatus.STOPPED);
        return responses.fill(NodeResponse.stopped(taskId));
    }


    public synchronized void remove(UUID taskId) {
        responsesMap.remove(taskId);
    }


    @Override
    public String toString() {
        return "ResponseStorage{" +
                "responsesMap=" + responsesMap +
                '}';
    }
}
