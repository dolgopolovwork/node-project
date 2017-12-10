package ru.babobka.nodeslaveserver.controller;

import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodeserials.NodeResponse;
import ru.babobka.nodeserials.enumerations.RequestStatus;
import ru.babobka.nodeslaveserver.runnable.RequestHandlerRunnable;
import ru.babobka.nodeslaveserver.server.SlaveServerConfig;
import ru.babobka.nodetask.TaskPool;
import ru.babobka.nodetask.TasksStorage;
import ru.babobka.nodetask.model.SubTask;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.logger.SimpleLogger;
import ru.babobka.nodeutils.network.NodeConnection;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;

public class SocketController implements Controller<NodeConnection>, Closeable {

    private final ExecutorService threadPool;
    private final TaskPool taskPool = Container.getInstance().get("slaveServerTaskPool");
    private final SlaveServerConfig slaveServerConfig = Container.getInstance().get(SlaveServerConfig.class);
    private final SimpleLogger logger = Container.getInstance().get(SimpleLogger.class);
    private final TasksStorage tasksStorage;

    public SocketController(ExecutorService threadPool, TasksStorage tasksStorage) {
        this.threadPool = threadPool;
        this.tasksStorage = tasksStorage;
    }

    @Override
    public void control(NodeConnection connection) throws IOException {
        connection.setReadTimeOut(slaveServerConfig.getRequestTimeoutMillis());
        NodeRequest request = connection.receive();
        if (request.getRequestStatus() == RequestStatus.HEART_BEAT) {
            connection.send(NodeResponse.heartBeat());
        } else if (request.getRequestStatus() == RequestStatus.STOP) {
            logger.info("Stopping request " + request);
            tasksStorage.stopTask(request);
        } else if (request.getRequestStatus() == RequestStatus.RACE && tasksStorage.exists(request.getTaskId())) {
            logger.warning(request.getTaskName() + " is race style task. Repeated request was not handled.");
        } else if (!tasksStorage.wasStopped(request)) {
            logger.info("New request " + request);
            SubTask subTask = taskPool.get(request.getTaskName());
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