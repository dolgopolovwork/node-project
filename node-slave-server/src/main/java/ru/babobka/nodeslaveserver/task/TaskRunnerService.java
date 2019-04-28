package ru.babobka.nodeslaveserver.task;

import lombok.NonNull;
import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodeserials.NodeResponse;
import ru.babobka.nodetask.TasksStorage;
import ru.babobka.nodetask.model.ExecutionResult;
import ru.babobka.nodetask.model.SubTask;
import ru.babobka.nodeutils.func.Callback;
import ru.babobka.nodeutils.time.Timer;

/**
 * Created by dolgopolov.a on 08.12.15.
 */
public class TaskRunnerService {

    public void runTask(@NonNull TasksStorage tasksStorage,
                        @NonNull NodeRequest request,
                        @NonNull SubTask subTask,
                        @NonNull Callback<NodeResponse> onResponseCallback,
                        @NonNull Callback<Exception> onExceptionCallback) {
        try {
            if (!subTask.getDataValidators().isValidRequest(request)) {
                onResponseCallback.callback(NodeResponse.validationError(request));
                return;
            }
            Timer timer = new Timer();
            ExecutionResult result = subTask.getTaskExecutor().execute(request);
            if (result.isStopped()) {
                onResponseCallback.callback(NodeResponse.stopped(request));
                return;
            }
            onResponseCallback.callback(NodeResponse.normal(result.getData(), request, timer.getTimePassed()));
        }
        catch (Exception e){
            onExceptionCallback.callback(e);
        }
        finally {
            tasksStorage.removeRequest(request);
        }
    }
}
