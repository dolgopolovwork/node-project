package ru.babobka.nodeslaveserver.callback;

import lombok.NonNull;
import org.apache.log4j.Logger;
import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodetask.service.TaskService;
import ru.babobka.nodeutils.func.Callback;

/**
 * Created by 123 on 30.03.2019.
 */
public class NodeStopTaskCallback implements Callback<NodeRequest> {

    private static final Logger logger = Logger.getLogger(NodeStopTaskCallback.class);
    private final TaskService taskService;

    public NodeStopTaskCallback(@NonNull TaskService taskService) {
        this.taskService = taskService;
    }

    @Override
    public void callback(NodeRequest request) {
        taskService.cancelTask(request.getTaskId(), cancelled -> {
            if (cancelled) {
                logger.error("task " + request.getTaskId() + " was successfully canceled");
            } else {
                logger.error("cannot cancel task " + request.getTaskId());
            }
        }, error -> logger.error("cannot cancel task " + request.getTaskId(), error));
    }
}
