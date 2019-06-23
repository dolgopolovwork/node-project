package ru.babobka.nodeclient.rpc;

import lombok.NonNull;
import ru.babobka.nodeserials.NodeResponse;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

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


    public NodeResponse getResponse(@NonNull String correlationId) throws InterruptedException, TimeoutException {
        return getResponse(correlationId, Long.MAX_VALUE);
    }

    public NodeResponse getResponse(@NonNull String correlationId, long timeoutMillis) throws InterruptedException, TimeoutException {
        LockedResponse lockedResponse = getLockedResponse(correlationId);
        if (lockedResponse != null) {
            try {
                return lockedResponse.getResponse(timeoutMillis);
            } finally {
                remove(correlationId);
            }
        }
        throw new IllegalStateException("No response with correlationId '" + correlationId + "' has been reserved");
    }

    private synchronized LockedResponse getLockedResponse(String correlationId) {
        return responses.get(correlationId);
    }

    private synchronized void remove(String correlationId) {
        responses.remove(correlationId);
    }

}
