package ru.babobka.nodeclient;

import ru.babobka.nodeserials.NodeData;
import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodeserials.NodeResponse;
import ru.babobka.nodeutils.network.NodeConnection;

import java.io.IOException;
import java.util.concurrent.Callable;

/**
 * Created by 123 on 10.12.2017.
 */
public class TaskExecutorCallable implements Callable<NodeResponse> {

    private static final int READ_TIMEOUT_MILLIS = 15_000;
    private final NodeRequest request;
    private final NodeConnection connection;

    TaskExecutorCallable(NodeRequest request, NodeConnection connection) {
        if (request == null) {
            throw new IllegalArgumentException("request is null");
        } else if (connection == null) {
            throw new IllegalArgumentException("connection is null");
        } else if (connection.isClosed()) {
            throw new IllegalArgumentException("connection is closed");
        }
        this.request = request;
        this.connection = connection;
    }

    @Override
    public NodeResponse call() {
        try {
            connection.send(request);
            return receiveResponse();
        } catch (IOException e) {
            e.printStackTrace();
            return NodeResponse.failed(request);
        } finally {
            connection.close();
        }
    }

    NodeResponse receiveResponse() throws IOException {
        while (!Thread.currentThread().isInterrupted()) {
            NodeData nodeData = connection.receive();
            if (nodeData instanceof NodeResponse) {
                return (NodeResponse) nodeData;
            }
            sendHeartBeat();
        }
        return null;
    }

    private void sendHeartBeat() {
        try {
            connection.setReadTimeOut(READ_TIMEOUT_MILLIS);
            connection.send(NodeResponse.heartBeat());
        } catch (IOException e) {
            //That's ok
            e.printStackTrace();
        }
    }
}
