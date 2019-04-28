package ru.babobka.nodeclient.future;

import lombok.NonNull;
import ru.babobka.nodeserials.NodeResponse;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by 123 on 21.07.2018.
 */
public class SingleNodeFuture implements Future<NodeResponse> {
    private final Future<List<NodeResponse>> future;

    public SingleNodeFuture(@NonNull Future<List<NodeResponse>> future) {
        this.future = future;
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return future.cancel(mayInterruptIfRunning);
    }

    @Override
    public boolean isCancelled() {
        return future.isCancelled();
    }

    @Override
    public boolean isDone() {
        return future.isDone();
    }

    @Override
    public NodeResponse get() throws InterruptedException, ExecutionException {
        List<NodeResponse> responses = future.get();
        if (responses != null && !responses.isEmpty()) {
            return responses.get(0);
        }
        throw new IllegalStateException("No responses");
    }

    @Override
    public NodeResponse get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        List<NodeResponse> responses = future.get(timeout, unit);
        if (responses != null && !responses.isEmpty()) {
            return responses.get(0);
        }
        return null;
    }
}
