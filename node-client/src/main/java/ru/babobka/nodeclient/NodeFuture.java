package ru.babobka.nodeclient;

import ru.babobka.nodeutils.network.NodeConnection;
import ru.babobka.nodeutils.util.ArrayUtil;

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

    public NodeFuture(NodeConnection connection, Future<V> future) {
        if (ArrayUtil.isNull(connection, future)) {
            throw new IllegalArgumentException("All the values must be set");
        }
        this.connection = connection;
        this.future = future;
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
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
