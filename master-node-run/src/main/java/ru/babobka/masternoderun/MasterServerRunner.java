package ru.babobka.masternoderun;

import lombok.NonNull;
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

    void run(String configPath) throws IOException {
        Container container = Container.getInstance();
        MasterServerConfig config = getConfig(configPath);
        configValidator.validate(config);
        LoggerInit.initPersistentConsoleLogger(config.getFolders().getLoggerFolder(), "master-server");
        container.put(config);
        container.put(new MasterServerApplicationContainer());
        new MasterServer().start();
    }


    private MasterServerConfig getConfig(@NonNull String configPath) throws IOException {
        return configProvider.getConfig(configPath, MasterServerConfig.class);
    }
}
