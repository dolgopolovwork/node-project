package ru.babobka.nodeserials;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class NodeResponse implements Serializable {

    private static final long serialVersionUID = 9L;

    public enum Status {
        NORMAL, STOPPED, FAILED
    }

    private static final UUID DUMMY_UUID = new UUID(0, 0);

    private final String taskName;

    private final UUID taskId;

    private final UUID responseId;

    private final long timeTakes;

    private final long timeStamp;

    private final Status status;

    private final String message;

    private final Map<String, Serializable> data = new HashMap<>();

    public NodeResponse(UUID taskId, UUID responseId, long timeTakes, Status status, String message,
                        Map<String, Serializable> dataMap, String taskName) {
        this.taskId = taskId;
        this.responseId = responseId;
        this.timeTakes = timeTakes;
        this.status = status;
        this.message = message;
        this.taskName = taskName;
        if (dataMap != null) {
            this.data.putAll(dataMap);
        }
        this.timeStamp = System.currentTimeMillis();
    }

    public NodeResponse(UUID taskId, long timeTakes, Status status, String message, Map<String, Serializable> dataMap,
                        String taskName) {
        this(taskId, UUID.randomUUID(), timeTakes, status, message, dataMap, taskName);
    }

    public NodeResponse(UUID taskId, Status status) {
        this(taskId, UUID.randomUUID(), -1, status, null, null, null);
    }

    public NodeResponse(UUID taskId, Status status, String taskName) {
        this(taskId, UUID.randomUUID(), 0, status, null, null, taskName);
    }

    public static NodeResponse failed(UUID taskId) {
        return new NodeResponse(taskId, NodeResponse.Status.FAILED);
    }

    public static NodeResponse failed(NodeRequest request, String message) {
        return new NodeResponse(request.getTaskId(), request.getRequestId(), -1, NodeResponse.Status.FAILED, message,
                null, request.getTaskName());
    }

    public static NodeResponse stopped(NodeRequest request) {
        return new NodeResponse(request.getTaskId(), request.getRequestId(), -1, NodeResponse.Status.STOPPED, null,
                null, request.getTaskName());
    }

    public static NodeResponse dummy(UUID taskId) {
        return new NodeResponse(taskId, NodeResponse.Status.NORMAL);
    }

    public static NodeResponse heartBeat() {
        return new NodeResponse(DUMMY_UUID, DUMMY_UUID, 0, NodeResponse.Status.NORMAL, null, null,
                Mappings.HEART_BEAT_TASK_NAME);
    }

    public static NodeResponse stopped(UUID taskId) {
        return new NodeResponse(taskId, NodeResponse.Status.STOPPED);
    }

    public static NodeResponse normal(Map<String, Serializable> result, NodeRequest request, long timePassed) {
        return new NodeResponse(request.getTaskId(), request.getRequestId(), timePassed, NodeResponse.Status.NORMAL,
                null, result, request.getTaskName());
    }

    public <T> T getDataValue(String key) {
        return getDataValue(key, null);
    }

    public <T> T getDataValue(String key, T defaultValue) {
        Serializable value = data.get(key);
        if (value == null) {
            return defaultValue;
        }
        return (T) value;
    }


    public long getTimeTakes() {
        return timeTakes;
    }

    public String getMessage() {
        return message;
    }

    public UUID getTaskId() {
        return taskId;
    }

    public UUID getResponseId() {
        return responseId;
    }

    public Status getStatus() {
        return status;
    }

    public String getTaskName() {
        return taskName;
    }

    public boolean isHeartBeatingResponse() {
        return Mappings.HEART_BEAT_TASK_NAME.equals(taskName);
    }

    public boolean isAuthResponse() {
        return Mappings.AUTH_TASK_NAME.equals(taskName);
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public Map<String, Serializable> getData() {
        return data;
    }

    public boolean isStopped() {
        return this.status == Status.STOPPED;
    }

    @Override
    public String toString() {
        return "NodeResponse [taskName=" + taskName + ", taskId=" + taskId + ", responseId=" + responseId
                + ", timeTakes=" + timeTakes + ", timeStamp=" + timeStamp + ", status=" + status + ", message="
                + message + ", data=" + data + "]";
    }


}
