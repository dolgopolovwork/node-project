package ru.babobka.nodemasterserver.client;

import ru.babobka.nodemasterserver.exception.TaskExecutionException;
import ru.babobka.nodemasterserver.server.MasterServerConfig;
import ru.babobka.nodemasterserver.service.TaskService;
import ru.babobka.nodemasterserver.task.TaskExecutionResult;
import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodeserials.NodeResponse;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.logger.SimpleLogger;
import ru.babobka.nodeutils.network.NodeConnection;

import java.io.IOException;

/**
 * Created by 123 on 28.10.2017.
 */
public class Client extends AbstractClient {

    private final ClientStorage clientStorage = Container.getInstance().get(ClientStorage.class);
    private final MasterServerConfig config = Container.getInstance().get(MasterServerConfig.class);
    private final SimpleLogger logger = Container.getInstance().get(SimpleLogger.class);
    private final TaskService taskService = Container.getInstance().get(TaskService.class);
    private volatile boolean done;

    Client(NodeConnection connection, NodeRequest request) {
        super(connection, request);
    }

    @Override
    public void run() {
        clientStorage.add(this);
        try {
            new Thread(new ExecutionRunnable()).start();
            processConnection();
        } finally {
            clientStorage.remove(this);
            close();
        }
    }

    void processConnection() {
        try {
            while (!isDone()) {
                connection.receive();
                connection.setReadTimeOut(config.getRequestTimeOutMillis());
            }
        } catch (IOException e) {
            if (!isDone()) {
                cancelTask();
                logger.error(e);
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
            logger.error(e);
        }
        setDone();
    }

    void executeTask() throws IOException {
        try {
            TaskExecutionResult result = taskService.executeTask(request);
            if (result.isWasStopped()) {
                sendStopped();
            } else {
                sendNormal(result);
            }
            setDone();
        } catch (TaskExecutionException e) {
            logger.error(e);
            sendFailed();
        } finally {
            close();
        }
    }

    void sendFailed() throws IOException {
        connection.send(NodeResponse.failed(request));
    }

    void sendNormal(TaskExecutionResult result) throws IOException {
        connection.send(NodeResponse.normal(result.getResult(), request, result.getTimeTakes()));
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

    private class ExecutionRunnable implements Runnable {

        @Override
        public void run() {
            try {
                executeTask();
            } catch (IOException e) {
                logger.error(e);
            }
        }
    }
}
