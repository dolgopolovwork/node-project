package ru.babobka.slavenoderun;

import ru.babobka.nodeslaveserver.server.SlaveServer;
import ru.babobka.nodeslaveserver.server.SlaveServerConfig;
import ru.babobka.nodeslaveserver.validator.config.SlaveServerConfigValidator;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.util.StreamUtil;
import ru.babobka.nodeutils.util.TextUtil;

import java.io.IOException;

/**
 * Created by 123 on 05.12.2017.
 */
public class SlaveServerRunner {

    private final StreamUtil streamUtil = Container.getInstance().get(StreamUtil.class);
    private final SlaveServerConfigValidator configValidator = Container.getInstance().get(SlaveServerConfigValidator.class);

    public void run(String configPath, String login, String hashedPassword) throws IOException {
        Container container = Container.getInstance();
        SlaveServerConfig config = TextUtil.readJsonFile(streamUtil, configPath, SlaveServerConfig.class);
        configValidator.validate(config);
        container.put(config);
        container.put(createSlaveServerContainer());
        createSlaveServer(config.getServerHost(), config.getServerPort(), login, hashedPassword).start();
    }

    SlaveServer createSlaveServer(String host, int port, String login, String password) throws IOException {
        return new SlaveServer(host, port, login, password);
    }

    SlaveServerApplicationContainer createSlaveServerContainer() {
        return new SlaveServerApplicationContainer();
    }

}
