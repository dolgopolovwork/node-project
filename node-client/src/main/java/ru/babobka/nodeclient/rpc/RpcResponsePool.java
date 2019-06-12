package ru.babobka.nodeclient.rpc;

import lombok.NonNull;
import ru.babobka.nodeserials.NodeResponse;

import java.util.HashMap;
import java.util.Map;

public class RpcResponsePool {

    private final Map<String, LockedResponse> responses = new HashMap<>();

    public synchronized void reserveResponse(@NonNull String correlationId) {
        responses.put(correlationId, new LockedResponse());
    }

    public synchronized void putResponse(@NonNull String correlationId, @NonNull NodeResponse response) {
        LockedResponse lockedResponse = responses.get(correlationId);
        if (lockedResponse != null) {
            lockedResponse.unlock(response);
        }
    }

    public NodeResponse getResponse(@NonNull String correlationId) throws InterruptedException {
        LockedResponse lockedResponse = getLockedResponse(correlationId);
        if (lockedResponse != null) {
            try {
                return lockedResponse.getResponse();
            } finally {
                remove(correlationId);
            }
        }
        throw new IllegalStateException("no response with correlationId '" + correlationId + "' has been reserved");
    }

    private synchronized LockedResponse getLockedResponse(String correlationId) {
        return responses.get(correlationId);
    }

    private synchronized void remove(String correlationId) {
        responses.remove(correlationId);
    }

}
