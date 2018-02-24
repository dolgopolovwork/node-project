package ru.babobka.nodetester.master;

import ru.babobka.nodebusiness.NodeBusinessApplicationContainer;
import ru.babobka.nodemasterserver.server.MasterServerApplicationSubContainer;
import ru.babobka.nodemasterserver.server.MasterServerConfig;
import ru.babobka.nodemasterserver.validation.config.MasterServerConfigValidator;
import ru.babobka.nodetask.NodeTaskApplicationContainer;
import ru.babobka.nodeutils.NodeUtilsApplicationContainer;
import ru.babobka.nodeutils.container.ApplicationContainer;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.container.ContainerException;
import ru.babobka.nodeutils.logger.SimpleLogger;
import ru.babobka.nodeweb.NodeWebApplicationContainer;

/**
 * Created by 123 on 05.11.2017.
 */
public class MasterServerApplicationContainer implements ApplicationContainer {

    @Override
    public void contain(Container container) {
        try {
            container.put(new NodeUtilsApplicationContainer());
            MasterServerConfig config = createTestConfig();
            new MasterServerConfigValidator().validate(config);
            container.put(config);
            container.putIfNotExists(SimpleLogger.defaultLogger("master-server", config.getLoggerFolder()));
            container.put(new NodeTaskApplicationContainer());
            container.put(new NodeBusinessApplicationContainer());
            container.put(new NodeWebApplicationContainer());
            container.put(new MasterServerApplicationSubContainer());
            SimpleLogger logger = container.get(SimpleLogger.class);
            logger.debug("container was successfully created");
        } catch (Exception e) {
            throw new ContainerException(e);
        }
    }

    private MasterServerConfig createTestConfig() {
        MasterServerConfig config = new MasterServerConfig();
        config.setTasksFolderEnv("NODE_TASKS");
        config.setLoggerFolderEnv("NODE_LOGS");
        config.setAuthTimeOutMillis(2000);
        config.setClientListenerPort(9999);
        config.setDebugMode(true);
        config.setEnableCache(Container.getInstance().get("enableCache", false));
        config.setHeartBeatTimeOutMillis(5000);
        config.setRequestTimeOutMillis(15000);
        config.setSlaveListenerPort(9090);
        config.setWebListenerPort(8080);
        return config;
    }

}
