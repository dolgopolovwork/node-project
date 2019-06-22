package ru.babobka.slavenoderun.factory;

import lombok.NonNull;
import ru.babobka.nodeconfigs.exception.EnvConfigCreationException;
import ru.babobka.nodeconfigs.service.ConfigProvider;
import ru.babobka.nodeconfigs.slave.SlaveServerConfig;
import ru.babobka.nodeconfigs.slave.validation.SlaveServerConfigValidator;
import ru.babobka.nodesecurity.keypair.KeyDecoder;
import ru.babobka.nodeslaveserver.server.SlaveServer;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.log.LoggerInit;
import ru.babobka.nodeutils.util.JSONUtil;
import ru.babobka.nodeutils.util.StreamUtil;
import ru.babobka.nodeutils.util.YamlUtil;
import ru.babobka.slavenoderun.SlaveServerApplicationContainer;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by 123 on 05.12.2017.
 */
public abstract class SlaveServerRunnerFactory {

    private final ConfigProvider configProvider = Container.getInstance().get(ConfigProvider.class);
    private final AtomicBoolean logInitialized = new AtomicBoolean();
    private final SlaveServerConfigValidator configValidator =
            Container.getInstance().get(SlaveServerConfigValidator.class);

    protected abstract SlaveServer create(String masterServerHost,
                                          int masterServerPort,
                                          String login,
                                          PrivateKey privateKey) throws IOException;

    public SlaveServer build() throws GeneralSecurityException, IOException, EnvConfigCreationException {
        Container container = Container.getInstance();
        SlaveServerConfig config = configProvider.getConfig("slave-server-config.yml", SlaveServerConfig.class);
        configValidator.validate(config);
        if (logInitialized.compareAndSet(false, true)) {
            LoggerInit.initPersistentConsoleLogger(config.getLoggerFolder(), "slave-server");
        }
        container.put(config);
        container.put(createSlaveServerContainer());
        return create(
                config.getMasterServerHost(),
                config.getMasterServerPort(),
                config.getSlaveLogin(),
                KeyDecoder.decodePrivateKey(config.getKeyPair().getPrivKey()));
    }

    private SlaveServerApplicationContainer createSlaveServerContainer() {
        return new SlaveServerApplicationContainer();
    }
}
