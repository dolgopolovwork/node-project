package ru.babobka.nodemasterserver.slave.pipeline.step;

import ru.babobka.nodemasterserver.key.MasterServerKey;
import ru.babobka.nodemasterserver.slave.pipeline.PipeContext;
import ru.babobka.nodetask.TaskPool;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.func.pipeline.Step;
import ru.babobka.nodeutils.logger.NodeLogger;
import ru.babobka.nodeutils.network.NodeConnection;

import java.io.IOException;
import java.util.Set;

/**
 * Created by 123 on 07.06.2018.
 */
public class CommonTasksStep implements Step<PipeContext> {
    private final NodeLogger nodeLogger = Container.getInstance().get(NodeLogger.class);
    private final TaskPool taskPool = Container.getInstance().get(MasterServerKey.MASTER_SERVER_TASK_POOL);

    @Override
    public boolean execute(PipeContext pipeContext) {
        NodeConnection connection = pipeContext.getConnection();
        try {
            Set<String> availableTasks = connection.receive();
            pipeContext.setAvailableTasks(availableTasks);
            boolean containsAnyOfTask = taskPool.containsAnyOfTask(availableTasks);
            if (!containsAnyOfTask) {
                nodeLogger.error("new slave doesn't have any common tasks with master");
                connection.send(false);
                return false;
            }
            connection.send(true);
            return true;
        } catch (IOException e) {
            nodeLogger.error("cannot get common tasks with slave due to network error");
            return false;
        }
    }
}
