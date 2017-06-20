package ru.babobka.nodeslaveserver.task;

import ru.babobka.nodeslaveserver.model.Timer;
import ru.babobka.nodeslaveserver.server.SlaveServerConfig;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodeserials.NodeResponse;
import ru.babobka.subtask.model.ExecutionResult;
import ru.babobka.subtask.model.SubTask;
import ru.babobka.subtask.model.ValidationResult;

/**
 * Created by dolgopolov.a on 08.12.15.
 */
public class TaskRunnerService {

    private final SlaveServerConfig config = Container.getInstance().get(SlaveServerConfig.class);

    public NodeResponse runTask(TasksStorage tasksStorage, NodeRequest request, SubTask subTask) {
        try {
            ValidationResult validationResult = subTask.validateRequest(request);
            if (validationResult.isValid()) {
                Timer timer = new Timer();
                ExecutionResult result = subTask.execute(config.getThreads(), request);
                if (result.isStopped()) {
                    return NodeResponse.stopped(request);
                } else {
                    return NodeResponse.normal(result.getResultMap(), request, timer.getTimePassed());
                }
            } else {
                return NodeResponse.failed(request, validationResult.getMessage());
            }

        } finally {
            tasksStorage.removeRequest(request);
        }
    }

}
