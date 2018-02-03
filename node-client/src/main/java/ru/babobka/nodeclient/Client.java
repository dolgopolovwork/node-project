package ru.babobka.nodeclient;

import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodeserials.NodeResponse;
import ru.babobka.nodeutils.network.NodeConnection;
import ru.babobka.nodeutils.util.TextUtil;

import java.io.Closeable;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by 123 on 12.12.2017.
 */
public class Client implements Closeable {
    private final String host;
    private final int port;
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    public Client(String host, int port) {
        if (TextUtil.isEmpty(host)) {
            throw new IllegalArgumentException("host is not set");
        } else if (port < 0 || port > 65535) {
            throw new IllegalArgumentException("invalid port " + port);
        }
        this.host = host;
        this.port = port;
    }

    public NodeFuture<NodeResponse> executeTask(NodeRequest request) throws IOException {
        NodeConnection connection = new NodeConnection(new Socket(host, port));
        Future<NodeResponse> future = executorService.submit(new TaskExecutorCallable(request, connection));
        return new NodeFuture<>(connection, future);
    }

    @Override
    public void close() {
        executorService.shutdownNow();
    }
}
