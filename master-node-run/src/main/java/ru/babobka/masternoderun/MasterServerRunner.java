package ru.babobka.masternoderun;

import ru.babobka.nodemasterserver.server.MasterServer;
import ru.babobka.nodemasterserver.server.MasterServerConfig;
import ru.babobka.nodemasterserver.validation.config.MasterServerConfigValidator;
import ru.babobka.nodeutils.container.Container;

import java.io.IOException;

/**
 * Created by 123 on 05.12.2017.
 */
public class MasterServerRunner {

    private final ConfigFactory configFactory = Container.getInstance().get(ConfigFactory.class);
    private final MasterServerConfigValidator configValidator = Container.getInstance().get(MasterServerConfigValidator.class);

    public void run(String configPath) throws IOException {
        Container container = Container.getInstance();
        MasterServerConfig config = configFactory.create(configPath);
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
