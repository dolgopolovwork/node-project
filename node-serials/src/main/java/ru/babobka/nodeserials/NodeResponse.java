package ru.babobka.nodeserials;

import ru.babobka.nodeserials.data.Data;
import ru.babobka.nodeserials.enumerations.ResponseStatus;

import java.util.Objects;
import java.util.UUID;

public class NodeResponse extends NodeData {

    private static final long serialVersionUID = -1071154624719215439L;
    private final long timeTakes;
    private final String message;
    private volatile ResponseStatus status;

    public NodeResponse(UUID id, UUID taskId, long timeTakes, ResponseStatus status, String message,
                        Data data, String taskName, long timeStamp) {
        super(id, taskId, taskName, timeStamp, data);
        this.timeTakes = timeTakes;
        this.status = status;
        this.message = message;
    }

    public NodeResponse(UUID taskId, ResponseStatus status) {
        this(UUID.randomUUID(), taskId, -1, status, null, null, null, System.currentTimeMillis());
    }

    public NodeResponse(UUID taskId, ResponseStatus status, String taskName) {
        this(UUID.randomUUID(), taskId, -1, status, null, null, taskName, System.currentTimeMillis());
    }

    public static NodeResponse validationError(NodeRequest request) {
        return new NodeResponse(request.getId(), request.getTaskId(), -1, ResponseStatus.VALIDATION_ERROR, "Failed validation", null, request.getTaskName(), System.currentTimeMillis());
    }

    public static NodeResponse validationError(UUID taskId) {
        return new NodeResponse(taskId, ResponseStatus.VALIDATION_ERROR);
    }

    public static NodeResponse validationError(NodeRequest request, String message) {
        return new NodeResponse(request.getId(), request.getTaskId(), -1, ResponseStatus.VALIDATION_ERROR, message,
                null, request.getTaskName(), System.currentTimeMillis());
    }

    public static NodeResponse noNodesError(NodeRequest request, String message) {
        return new NodeResponse(request.getId(), request.getTaskId(), -1, ResponseStatus.NO_NODES, message,
                null, request.getTaskName(), System.currentTimeMillis());
    }

    public static NodeResponse systemError(NodeRequest request) {
        return new NodeResponse(request.getId(), request.getTaskId(), -1, ResponseStatus.SYSTEM_ERROR, null, null, request.getTaskName(), System.currentTimeMillis());
    }

    public static NodeResponse systemError(UUID taskId) {
        return new NodeResponse(taskId, ResponseStatus.SYSTEM_ERROR);
    }

    public static NodeResponse systemError(NodeRequest request, String message) {
        return new NodeResponse(request.getId(), request.getTaskId(), -1, ResponseStatus.SYSTEM_ERROR, message,
                null, request.getTaskName(), System.currentTimeMillis());
    }

    public static NodeResponse stopped(NodeRequest request) {
        return new NodeResponse(request.getId(), request.getTaskId(), -1, ResponseStatus.STOPPED, null,
                null, request.getTaskName(), System.currentTimeMillis());
    }

    public static NodeResponse dummy(UUID taskId) {
        return new NodeResponse(taskId, ResponseStatus.NORMAL);
    }

    public static NodeResponse dummy(NodeRequest request) {
        return dummy(request.getTaskId());
    }

    public static NodeResponse heartBeat() {
        return new NodeResponse(DUMMY_UUID, DUMMY_UUID, -1, ResponseStatus.HEART_BEAT, null, null,
                null, System.currentTimeMillis());
    }

    public static NodeResponse death() {
        return new NodeResponse(DUMMY_UUID, DUMMY_UUID, -1, ResponseStatus.DEATH, null, null,
                null, System.currentTimeMillis());
    }

    public static NodeResponse stopped(UUID taskId) {
        return new NodeResponse(taskId, ResponseStatus.STOPPED);
    }

    public static NodeResponse normal(Data result, NodeRequest request, long timePassed) {
        return new NodeResponse(request.getId(), request.getTaskId(), timePassed, ResponseStatus.NORMAL,
                null, result, request.getTaskName(), System.currentTimeMillis());
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
        return status == that.status && (Objects.equals(message, that.message));
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
