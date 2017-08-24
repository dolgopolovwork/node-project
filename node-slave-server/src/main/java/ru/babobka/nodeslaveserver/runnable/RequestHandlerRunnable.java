package ru.babobka.nodeslaveserver.runnable;

import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodeserials.NodeResponse;
import ru.babobka.nodeserials.enumerations.ResponseStatus;
import ru.babobka.nodeslaveserver.task.TaskRunnerService;
import ru.babobka.nodetask.TasksStorage;
import ru.babobka.nodetask.model.SubTask;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.logger.SimpleLogger;
import ru.babobka.nodeutils.network.NodeConnection;

import java.io.IOException;

/**
 * Created by dolgopolov.a on 27.07.15.
 */
public class RequestHandlerRunnable implements Runnable {

    private final NodeConnection connection;

    private final NodeRequest request;

    private final SubTask subTask;

    private final SimpleLogger logger = Container.getInstance().get(SimpleLogger.class);

    private final TaskRunnerService taskRunnerService = Container.getInstance().get(TaskRunnerService.class);

    private final TasksStorage tasksStorage;

    private NodeResponse lastResponse;

    public RequestHandlerRunnable(NodeConnection connection, TasksStorage tasksStorage, NodeRequest request, SubTask subTask) {
        this.connection = connection;
        this.request = request;
        this.subTask = subTask;
        this.tasksStorage = tasksStorage;
    }

    //TODO написать тест
    @Override
    public void run() {
        try {
            NodeResponse response = taskRunnerService.runTask(tasksStorage, request, subTask);
            lastResponse = response;
            if (response.getStatus() != ResponseStatus.STOPPED) {
                connection.send(response);
                logger.info("Response was sent " + response);
            }
        } catch (RuntimeException e) {
            logger.error(e);
            try {
                connection.send(NodeResponse.failed(request));
            } catch (IOException e1) {
                logger.error(e1);
            }

        } catch (IOException e) {
            //TODO какого хуя оно все равно отправляется, даже если мы отменили эту поеботу?
            logger.error("Response wasn't sent " + lastResponse, e);
        }
    }
}