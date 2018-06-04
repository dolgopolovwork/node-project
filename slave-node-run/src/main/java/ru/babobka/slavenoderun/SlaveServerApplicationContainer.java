package ru.babobka.slavenoderun;

import ru.babobka.nodesecurity.SecurityApplicationContainer;
import ru.babobka.nodeslaveserver.key.SlaveServerKey;
import ru.babobka.nodeslaveserver.server.SlaveServerConfig;
import ru.babobka.nodeslaveserver.service.SlaveAuthService;
import ru.babobka.nodeslaveserver.task.TaskRunnerService;
import ru.babobka.nodetask.NodeTaskApplicationContainer;
import ru.babobka.nodetask.TaskPool;
import ru.babobka.nodeutils.NodeUtilsApplicationContainer;
import ru.babobka.nodeutils.container.ApplicationContainer;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.container.ContainerException;
import ru.babobka.nodeutils.container.Properties;
import ru.babobka.nodeutils.key.UtilKey;
import ru.babobka.nodeutils.logger.SimpleLoggerFactory;
import ru.babobka.nodeutils.network.NodeConnectionFactory;
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
            Properties.put(UtilKey.SERVICE_THREADS_NUM, Runtime.getRuntime().availableProcessors());
            container.put(SimpleLoggerFactory.defaultLogger("slave-server", config.getLoggerFolder()));
            container.put(new NodeUtilsApplicationContainer());
            container.put(new SecurityApplicationContainer());
            container.put(new NodeConnectionFactory());
            container.put(config);
            container.put(new NodeTaskApplicationContainer());
            container.put(new TaskRunnerService());
            container.put(UtilKey.SERVICE_THREAD_POOL, ThreadPoolService.createDaemonPool("service thread pool"));
            container.put(SlaveServerKey.SLAVE_SERVER_TASK_POOL, new TaskPool(config.getTasksFolder()));
            container.put(new SlaveAuthService());
        } catch (IOException | RuntimeException e) {
            throw new ContainerException(e);
        }
    }
}
