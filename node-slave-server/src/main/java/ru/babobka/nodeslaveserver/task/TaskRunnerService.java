package ru.babobka.nodeslaveserver.task;

import lombok.NonNull;
import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodeserials.NodeResponse;
import ru.babobka.nodetask.TasksStorage;
import ru.babobka.nodetask.model.ExecutionResult;
import ru.babobka.nodetask.model.SubTask;
import ru.babobka.nodeutils.time.Timer;

/**
 * Created by dolgopolov.a on 08.12.15.
 */
public class TaskRunnerService {

    public NodeResponse runTask(@NonNull TasksStorage tasksStorage,
                                @NonNull NodeRequest request,
                                @NonNull SubTask subTask) {
        try {
            if (!subTask.getDataValidators().isValidRequest(request)) {
                return NodeResponse.validationError(request);
            }
            Timer timer = new Timer();
            ExecutionResult result = subTask.getTaskExecutor().execute(request);
            if (result.isStopped())
                return NodeResponse.stopped(request);
            return NodeResponse.normal(result.getData(), request, timer.getTimePassed());
        } finally {
            tasksStorage.removeRequest(request);
        }
    }
}
