package ru.babobka.masternoderun;

import ru.babobka.nodeconfigs.master.MasterServerConfig;
import ru.babobka.nodeconfigs.master.validation.MasterServerConfigValidator;
import ru.babobka.nodeconfigs.service.ConfigProvider;
import ru.babobka.nodemasterserver.server.MasterServer;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.log.LoggerInit;

import java.io.IOException;

/**
 * Created by 123 on 05.12.2017.
 */
public class MasterServerRunner {

    private final ConfigProvider configProvider = Container.getInstance().get(ConfigProvider.class);
    private final MasterServerConfigValidator configValidator = Container.getInstance().get(MasterServerConfigValidator.class);

    public void run(String configPath, String configPassword) throws IOException {
        Container container = Container.getInstance();
        MasterServerConfig config = configProvider.getConfig(configPath, MasterServerConfig.class, configPassword);
        LoggerInit.initPersistentConsoleLogger(config.getFolders().getLoggerFolder(),"master-server");
        configValidator.validate(config);
        container.put(config);
        container.put(createMasterServerContainer());
        createMasterServer().start();
    }

    MasterServer createMasterServer() {
        return new MasterServer();
    }

    MasterServerApplicationContainer createMasterServerContainer() {
        return new MasterServerApplicationContainer();
    }
}
