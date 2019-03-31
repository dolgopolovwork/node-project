package ru.babobka.nodeclient.future;

import lombok.NonNull;
import ru.babobka.nodeutils.func.done.DoneFunc;
import ru.babobka.nodeutils.network.NodeConnection;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by 123 on 24.12.2017.
 */
public class NodeFuture<V> implements Future<V> {
    private final NodeConnection connection;
    private final Future<V> future;
    private final DoneFunc doneFunc;

    public NodeFuture(@NonNull NodeConnection connection,
                      @NonNull Future<V> future,
                      @NonNull DoneFunc doneFunc) {
        this.connection = connection;
        this.future = future;
        this.doneFunc = doneFunc;
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        doneFunc.setDone();
        connection.close();
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
    public V get() throws InterruptedException, ExecutionException {
        return future.get();
    }

    @Override
    public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return future.get(timeout, unit);
    }
}
