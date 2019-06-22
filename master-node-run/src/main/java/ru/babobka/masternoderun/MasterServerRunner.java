package ru.babobka.masternoderun;

import lombok.NonNull;
import ru.babobka.nodeconfigs.exception.EnvConfigCreationException;
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
    private final MasterServerConfigValidator configValidator =
            Container.getInstance().get(MasterServerConfigValidator.class);

    void run() throws IOException, EnvConfigCreationException {
        Container container = Container.getInstance();
        MasterServerConfig config = getConfig();
        configValidator.validate(config);
        LoggerInit.initPersistentConsoleLogger(config.getFolders().getLoggerFolder(), "master-server");
        container.put(config);
        container.put(new MasterServerApplicationContainer());
        new MasterServer().start();
    }

    private MasterServerConfig getConfig() throws IOException, EnvConfigCreationException {
        return configProvider.getConfig("master-server-config.yml", MasterServerConfig.class);
    }
}
