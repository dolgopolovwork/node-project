package ru.babobka.nodeslaveserver.controller;

import lombok.NonNull;
import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodeserials.enumerations.RequestStatus;
import ru.babobka.nodeslaveserver.callback.NodeRequestCallback;
import ru.babobka.nodeslaveserver.callback.NodeStopTaskCallback;
import ru.babobka.nodeslaveserver.task.RaceStyleTaskStorage;
import ru.babobka.nodetask.model.StoppedTasks;
import ru.babobka.nodetask.service.TaskService;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.network.NodeConnection;
import ru.babobka.nodeutils.react.PubSub;

import java.util.concurrent.ExecutorService;

/**
 * Created by 123 on 26.03.2019.
 */
public class MasterBackedSocketController extends AbstractSocketController {

    private final PubSub<NodeRequest> requestsStream;
    private final ExecutorService threadPool;
    private final TaskService taskService = Container.getInstance().get(TaskService.class);
    private final StoppedTasks stoppedTasks;
    private final RaceStyleTaskStorage raceStyleTaskStorage;

    public MasterBackedSocketController(
            NodeConnection connection,
            @NonNull ExecutorService threadPool,
            @NonNull PubSub<NodeRequest> requestsStream,
            @NonNull RaceStyleTaskStorage raceStyleTaskStorage,
            @NonNull StoppedTasks stoppedTasks) {
        super(connection);
        this.requestsStream = requestsStream;
        this.raceStyleTaskStorage = raceStyleTaskStorage;
        this.threadPool = threadPool;
        this.stoppedTasks = stoppedTasks;
        initExecuteTaskSubscriber();
        initStopTaskSubscriber();
    }

    public MasterBackedSocketController(
            NodeConnection connection,
            ExecutorService threadPool,
            PubSub<NodeRequest> requestsStream
    ) {
        this(connection, threadPool, requestsStream, new RaceStyleTaskStorage(), new StoppedTasks());

    }

    private void initExecuteTaskSubscriber() {
        requestsStream.subscribe(request -> request.getRequestStatus() != RequestStatus.STOP,
                request -> new NodeRequestCallback(raceStyleTaskStorage, taskService, connection).callback(request),
                threadPool);
    }

    private void initStopTaskSubscriber() {
        requestsStream.subscribe(
                request -> request.getRequestStatus() == RequestStatus.STOP,
                request -> new NodeStopTaskCallback(taskService).callback(request));
    }

    @Override
    public void onStop(NodeRequest request) {
        stoppedTasks.add(request);
        requestsStream.publish(request);
    }

    @Override
    public void onExecute(NodeRequest request) {
        if (!stoppedTasks.wasStopped(request)) {
            requestsStream.publish(request);
        }
    }

    @Override
    public void close() {
        stoppedTasks.clear();
        raceStyleTaskStorage.clear();
        requestsStream.close();
    }
}
