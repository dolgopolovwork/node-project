package ru.babobka.nodeserials;

import ru.babobka.nodeserials.enumerations.RequestStatus;

import java.io.Serializable;
import java.util.Map;
import java.util.UUID;

/**
 * Created by dolgopolov.a on 08.07.15.
 */

public class NodeRequest extends NodeData {

    private static final long serialVersionUID = -7966050005036288334L;
    private final RequestStatus requestStatus;

    private NodeRequest(UUID taskId, String taskName, Map<String, Serializable> data, RequestStatus requestStatus) {
        super(UUID.randomUUID(), taskId, taskName, System.currentTimeMillis(), data);
        this.requestStatus = requestStatus;
    }

    public static NodeRequest regular(UUID taskId, String taskName, Map<String, Serializable> data) {
        return new NodeRequest(taskId, taskName, data, RequestStatus.NORMAL);
    }

    public static NodeRequest race(UUID taskId, String taskName, Map<String, Serializable> data) {
        return new NodeRequest(taskId, taskName, data, RequestStatus.RACE);
    }

    public static NodeRequest stop(UUID taskId) {
        return new NodeRequest(taskId, null, null, RequestStatus.STOP);
    }

    public static NodeRequest heartBeatRequest() {
        return new NodeRequest(UUID.randomUUID(), null, null, RequestStatus.HEART_BEAT);
    }

    public RequestStatus getRequestStatus() {
        return requestStatus;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        NodeRequest request = (NodeRequest) o;

        return requestStatus == request.requestStatus;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (requestStatus != null ? requestStatus.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "NodeRequest{" +
                "requestStatus=" + requestStatus +
                "} " + super.toString();
    }
}