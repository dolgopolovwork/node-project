package ru.babobka.nodeclient;

import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodeserials.NodeResponse;
import ru.babobka.nodeutils.network.NodeConnection;
import ru.babobka.nodeutils.network.NodeConnectionImpl;
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

    public NodeFuture<NodeResponse> executeTask(NodeRequest request) throws IOException {
        NodeConnection connection = createConnection(host, port);
        Future<NodeResponse> future = executorService.submit(new TaskExecutorCallable(request, connection));
        return new NodeFuture<>(connection, future);
    }

    private NodeConnection createConnection(String host, int port) throws IOException {
        return new NodeConnectionImpl(new Socket(host, port));
    }

    @Override
    public void close() {
        executorService.shutdownNow();
    }
}
