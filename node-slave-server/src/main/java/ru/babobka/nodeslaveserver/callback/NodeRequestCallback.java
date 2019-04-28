package ru.babobka.nodeslaveserver.callback;

import lombok.NonNull;
import org.apache.log4j.Logger;
import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodeserials.NodeResponse;
import ru.babobka.nodeslaveserver.task.RaceStyleTaskStorage;
import ru.babobka.nodetask.service.TaskExecutionResult;
import ru.babobka.nodetask.service.TaskService;
import ru.babobka.nodeutils.func.Callback;
import ru.babobka.nodeutils.network.NodeConnection;

/**
 * Created by 123 on 30.03.2019.
 */
public class NodeRequestCallback implements Callback<NodeRequest> {
    private static final Logger logger = Logger.getLogger(NodeRequestCallback.class);
    private final RaceStyleTaskStorage raceStyleTaskStorage;
    private final TaskService taskService;
    private final NodeConnection connection;

    public NodeRequestCallback(
            @NonNull RaceStyleTaskStorage raceStyleTaskStorage,
            @NonNull TaskService taskService,
            @NonNull NodeConnection connection) {
        this.taskService = taskService;
        this.connection = connection;
        this.raceStyleTaskStorage = raceStyleTaskStorage;
    }

    @Override
    public void callback(NodeRequest request) {
        taskService.executeTask(request,
                result -> {
                    try {
                        if (raceStyleTaskStorage.isRepeated(request)) {
                            logger.warn(request.getTaskName() +
                                    " is a race style task. repeated request was not handled.");
                        } else if (!result.wasStopped()) {
                            callOnResponseCallback(result, request);
                        }
                    } finally {
                        raceStyleTaskStorage.unregister(request);
                    }
                }, error -> callOnErrorCallback(error, request)
        );
    }

    void callOnResponseCallback(TaskExecutionResult result, NodeRequest request) {
        new NodeResponseCallback(connection).callback(
                NodeResponse.normal(result.getData(), request, result.getTimeTakes()));
    }

    void callOnErrorCallback(Exception error, NodeRequest request) {
        new NodeResponseErrorCallback(request, connection).callback(error);
    }
}
