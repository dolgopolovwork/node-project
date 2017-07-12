package ru.babobka.nodeslaveserver.runnable;

import ru.babobka.nodeutils.logger.SimpleLogger;
import ru.babobka.nodeutils.util.StreamUtil;
import ru.babobka.nodeslaveserver.task.TaskRunnerService;
import ru.babobka.nodeslaveserver.task.TasksStorage;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodeserials.NodeResponse;
import ru.babobka.subtask.model.SubTask;

import java.io.IOException;
import java.net.Socket;

/**
 * Created by dolgopolov.a on 27.07.15.
 */
public class RequestHandlerRunnable implements Runnable {

    private final Socket socket;

    private final NodeRequest request;

    private final SubTask subTask;

    private final SimpleLogger logger = Container.getInstance().get(SimpleLogger.class);

    private final TaskRunnerService taskRunnerService = Container.getInstance().get(TaskRunnerService.class);

    private final TasksStorage tasksStorage;

    public RequestHandlerRunnable(Socket socket, TasksStorage tasksStorage, NodeRequest request, SubTask subTask) {
        this.socket = socket;
        this.request = request;
        this.subTask = subTask;
        this.tasksStorage = tasksStorage;
    }

    @Override
    public void run() {
        try {
            NodeResponse response = taskRunnerService.runTask(tasksStorage, request, subTask);
            if (!response.isStopped()) {
                StreamUtil.sendObject(response, socket);
                logger.info(response);
                logger.info("Response was sent");
            }
        } catch (RuntimeException e) {
            logger.error(e);
            try {
                StreamUtil.sendObject(NodeResponse.failed(request), socket);
            } catch (IOException e1) {
                logger.error(e1);
            }

        } catch (IOException e) {
            logger.error("Response wasn't sent", e);
        }
    }
}