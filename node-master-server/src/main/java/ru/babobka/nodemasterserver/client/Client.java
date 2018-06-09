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

/**
 * Created by 123 on 28.10.2017.
 */
public class Client extends AbstractClient {

    private final ClientStorage clientStorage = Container.getInstance().get(ClientStorage.class);
    private final MasterServerConfig config = Container.getInstance().get(MasterServerConfig.class);
    private final NodeLogger nodeLogger = Container.getInstance().get(NodeLogger.class);
    private final TaskService taskService = Container.getInstance().get(TaskService.class);
    private volatile boolean done;

    Client(NodeConnection connection, NodeRequest request) {
        super(connection, request);
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
        new Thread(new ExecutionRunnable()).start();
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
        try {
            taskService.cancelTask(request.getTaskId());
        } catch (TaskExecutionException e) {
            nodeLogger.error(e);
        }
        setDone();
    }

    void sendFailed() throws IOException {
        connection.send(NodeResponse.failed(request));
    }

    void sendNormal(TaskExecutionResult result) throws IOException {
        connection.send(NodeResponse.normal(result.getData(), request, result.getTimeTakes()));
    }

    void sendStopped() throws IOException {
        connection.send(NodeResponse.stopped(request));
    }

    boolean isDone() {
        return done;
    }

    void setDone() {
        this.done = true;
    }

    void executeTask() throws IOException {
        try {
            TaskExecutionResult result = taskService.executeTask(request);
            if (result.wasStopped()) {
                sendStopped();
            } else {
                sendNormal(result);
            }
            setDone();
        } catch (TaskExecutionException e) {
            nodeLogger.error(e);
            sendFailed();
        }
    }

    private class ExecutionRunnable implements Runnable {

        @Override
        public void run() {
            try {
                executeTask();
            } catch (IOException e) {
                nodeLogger.error(e);
            }
        }
    }
}
