package ru.babobka.nodeslaveserver.server.pipeline.step;

import ru.babobka.nodeslaveserver.key.SlaveServerKey;
import ru.babobka.nodeslaveserver.server.pipeline.PipeContext;
import ru.babobka.nodetask.TaskPool;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.func.pipeline.Step;
import ru.babobka.nodeutils.logger.NodeLogger;
import ru.babobka.nodeutils.network.NodeConnection;

import java.io.IOException;

/**
 * Created by 123 on 07.06.2018.
 */
public class CommonTasksStep implements Step<PipeContext> {
    private final NodeLogger nodeLogger = Container.getInstance().get(NodeLogger.class);
    private final TaskPool taskPool = Container.getInstance().get(SlaveServerKey.SLAVE_SERVER_TASK_POOL);

    @Override
    public boolean execute(PipeContext pipeContext) {
        try {
            NodeConnection connection = pipeContext.getConnection();
            connection.send(taskPool.getTaskNames());
            boolean haveCommonTasks = connection.receive();
            if (!haveCommonTasks) {
                nodeLogger.error("no common tasks with master server");
                return false;
            }
            return true;
        } catch (IOException e) {
            nodeLogger.error("can not get common tasks with server due to network error", e);
            return false;
        }
    }
}
