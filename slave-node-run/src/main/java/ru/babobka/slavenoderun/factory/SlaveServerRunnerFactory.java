package ru.babobka.slavenoderun.factory;

import lombok.NonNull;
import ru.babobka.nodeconfigs.slave.SlaveServerConfig;
import ru.babobka.nodeconfigs.slave.validation.SlaveServerConfigValidator;
import ru.babobka.nodesecurity.keypair.KeyDecoder;
import ru.babobka.nodeslaveserver.server.SlaveServer;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.log.LoggerInit;
import ru.babobka.nodeutils.util.JSONUtil;
import ru.babobka.nodeutils.util.StreamUtil;
import ru.babobka.slavenoderun.SlaveServerApplicationContainer;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;

/**
 * Created by 123 on 05.12.2017.
 */
public abstract class SlaveServerRunnerFactory {

    private final StreamUtil streamUtil = Container.getInstance().get(StreamUtil.class);
    private volatile boolean logInitialized = false;
    private final SlaveServerConfigValidator configValidator =
            Container.getInstance().get(SlaveServerConfigValidator.class);

    protected abstract SlaveServer create(String masterServerHost,
                                          int masterServerPort,
                                          String login,
                                          PrivateKey privateKey) throws IOException;

    public SlaveServer build(String configPath) throws GeneralSecurityException, IOException {
        Container container = Container.getInstance();
        SlaveServerConfig config = getConfig(configPath);
        configValidator.validate(config);
        if (!logInitialized) {
            LoggerInit.initPersistentConsoleLogger(config.getLoggerFolder(), "slave-server");
            logInitialized = true;
        }
        container.put(config);
        container.put(createSlaveServerContainer());
        return create(
                config.getServerHost(),
                config.getServerPort(),
                config.getSlaveLogin(),
                KeyDecoder.decodePrivateKey(config.getKeyPair().getPrivKey()));
    }

    private SlaveServerConfig getConfig(@NonNull String configPath) throws IOException {
        return JSONUtil.readJsonFile(streamUtil, configPath, SlaveServerConfig.class);
    }

    private SlaveServerApplicationContainer createSlaveServerContainer() {
        return new SlaveServerApplicationContainer();
    }
}
