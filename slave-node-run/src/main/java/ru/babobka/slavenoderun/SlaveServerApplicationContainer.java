package ru.babobka.slavenoderun;

import ru.babobka.nodeslaveserver.server.SlaveServerConfig;
import ru.babobka.nodeslaveserver.service.SlaveAuthService;
import ru.babobka.nodeslaveserver.task.TaskRunnerService;
import ru.babobka.nodetask.NodeTaskApplicationContainer;
import ru.babobka.nodetask.TaskPool;
import ru.babobka.nodeutils.NodeUtilsApplicationContainer;
import ru.babobka.nodeutils.container.ApplicationContainer;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.container.ContainerException;
import ru.babobka.nodeutils.logger.SimpleLogger;
import ru.babobka.nodeutils.thread.ThreadPoolService;

import java.io.IOException;

/**
 * Created by 123 on 05.11.2017.
 */
public class SlaveServerApplicationContainer implements ApplicationContainer {

    @Override
    public void contain(Container container) {
        try {
            SlaveServerConfig config = container.get(SlaveServerConfig.class);
            container.put("service-threads", Runtime.getRuntime().availableProcessors());
            container.putIfNotExists(SimpleLogger.defaultLogger("slave-server", config.getLoggerFolder()));
            container.put(new NodeUtilsApplicationContainer());
            container.put(config);
            container.put(new NodeTaskApplicationContainer());
            container.put(new TaskRunnerService());
            Container.getInstance().put("service-thread-pool", ThreadPoolService.createDaemonPool());
            container.put("slaveServerTaskPool", new TaskPool(config.getTasksFolder()));
            container.put(new SlaveAuthService());
        } catch (IOException | RuntimeException e) {
            throw new ContainerException(e);
        }
    }
}
