package ru.babobka.nodemasterserver.client;

import ru.babobka.nodemasterserver.exception.TaskExecutionException;
import ru.babobka.nodemasterserver.server.MasterServerConfig;
import ru.babobka.nodemasterserver.service.TaskService;
import ru.babobka.nodemasterserver.task.TaskExecutionResult;
import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodeserials.NodeResponse;
import ru.babobka.nodetask.model.StoppedTasks;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.logger.SimpleLogger;
import ru.babobka.nodeutils.network.NodeConnection;

import java.io.IOException;

/**
 * Created by 123 on 28.10.2017.
 */
public class Client extends AbstractClient {

    private final StoppedTasks stoppedTasks;
    private final ClientStorage clientStorage = Container.getInstance().get(ClientStorage.class);
    private final MasterServerConfig config = Container.getInstance().get(MasterServerConfig.class);
    private final SimpleLogger logger = Container.getInstance().get(SimpleLogger.class);
    private final TaskService taskService = Container.getInstance().get(TaskService.class);
    private volatile boolean done;

    public Client(NodeConnection connection, NodeRequest request, StoppedTasks stoppedTasks) {
        super(connection, request, stoppedTasks);
        this.stoppedTasks = stoppedTasks;
    }

    @Override
    public void run() {
        clientStorage.add(this);
        try {
            new Thread(new ExecutionRunnable()).start();
            processHeartBeating();
        } finally {
            clientStorage.remove(this);
        }
    }

    void processHeartBeating() {
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

    public void close() {
        connection.close();
    }

    void cancelTask() {
        if (stoppedTasks.wasStopped(request)) {
            return;
        }
        try {
            taskService.cancelTask(request.getTaskId());
        } catch (TaskExecutionException e) {
            logger.error(e);
        }
    }

    void executeTask() throws IOException {
        try {
            TaskExecutionResult result = taskService.executeTask(request);
            NodeResponse response = NodeResponse.normal(result.getResult(), request, result.getTimeTakes());
            connection.send(response);
        } catch (TaskExecutionException e) {
            logger.error(e);
            connection.send(NodeResponse.failed(request));
        } finally {
            setDone();
            connection.close();
        }
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
