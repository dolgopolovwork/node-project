package ru.babobka.nodemasterserver.model;

import ru.babobka.nodeserials.NodeResponse;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by dolgopolov.a on 03.08.15.
 */
public class ResponseStorage {

    private final Map<UUID, ResponsesArray> responsesMap;

    public ResponseStorage() {
	responsesMap = new HashMap<>();
    }

    public synchronized void create(UUID taskId, ResponsesArray responses) {
	responsesMap.put(taskId, responses);
    }

    public synchronized ResponsesArray get(UUID taskId) {

	return responsesMap.get(taskId);
    }

    public synchronized boolean exists(UUID taskId) {
	return responsesMap.containsKey(taskId);
    }

    public synchronized void addBadResponse(UUID taskId) {
	ResponsesArray responsesArray = responsesMap.get(taskId);
	if (responsesArray != null) {
	    responsesArray.add(NodeResponse.failed(taskId));
	}
    }

    public synchronized void addStopResponse(UUID taskId) {
	ResponsesArray responsesArray = responsesMap.get(taskId);
	if (responsesArray != null) {
	    responsesArray.add(NodeResponse.stopped(taskId));
	}
    }

    public synchronized void setStopAllResponses(UUID taskId) {
	ResponsesArray responsesArray = responsesMap.get(taskId);
	if (responsesArray != null) {
	    responsesArray.fill(NodeResponse.stopped(taskId));
	}
    }

    public synchronized Map<UUID, ResponsesArrayMeta> getRunningTasksMetaMap() {
	Map<UUID, ResponsesArrayMeta> taskMap = new HashMap<>();
	for (Map.Entry<UUID, ResponsesArray> entry : responsesMap.entrySet()) {
	    if (!entry.getValue().isComplete()) {
		taskMap.put(entry.getKey(), entry.getValue().getMeta());
	    }
	}
	return taskMap;
    }

    public synchronized ResponsesArrayMeta getTaskMeta(UUID taskId) {
	ResponsesArray responsesArray = responsesMap.get(taskId);
	if (responsesArray != null) {
	    return responsesArray.getMeta();
	}
	return null;
    }

    public synchronized void remove(UUID taskId) {
	responsesMap.remove(taskId);
    }

    public synchronized int size() {
	return responsesMap.size();
    }

}
