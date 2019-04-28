package ru.babobka.nodeslaveserver.thread;

import lombok.NonNull;
import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodeslaveserver.callback.NodeResponseCallback;
import ru.babobka.nodeslaveserver.callback.NodeResponseErrorCallback;
import ru.babobka.nodeslaveserver.task.TaskRunnerService;
import ru.babobka.nodetask.TasksStorage;
import ru.babobka.nodetask.model.SubTask;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.network.NodeConnection;

/**
 * Created by dolgopolov.a on 27.07.15.
 */
public class SlaveBackedNodeRequestHandler extends Thread {

    private final NodeRequest request;
    private final SubTask subTask;
    private final TaskRunnerService taskRunnerService = Container.getInstance().get(TaskRunnerService.class);
    private final TasksStorage tasksStorage;
    private final NodeResponseCallback nodeResponseCallback;
    private final NodeResponseErrorCallback onErrorCallback;

    public SlaveBackedNodeRequestHandler(@NonNull NodeConnection connection,
                                         @NonNull TasksStorage tasksStorage,
                                         @NonNull NodeRequest request,
                                         @NonNull SubTask subTask) {
        this.request = request;
        this.subTask = subTask;
        this.tasksStorage = tasksStorage;
        nodeResponseCallback = new NodeResponseCallback(connection);
        onErrorCallback = new NodeResponseErrorCallback(request, connection);
    }

    @Override
    public void run() {
        taskRunnerService.runTask(tasksStorage, request, subTask, nodeResponseCallback, onErrorCallback);
    }

    @Override
    public void interrupt() {
        super.interrupt();
        subTask.stopProcess();
    }
}
