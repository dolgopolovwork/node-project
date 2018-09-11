package ru.babobka.slavenoderun;

import ru.babobka.nodeslaveserver.server.SlaveServer;
import ru.babobka.nodeslaveserver.server.SlaveServerConfig;
import ru.babobka.nodeslaveserver.validator.config.SlaveServerConfigValidator;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.util.JSONUtil;
import ru.babobka.nodeutils.util.StreamUtil;

import java.io.IOException;
import java.net.Socket;

/**
 * Created by 123 on 05.12.2017.
 */
public class SlaveServerFactory {

    private final StreamUtil streamUtil = Container.getInstance().get(StreamUtil.class);
    private final SlaveServerConfigValidator configValidator = Container.getInstance().get(SlaveServerConfigValidator.class);

    public SlaveServer create(String configPath, String login, String hashedPassword) throws IOException {
        Container container = Container.getInstance();
        SlaveServerConfig config = JSONUtil.readJsonFile(streamUtil, configPath, SlaveServerConfig.class);
        configValidator.validate(config);
        container.put(config);
        container.put(createSlaveServerContainer());
        return createSlaveServer(config.getServerHost(), config.getServerPort(), login, hashedPassword);
    }

    private SlaveServer createSlaveServer(String host, int port, String login, String password) throws IOException {
        return new SlaveServer(new Socket(host, port), login, password);
    }

    private SlaveServerApplicationContainer createSlaveServerContainer() {
        return new SlaveServerApplicationContainer();
    }
}
