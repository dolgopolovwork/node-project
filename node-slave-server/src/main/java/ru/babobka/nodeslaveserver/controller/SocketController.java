package ru.babobka.nodeslaveserver.controller;

import lombok.NonNull;
import org.apache.log4j.Logger;
import ru.babobka.nodeconfigs.slave.SlaveServerConfig;
import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodeserials.NodeResponse;
import ru.babobka.nodeserials.enumerations.RequestStatus;
import ru.babobka.nodeslaveserver.key.SlaveServerKey;
import ru.babobka.nodeslaveserver.thread.RequestHandlerThread;
import ru.babobka.nodetask.TaskPool;
import ru.babobka.nodetask.TasksStorage;
import ru.babobka.nodetask.model.SubTask;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.network.NodeConnection;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;

public class SocketController implements Closeable {

    private final TaskPool taskPool = Container.getInstance().get(SlaveServerKey.SLAVE_SERVER_TASK_POOL);
    private final SlaveServerConfig slaveServerConfig = Container.getInstance().get(SlaveServerConfig.class);
    private static final Logger logger = Logger.getLogger(SocketController.class);
    private final ExecutorService threadPool;
    private final TasksStorage tasksStorage;

    public SocketController(@NonNull ExecutorService threadPool,
                            @NonNull TasksStorage tasksStorage) {
        this.threadPool = threadPool;
        this.tasksStorage = tasksStorage;
    }

    public void control(@NonNull NodeConnection connection) {
        try {
            doControl(connection);
        } catch (IOException e) {
            if (!connection.isClosed()) {
                throw new IllegalStateException("cannot control", e);
            }
        }
    }

    private void doControl(NodeConnection connection) throws IOException {
        connection.setReadTimeOut(slaveServerConfig.getRequestTimeoutMillis());
        NodeRequest request = connection.receive();
        if (request.getRequestStatus() == RequestStatus.HEART_BEAT) {
            connection.send(NodeResponse.heartBeat());
        } else if (request.getRequestStatus() == RequestStatus.STOP) {
            logger.info("stopping request " + request);
            tasksStorage.stopTask(request);
        } else if (request.getRequestStatus() == RequestStatus.RACE && tasksStorage.exists(request.getTaskId())) {
            logger.warn(request.getTaskName() + " is race style task. repeated request was not handled.");
        } else if (!tasksStorage.wasStopped(request)) {
            logger.info("new request " + request);
            SubTask subTask = taskPool.get(request.getTaskName());
            tasksStorage.put(request, subTask);
            try {
                threadPool.submit(new RequestHandlerThread(connection, tasksStorage, request, subTask));
            } catch (RejectedExecutionException e) {
                logger.warn("new request was rejected", e);
            }
        }
    }

    @Override
    public void close() throws IOException {
        threadPool.shutdownNow();
    }
}