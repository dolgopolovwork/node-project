package ru.babobka.nodeslaveserver.thread;

import lombok.NonNull;
import org.apache.log4j.Logger;
import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodeserials.NodeResponse;
import ru.babobka.nodeserials.enumerations.ResponseStatus;
import ru.babobka.nodeslaveserver.task.TaskRunnerService;
import ru.babobka.nodetask.TasksStorage;
import ru.babobka.nodetask.model.SubTask;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.network.NodeConnection;

import java.io.IOException;

/**
 * Created by dolgopolov.a on 27.07.15.
 */
public class RequestHandlerThread extends Thread {

    private static final Logger logger = Logger.getLogger(RequestHandlerThread.class);
    private final NodeConnection connection;
    private final NodeRequest request;
    private final SubTask subTask;
    private final TaskRunnerService taskRunnerService = Container.getInstance().get(TaskRunnerService.class);
    private final TasksStorage tasksStorage;
    private NodeResponse lastResponse;

    public RequestHandlerThread(@NonNull NodeConnection connection,
                                @NonNull TasksStorage tasksStorage,
                                @NonNull NodeRequest request,
                                @NonNull SubTask subTask) {
        this.connection = connection;
        this.request = request;
        this.subTask = subTask;
        this.tasksStorage = tasksStorage;
        setName("request handler thread");
    }

    @Override
    public void run() {
        try {
            NodeResponse response = taskRunnerService.runTask(tasksStorage, request, subTask);
            lastResponse = response;
            if (response.getStatus() != ResponseStatus.STOPPED) {
                connection.send(response);
                logger.info("response was sent " + response);
            } else {
                logger.warn("response was stopped " + response);
            }
        } catch (RuntimeException e) {
            logger.error("exception thrown", e);
            try {
                connection.send(NodeResponse.systemError(request));
            } catch (IOException ioException) {
                logger.error(ioException);
            }

        } catch (IOException e) {
            logger.error("response wasn't sent " + lastResponse, e);
        }
    }

    @Override
    public void interrupt() {
        super.interrupt();
        subTask.stopProcess();
    }
}