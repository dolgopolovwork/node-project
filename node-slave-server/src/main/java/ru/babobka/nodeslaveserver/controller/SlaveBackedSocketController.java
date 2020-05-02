package ru.babobka.nodeslaveserver.controller;

import lombok.NonNull;
import org.apache.log4j.Logger;
import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodeserials.enumerations.RequestStatus;
import ru.babobka.nodeutils.key.SlaveServerKey;
import ru.babobka.nodeslaveserver.thread.SlaveBackedNodeRequestHandler;
import ru.babobka.nodetask.TaskPool;
import ru.babobka.nodetask.TasksStorage;
import ru.babobka.nodetask.model.SubTask;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.network.NodeConnection;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;

/**
 * Created by 123 on 26.03.2019.
 */
public class SlaveBackedSocketController extends AbstractSocketController {

    private static final Logger logger = Logger.getLogger(SlaveBackedSocketController.class);
    private final TaskPool taskPool = Container.getInstance().get(SlaveServerKey.SLAVE_SERVER_TASK_POOL);
    private final ExecutorService threadPool;
    private final TasksStorage tasksStorage;

    public SlaveBackedSocketController(
            NodeConnection connection,
            @NonNull TasksStorage tasksStorage,
            @NonNull ExecutorService threadPool) {
        super(connection);
        this.tasksStorage = tasksStorage;
        this.threadPool = threadPool;
    }

    @Override
    public void onStop(NodeRequest request) {
        tasksStorage.stopTask(request);
    }

    @Override
    public void onExecute(NodeRequest request) {
        if (request.getRequestStatus() == RequestStatus.RACE && tasksStorage.exists(request.getTaskId())) {
            logger.warn(request.getTaskName() + " is race style task. repeated request was not handled.");
        } else if (!tasksStorage.wasStopped(request)) {
            logger.info("new request " + request);
            SubTask subTask = getTaskByRequest(request);
            tasksStorage.put(request, subTask);
            try {
                threadPool.submit(new SlaveBackedNodeRequestHandler(connection, tasksStorage, request, subTask));
            } catch (RejectedExecutionException e) {
                logger.warn("request of task " + request.getTaskId() + " was rejected", e);
            }
        }
    }

    private SubTask getTaskByRequest(NodeRequest request) {
        try {
            return taskPool.get(request.getTaskName());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() {
        threadPool.shutdownNow();
    }
}
