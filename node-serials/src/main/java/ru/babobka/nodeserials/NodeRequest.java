package ru.babobka.nodeserials;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by dolgopolov.a on 08.07.15.
 */

public final class NodeRequest implements Serializable {

    private static final long serialVersionUID = 8L;

    private final UUID taskId;

    private final UUID requestId;

    private final boolean stoppingRequest;

    private final boolean raceStyle;

    private final String taskName;

    private final long timeStamp;

    private final Map<String, Serializable> data = new HashMap<>();

    private NodeRequest(UUID taskId, UUID requestId, String taskName, Map<String, Serializable> data,
	    boolean stoppingRequest, boolean raceStyle) {
	this.taskId = taskId;
	this.requestId = requestId;
	this.taskName = taskName;
	if (data != null) {
	    this.data.putAll(data);
	}
	this.stoppingRequest = stoppingRequest;
	this.raceStyle = raceStyle;
	this.timeStamp = System.currentTimeMillis();
    }

    private NodeRequest(UUID taskId, UUID requestId, boolean stoppingRequest, String taskName) {
	this(taskId, requestId, taskName, null, stoppingRequest, false);
    }

    private NodeRequest(UUID taskId, boolean stoppingRequest, String taskName) {
	this(taskId, UUID.randomUUID(), taskName, null, stoppingRequest, false);
    }

    public static NodeRequest regular(UUID taskId, String taskName, Map<String, Serializable> data) {
	return new NodeRequest(taskId, UUID.randomUUID(), taskName, data, false, false);
    }

    public static NodeRequest race(UUID taskId, String taskName, Map<String, Serializable> data) {
	return new NodeRequest(taskId, UUID.randomUUID(), taskName, data, false, true);
    }

    public static NodeRequest stop(UUID taskId, String taskName) {
	return new NodeRequest(taskId, true, taskName);
    }

    public String getStringDataValue(String key) {
	Serializable value = data.get(key);
	if (value != null)
	    return value.toString();
	return "";
    }

    public static NodeRequest heartBeatRequest() {
	return new NodeRequest(UUID.randomUUID(), UUID.randomUUID(), Mappings.HEART_BEAT_TASK_NAME, null, false, false);
    }

    public UUID getTaskId() {
	return taskId;
    }

    public UUID getRequestId() {
	return requestId;
    }

    public boolean isAuthRequest() {
	if (taskName.equals(Mappings.AUTH_TASK_NAME)) {
	    return true;
	}
	return false;
    }

    public boolean isHeartBeatingRequest() {
	if (taskName.equals(Mappings.HEART_BEAT_TASK_NAME)) {
	    return true;
	}
	return false;
    }

    public String getTaskName() {
	return taskName;
    }

    public boolean isRaceStyle() {
	return raceStyle;
    }

    public boolean isStoppingRequest() {
	return stoppingRequest;
    }

    public long getTimeStamp() {
	return timeStamp;
    }

    public Map<String, Serializable> getData() {
	return data;
    }

    @Override
    public String toString() {
	return "NodeRequest [taskId=" + taskId + ", requestId=" + requestId + ", stoppingRequest=" + stoppingRequest
		+ ", raceStyle=" + raceStyle + ", taskName=" + taskName + ", timeStamp=" + timeStamp + ", data=" + data
		+ "]";
    }

}