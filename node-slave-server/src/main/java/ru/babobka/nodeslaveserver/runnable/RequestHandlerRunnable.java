package ru.babobka.nodeslaveserver.runnable;

import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodeserials.NodeResponse;
import ru.babobka.nodeserials.enumerations.ResponseStatus;
import ru.babobka.nodeslaveserver.task.TaskRunnerService;
import ru.babobka.nodetask.TasksStorage;
import ru.babobka.nodetask.model.SubTask;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.logger.NodeLogger;
import ru.babobka.nodeutils.network.NodeConnection;

import java.io.IOException;

/**
 * Created by dolgopolov.a on 27.07.15.
 */
public class RequestHandlerRunnable implements Runnable {

    private final NodeConnection connection;
    private final NodeRequest request;
    private final SubTask subTask;
    private final NodeLogger nodeLogger = Container.getInstance().get(NodeLogger.class);
    private final TaskRunnerService taskRunnerService = Container.getInstance().get(TaskRunnerService.class);
    private final TasksStorage tasksStorage;
    private NodeResponse lastResponse;

    public RequestHandlerRunnable(NodeConnection connection, TasksStorage tasksStorage, NodeRequest request, SubTask subTask) {
        this.connection = connection;
        this.request = request;
        this.subTask = subTask;
        this.tasksStorage = tasksStorage;
    }

    @Override
    public void run() {
        try {
            NodeResponse response = taskRunnerService.runTask(tasksStorage, request, subTask);
            lastResponse = response;
            if (response.getStatus() != ResponseStatus.STOPPED) {
                connection.send(response);
                nodeLogger.info("response was sent " + response);
            } else {
                nodeLogger.warning("response was stopped " + response);
            }
        } catch (RuntimeException e) {
            nodeLogger.error(e);
            try {
                connection.send(NodeResponse.failed(request));
            } catch (IOException e1) {
                nodeLogger.error(e1);
            }

        } catch (IOException e) {
            nodeLogger.error("response wasn't sent " + lastResponse, e);
        }
    }
}