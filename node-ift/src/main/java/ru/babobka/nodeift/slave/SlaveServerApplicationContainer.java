package ru.babobka.nodeift.slave;

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

/**
 * Created by 123 on 05.11.2017.
 */
public class SlaveServerApplicationContainer implements ApplicationContainer {
    @Override
    public void contain(Container container) {
        try {
            container.put(new NodeUtilsApplicationContainer());
            SlaveServerConfig config = createTestConfig();
            new SlaveServerConfigValidator().validate(config);
            container.put(config);
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
        config.setAuthTimeoutMillis(2000);
        config.setRequestTimeoutMillis(5000);
        config.setServerHost("localhost");
        config.setServerPort(9090);
        return config;
    }
}
