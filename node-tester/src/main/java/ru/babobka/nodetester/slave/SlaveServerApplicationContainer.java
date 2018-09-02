package ru.babobka.nodetester.slave;

import ru.babobka.nodesecurity.SecurityApplicationContainer;
import ru.babobka.nodesecurity.rsa.RSAPublicKey;
import ru.babobka.nodeslaveserver.key.SlaveServerKey;
import ru.babobka.nodeslaveserver.server.SlaveServerConfig;
import ru.babobka.nodeslaveserver.server.pipeline.SlavePipelineFactory;
import ru.babobka.nodeslaveserver.service.SlaveAuthService;
import ru.babobka.nodeslaveserver.task.TaskRunnerService;
import ru.babobka.nodeslaveserver.validator.config.SlaveServerConfigValidator;
import ru.babobka.nodetask.NodeTaskApplicationContainer;
import ru.babobka.nodetask.TaskPool;
import ru.babobka.nodeutils.NodeUtilsApplicationContainer;
import ru.babobka.nodeutils.container.AbstractApplicationContainer;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.container.ContainerException;
import ru.babobka.nodeutils.container.Properties;
import ru.babobka.nodeutils.enums.Env;
import ru.babobka.nodeutils.key.UtilKey;
import ru.babobka.nodeutils.logger.SimpleLoggerFactory;
import ru.babobka.nodeutils.network.NodeConnectionFactory;
import ru.babobka.nodeutils.thread.ThreadPoolService;

/**
 * Created by 123 on 05.11.2017.
 */
public class SlaveServerApplicationContainer extends AbstractApplicationContainer {

    private final RSAPublicKey rsaPublicKey;

    public SlaveServerApplicationContainer(RSAPublicKey rsaPublicKey) {
        if (rsaPublicKey == null) {
            throw new IllegalArgumentException("rsaPublicKey is null");
        }
        this.rsaPublicKey = rsaPublicKey;
    }

    @Override
    protected void containImpl(Container container) {
        try {
            Properties.put(UtilKey.SERVICE_THREADS_NUM, Runtime.getRuntime().availableProcessors());
            container.put(new NodeUtilsApplicationContainer());
            container.put(new SecurityApplicationContainer());
            container.putIfNotExists(new NodeConnectionFactory());
            SlaveServerConfig config = createTestConfig();
            new SlaveServerConfigValidator().validate(config);
            container.put(config);
            container.putIfNotExists(SimpleLoggerFactory.defaultLogger("slave-server", config.getLoggerFolder()));
            Container.getInstance().put(UtilKey.SERVICE_THREAD_POOL, ThreadPoolService.createDaemonPool("service thread pool"));
            container.put(new NodeTaskApplicationContainer());
            container.put(new SlavePipelineFactory());
            container.put(new TaskRunnerService());
            container.put(SlaveServerKey.SLAVE_SERVER_TASK_POOL, new TaskPool(config.getTasksFolder()));
            container.put(new SlaveAuthService());
        } catch (Exception e) {
            throw new ContainerException(e);
        }
    }

    private SlaveServerConfig createTestConfig() {
        SlaveServerConfig config = new SlaveServerConfig();
        config.setTasksFolderEnv(Env.NODE_TASKS.name());
        config.setLoggerFolderEnv(Env.NODE_LOGS.name());
        config.setAuthTimeoutMillis(15000);
        config.setRequestTimeoutMillis(30_000);
        config.setServerHost("localhost");
        config.setServerPort(9090);
        config.setServerPublicKey(rsaPublicKey);
        return config;
    }
}
