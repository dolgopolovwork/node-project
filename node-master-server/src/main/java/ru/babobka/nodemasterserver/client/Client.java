package ru.babobka.nodemasterserver.client;

import ru.babobka.nodemasterserver.exception.TaskExecutionException;
import ru.babobka.nodemasterserver.server.config.MasterServerConfig;
import ru.babobka.nodemasterserver.service.TaskService;
import ru.babobka.nodemasterserver.task.TaskExecutionResult;
import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodeserials.NodeResponse;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.logger.NodeLogger;
import ru.babobka.nodeutils.network.NodeConnection;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by 123 on 28.10.2017.
 */
public class Client extends AbstractClient {

    private final ClientStorage clientStorage = Container.getInstance().get(ClientStorage.class);
    private final MasterServerConfig config = Container.getInstance().get(MasterServerConfig.class);
    private final NodeLogger nodeLogger = Container.getInstance().get(NodeLogger.class);
    private final TaskService taskService = Container.getInstance().get(TaskService.class);
    private final AtomicInteger processedRequests = new AtomicInteger(0);
    private volatile boolean done;

    Client(NodeConnection connection, List<NodeRequest> requests) {
        super(connection, requests);
    }

    @Override
    public void run() {
        clientStorage.add(this);
        try {
            runExecution();
            processConnection();
        } finally {
            clientStorage.remove(this);
            close();
        }
    }

    void runExecution() {
        for (NodeRequest request : requests) {
            new Thread(new ExecutionRunnable(request)).start();
        }
    }

    void processConnection() {
        try {
            while (!isDone()) {
                connection.receive();
                connection.setReadTimeOut(config.getTime().getRequestReadTimeOutMillis());
            }
        } catch (IOException e) {
            if (!isDone()) {
                cancelTask();
                nodeLogger.error(e);
            }
        }
    }

    void close() {
        connection.close();
    }

    void cancelTask() {
        for (NodeRequest request : requests) {
            try {
                taskService.cancelTask(request.getTaskId());
            } catch (TaskExecutionException e) {
                nodeLogger.error(e);
            }
        }
        setDone();
    }

    void sendFailed() throws IOException {
        for (NodeRequest request : requests) {
            connection.send(NodeResponse.failed(request));
        }
    }

    void sendNormal(TaskExecutionResult result, NodeRequest request) throws IOException {
        connection.send(NodeResponse.normal(result.getData(), request, result.getTimeTakes()));
    }

    void sendStopped() throws IOException {
        for (NodeRequest request : requests) {
            connection.send(NodeResponse.stopped(request));
        }
    }

    boolean isDone() {
        return done;
    }

    void setDone() {
        this.done = true;
    }

    void executeTask(NodeRequest request) throws IOException {
        try {
            TaskExecutionResult result = taskService.executeTask(request);
            if (result.wasStopped()) {
                sendStopped();
            } else {
                sendNormal(result, request);
            }
            if (processedRequests.incrementAndGet() == requests.size()) {
                setDone();
            }
        } catch (TaskExecutionException e) {
            nodeLogger.error(e);
            sendFailed();
        }
    }

    private class ExecutionRunnable implements Runnable {

        private final NodeRequest request;

        ExecutionRunnable(NodeRequest request) {
            this.request = request;
        }

        @Override
        public void run() {
            try {
                executeTask(request);
            } catch (IOException e) {
                nodeLogger.error(e);
            }
        }
    }
}
