package ru.babobka.nodetester.slave;

import ru.babobka.nodeslaveserver.server.SlaveServerConfig;
import ru.babobka.nodeslaveserver.service.SlaveAuthService;
import ru.babobka.nodeslaveserver.task.TaskRunnerService;
import ru.babobka.nodeslaveserver.validator.config.SlaveServerConfigValidator;
import ru.babobka.nodetask.NodeTaskApplicationContainer;
import ru.babobka.nodetask.TaskPool;
import ru.babobka.nodeutils.NodeUtilsApplicationContainer;
import ru.babobka.nodeutils.container.ApplicationContainer;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.container.ContainerException;
import ru.babobka.nodeutils.logger.SimpleLogger;

/**
 * Created by 123 on 05.11.2017.
 */
public class SlaveServerApplicationContainer implements ApplicationContainer {
    @Override
    public void contain(Container container) {
        try {
            container.put("service-threads", Runtime.getRuntime().availableProcessors());
            container.put(new NodeUtilsApplicationContainer());
            SlaveServerConfig config = createTestConfig();
            new SlaveServerConfigValidator().validate(config);
            container.put(config);
            container.putIfNotExists(SimpleLogger.defaultLogger("slave-server", config.getLoggerFolder(), "slave"));
            container.put(new NodeTaskApplicationContainer());
            container.put(new TaskRunnerService());
            container.put("slaveServerTaskPool", new TaskPool(config.getTasksFolder()));
            container.put(new SlaveAuthService());
        } catch (Exception e) {
            throw new ContainerException(e);
        }
    }

    private SlaveServerConfig createTestConfig() {
        SlaveServerConfig config = new SlaveServerConfig();
        config.setTasksFolderEnv("NODE_IFT_TASKS");
        config.setLoggerFolderEnv("NODE_IFT_LOGS");
        config.setAuthTimeoutMillis(5000);
        config.setRequestTimeoutMillis(15000);
        config.setServerHost("localhost");
        config.setServerPort(9090);
        return config;
    }
}
