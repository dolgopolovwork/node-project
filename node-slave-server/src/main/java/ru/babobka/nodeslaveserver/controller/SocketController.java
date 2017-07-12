package ru.babobka.nodeslaveserver.controller;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;

import ru.babobka.nodeutils.logger.SimpleLogger;
import ru.babobka.nodeutils.network.NodeConnection;
import ru.babobka.nodeslaveserver.runnable.RequestHandlerRunnable;
import ru.babobka.nodeslaveserver.server.SlaveServerConfig;
import ru.babobka.nodeslaveserver.task.TaskPool;
import ru.babobka.nodeslaveserver.task.TasksStorage;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodeserials.NodeResponse;
import ru.babobka.subtask.model.SubTask;

public class SocketController implements Controller<NodeConnection>, Closeable {

    private static final int MAX_POOL_SIZE = 10;

    private final ExecutorService threadPool = Executors.newFixedThreadPool(MAX_POOL_SIZE);

    private final TaskPool taskPool = Container.getInstance().get(TaskPool.class);

    private final SlaveServerConfig slaveServerConfig = Container.getInstance().get(SlaveServerConfig.class);

    private final SimpleLogger logger = Container.getInstance().get(SimpleLogger.class);

    private final TasksStorage tasksStorage;

    public SocketController(TasksStorage tasksStorage) {
        this.tasksStorage = tasksStorage;
    }

    @Override
    public void control(NodeConnection connection) throws IOException {

        connection.setReadTimeOut(slaveServerConfig.getRequestTimeoutMillis());
        NodeRequest request = connection.receive();
        if (request.isHeartBeatingRequest()) {
            connection.send(NodeResponse.heartBeat());
        } else if (request.isStoppingRequest()) {
            logger.info("Stopping request " + request);
            tasksStorage.stopTask(request.getTaskId(), request.getTimeStamp());
        } else if (request.isRaceStyle() && tasksStorage.exists(request.getTaskId())) {
            logger.warning(request.getTaskName() + " is race style task. Repeated request was not handled.");
        } else if (!tasksStorage.wasStopped(request.getTaskId(), request.getTimeStamp())) {
            logger.info("Got request " + request);
            SubTask subTask = taskPool.get(request.getTaskName()).getTask();
            tasksStorage.put(request, subTask);
            try {
                threadPool.submit(new RequestHandlerRunnable(connection, tasksStorage, request, subTask));
            } catch (RejectedExecutionException e) {
                logger.warning("New request was rejected", e);
            }
        }
    }

    @Override
    public void close() throws IOException {
        threadPool.shutdownNow();
    }

}
