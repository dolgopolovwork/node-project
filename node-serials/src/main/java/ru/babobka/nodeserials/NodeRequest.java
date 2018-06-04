package ru.babobka.nodeserials;

import ru.babobka.nodeserials.data.Data;
import ru.babobka.nodeserials.enumerations.RequestStatus;

import java.util.UUID;

/**
 * Created by dolgopolov.a on 08.07.15.
 */

public class NodeRequest extends NodeData {

    private static final long serialVersionUID = -7966050005036288334L;
    private final RequestStatus requestStatus;

    protected NodeRequest(UUID id, UUID taskId, String taskName, Data data, RequestStatus requestStatus, long timeStamp) {
        super(id, taskId, taskName, timeStamp, data);
        this.requestStatus = requestStatus;
    }

    public static NodeRequest regular(UUID taskId, String taskName, Data data) {
        return new NodeRequest(UUID.randomUUID(), taskId, taskName, data, RequestStatus.NORMAL, System.currentTimeMillis());
    }

    public static NodeRequest regular(UUID taskId, String taskName, Data data, long timeStamp) {
        return new NodeRequest(UUID.randomUUID(), taskId, taskName, data, RequestStatus.NORMAL, timeStamp);
    }

    public static NodeRequest race(UUID taskId, String taskName, Data data) {
        return new NodeRequest(UUID.randomUUID(), taskId, taskName, data, RequestStatus.RACE, System.currentTimeMillis());
    }

    public static NodeRequest stop(UUID taskId) {
        return new NodeRequest(UUID.randomUUID(), taskId, null, null, RequestStatus.STOP, System.currentTimeMillis());
    }

    public static NodeRequest heartBeat() {
        return new NodeRequest(DUMMY_UUID, DUMMY_UUID, null, null, RequestStatus.HEART_BEAT, System.currentTimeMillis());
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

    public int cacheKey() {
        return getData().hashCode() ^ getTaskName().hashCode();
    }

    @Override
    public String toString() {
        return "NodeRequest{" +
                "requestStatus=" + requestStatus +
                "} " + super.toString();
    }
}