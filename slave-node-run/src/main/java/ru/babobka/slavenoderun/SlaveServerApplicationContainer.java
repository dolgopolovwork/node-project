package ru.babobka.slavenoderun;

import ru.babobka.nodeconfigs.slave.SlaveServerConfig;
import ru.babobka.nodesecurity.SecurityApplicationContainer;
import ru.babobka.nodeslaveserver.key.SlaveServerKey;
import ru.babobka.nodeslaveserver.server.pipeline.SlavePipelineFactory;
import ru.babobka.nodeslaveserver.service.SlaveAuthService;
import ru.babobka.nodeslaveserver.task.TaskRunnerService;
import ru.babobka.nodetask.NodeTaskApplicationContainer;
import ru.babobka.nodetask.TaskPool;
import ru.babobka.nodeutils.NodeUtilsApplicationContainer;
import ru.babobka.nodeutils.container.AbstractApplicationContainer;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.container.Properties;
import ru.babobka.nodeutils.key.UtilKey;
import ru.babobka.nodeutils.network.NodeConnectionFactory;
import ru.babobka.nodeutils.thread.ThreadPoolService;

/**
 * Created by 123 on 05.11.2017.
 */
public class SlaveServerApplicationContainer extends AbstractApplicationContainer {

    @Override
    protected void containImpl(Container container) {
        SlaveServerConfig config = container.get(SlaveServerConfig.class);
        Properties.put(UtilKey.SERVICE_THREADS_NUM, Runtime.getRuntime().availableProcessors());
        container.put(new NodeUtilsApplicationContainer());
        container.put(new SecurityApplicationContainer());
        container.put(new NodeConnectionFactory());
        container.put(config);
        container.put(new NodeTaskApplicationContainer());
        container.put(new SlavePipelineFactory());
        container.put(new TaskRunnerService());
        container.put(UtilKey.SERVICE_THREAD_POOL, ThreadPoolService.createDaemonPool("service"));
        container.put(SlaveServerKey.SLAVE_SERVER_TASK_POOL, new TaskPool(config.getTasksFolder()));
        container.put(new SlaveAuthService());
    }
}
