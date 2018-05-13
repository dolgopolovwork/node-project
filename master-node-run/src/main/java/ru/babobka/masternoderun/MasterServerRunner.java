package ru.babobka.masternoderun;

import ru.babobka.nodemasterserver.server.MasterServer;
import ru.babobka.nodemasterserver.server.config.MasterServerConfig;
import ru.babobka.nodemasterserver.validation.config.MasterServerConfigValidator;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.util.JSONUtil;
import ru.babobka.nodeutils.util.StreamUtil;

import java.io.IOException;

/**
 * Created by 123 on 05.12.2017.
 */
public class MasterServerRunner {

    private final StreamUtil streamUtil = Container.getInstance().get(StreamUtil.class);
    private final MasterServerConfigValidator configValidator = Container.getInstance().get(MasterServerConfigValidator.class);

    public void run(String configPath) throws IOException {
        Container container = Container.getInstance();
        MasterServerConfig config = JSONUtil.readJsonFile(streamUtil, configPath, MasterServerConfig.class);
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
