package ru.babobka.nodeslaveserver.task;

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

    public NodeResponse runTask(TasksStorage tasksStorage, NodeRequest request, SubTask subTask) {
        try {
            if (!subTask.getDataValidators().isValidRequest(request)) {
                return NodeResponse.failed(request, "Failed validation");
            }
            Timer timer = new Timer();
            ExecutionResult result = subTask.getTaskExecutor().execute(request);
            if (result.isStopped())
                return NodeResponse.stopped(request);
            else
                return NodeResponse.normal(result.getResultMap(), request, timer.getTimePassed());

        } finally {
            tasksStorage.removeRequest(request);
        }
    }
}
