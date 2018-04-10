package ru.babobka.nodeserials;

import ru.babobka.nodeserials.enumerations.ResponseStatus;

import java.io.Serializable;
import java.util.Map;
import java.util.UUID;

public class NodeResponse extends NodeData {

    private static final UUID DUMMY_UUID = new UUID(0, 0);
    private static final long serialVersionUID = -1071154624719215439L;
    private final long timeTakes;
    private final String message;
    private volatile ResponseStatus status;

    public NodeResponse(UUID id, UUID taskId, long timeTakes, ResponseStatus status, String message,
                        Map<String, Serializable> data, String taskName) {
        super(id, taskId, taskName, System.currentTimeMillis(), data);
        this.timeTakes = timeTakes;
        this.status = status;
        this.message = message;
    }

    public NodeResponse(UUID taskId, ResponseStatus status) {
        this(UUID.randomUUID(), taskId, -1, status, null, null, null);
    }

    public NodeResponse(UUID taskId, ResponseStatus status, String taskName) {
        this(UUID.randomUUID(), taskId, 0, status, null, null, taskName);
    }

    public static NodeResponse failed(NodeRequest request) {
        return new NodeResponse(request.getId(), request.getTaskId(), -1, ResponseStatus.FAILED, null, null, request.getTaskName());
    }

    public static NodeResponse failed(UUID taskId) {
        return new NodeResponse(taskId, ResponseStatus.FAILED);
    }

    public static NodeResponse failed(NodeRequest request, String message) {
        return new NodeResponse(request.getId(), request.getTaskId(), -1, ResponseStatus.FAILED, message,
                null, request.getTaskName());
    }

    public static NodeResponse stopped(NodeRequest request) {
        return new NodeResponse(request.getId(), request.getTaskId(), -1, ResponseStatus.STOPPED, null,
                null, request.getTaskName());
    }

    public static NodeResponse dummy(UUID taskId) {
        return new NodeResponse(taskId, ResponseStatus.NORMAL);
    }

    public static NodeResponse dummy(NodeRequest request) {
        return dummy(request.getTaskId());
    }

    public static NodeResponse heartBeat() {
        return new NodeResponse(DUMMY_UUID, DUMMY_UUID, 0, ResponseStatus.HEART_BEAT, null, null,
                null);
    }

    public static NodeResponse stopped(UUID taskId) {
        return new NodeResponse(taskId, ResponseStatus.STOPPED);
    }

    public static NodeResponse normal(Map<String, Serializable> result, NodeRequest request, long timePassed) {
        return new NodeResponse(request.getId(), request.getTaskId(), timePassed, ResponseStatus.NORMAL,
                null, result, request.getTaskName());
    }

    public long getTimeTakes() {
        return timeTakes;
    }

    public String getMessage() {
        return message;
    }

    public ResponseStatus getStatus() {
        return status;
    }

    public void setStatus(ResponseStatus status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        NodeResponse that = (NodeResponse) o;

        if (timeTakes != that.timeTakes) return false;
        return status == that.status && (message != null ? message.equals(that.message) : that.message == null);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (int) (timeTakes ^ (timeTakes >>> 32));
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + (message != null ? message.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "NodeResponse{" +
                "timeTakes=" + timeTakes +
                ", status=" + status +
                ", message='" + message + '\'' +
                "} " + super.toString();
    }
}
