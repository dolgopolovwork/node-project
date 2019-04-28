package ru.babobka.nodeclient;

import lombok.NonNull;
import org.apache.log4j.Logger;
import ru.babobka.nodeclient.listener.ListenerResult;
import ru.babobka.nodeclient.listener.OnResponseListener;
import ru.babobka.nodeserials.NodeData;
import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodeserials.NodeResponse;
import ru.babobka.nodeutils.func.done.DoneFunc;
import ru.babobka.nodeutils.network.NodeConnection;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by 123 on 10.12.2017.
 */
public class TaskExecutorCallable implements Callable<List<NodeResponse>> {

    private static final Logger logger = Logger.getLogger(TaskExecutorCallable.class);
    private static final OnResponseListener DUMMY_LISTENER = response -> ListenerResult.PROCEED;
    private static final int READ_TIMEOUT_MILLIS = 15_000;
    private final List<NodeRequest> requests;
    private final NodeConnection connection;
    private final List<NodeResponse> responses;
    private final OnResponseListener listener;
    private final DoneFunc doneFunc;


    TaskExecutorCallable(@NonNull List<NodeRequest> requests,
                         @NonNull NodeConnection connection,
                         OnResponseListener listener,
                         @NonNull DoneFunc doneFunc) {
        if (connection.isClosed()) {
            throw new IllegalArgumentException("cannot execute task. 'connection' is closed.");
        }
        this.doneFunc = doneFunc;
        this.requests = requests;
        this.connection = connection;
        this.responses = new ArrayList<>(requests.size());
        if (listener == null)
            this.listener = DUMMY_LISTENER;
        else
            this.listener = listener;
    }

    @Override
    public List<NodeResponse> call() {
        try {
            connection.send(requests);
            for (int i = 0; i < requests.size(); i++) {
                if (doneFunc.isDone()) {
                    break;
                }
                NodeResponse response = receiveResponse();
                if (response == null) {
                    continue;
                }
                responses.add(response);
                if (listener.onResponse(response) == ListenerResult.STOP) {
                    doneFunc.setDone();
                    break;
                }
            }
        } catch (IOException e) {
            printIfNotDone(e);
        } finally {
            connection.close();
        }
        return responses;
    }

    NodeResponse receiveResponse() throws IOException {
        while (!doneFunc.isDone() && !Thread.currentThread().isInterrupted()) {
            NodeData nodeData = connection.receive();
            if (nodeData instanceof NodeResponse) {
                return (NodeResponse) nodeData;
            }
            sendHeartBeat();
        }
        return null;
    }

    void sendHeartBeat() {
        try {
            connection.setReadTimeOut(READ_TIMEOUT_MILLIS);
            connection.send(NodeResponse.heartBeat());
        } catch (IOException e) {
            //that's ok
            printIfNotDone(e);
        }
    }

    private void printIfNotDone(Exception e) {
        if (!(doneFunc.isDone() || connection.isClosed())) {
            logger.error("exception thrown", e);
        }
    }
}
