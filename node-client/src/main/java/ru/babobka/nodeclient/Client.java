package ru.babobka.nodeclient;

import ru.babobka.nodeclient.future.NodeFuture;
import ru.babobka.nodeclient.future.SingleNodeFuture;
import ru.babobka.nodeclient.listener.OnResponseListener;
import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodeserials.NodeResponse;
import ru.babobka.nodeutils.func.done.DoneFunc;
import ru.babobka.nodeutils.func.done.FlaggedDoneFunc;
import ru.babobka.nodeutils.network.NodeConnection;
import ru.babobka.nodeutils.network.NodeConnectionImpl;
import ru.babobka.nodeutils.util.TextUtil;

import java.io.Closeable;
import java.io.IOException;
import java.net.Socket;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by 123 on 12.12.2017.
 */
public class Client implements Closeable {
    private final String host;
    private final int port;
    private final ExecutorService executorService;

    //for testing
    Client(String host, int port, ExecutorService executorService) {
        if (TextUtil.isEmpty(host)) {
            throw new IllegalArgumentException("host is not set");
        } else if (!TextUtil.isValidPort(port)) {
            throw new IllegalArgumentException("invalid port " + port);
        }
        this.host = host;
        this.port = port;
        this.executorService = executorService;
    }

    public Client(String host, int port) {
        this(host, port, Executors.newCachedThreadPool(r -> {
            Thread t = Executors.defaultThreadFactory().newThread(r);
            t.setDaemon(true);
            return t;
        }));
    }

    public SingleNodeFuture executeTask(NodeRequest request) throws IOException {
        return new SingleNodeFuture(executeTask(Collections.singletonList(request), null, new FlaggedDoneFunc()));
    }

    public NodeFuture<List<NodeResponse>> executeTask(List<NodeRequest> requests, OnResponseListener listener, DoneFunc doneFunc) throws IOException {
        NodeConnection connection = createConnection(host, port);
        Future<List<NodeResponse>> future = executorService.submit(new TaskExecutorCallable(requests, connection, listener, doneFunc));
        return new NodeFuture<>(connection, future, doneFunc);
    }

    public NodeFuture<List<NodeResponse>> executeTask(List<NodeRequest> requests, OnResponseListener listener) throws IOException {
        return executeTask(requests, listener, new FlaggedDoneFunc());
    }

    public NodeFuture<List<NodeResponse>> executeTask(List<NodeRequest> requests) throws IOException {
        return executeTask(requests, null, new FlaggedDoneFunc());
    }

    private NodeConnection createConnection(String host, int port) throws IOException {
        return new NodeConnectionImpl(new Socket(host, port));
    }

    @Override
    public void close() {
        executorService.shutdownNow();
    }
}
