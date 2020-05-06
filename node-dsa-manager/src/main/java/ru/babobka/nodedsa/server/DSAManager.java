package ru.babobka.nodedsa.server;

import io.javalin.Javalin;
import ru.babobka.nodeconfigs.dsa.DSAServerConfig;
import ru.babobka.nodeconfigs.dsa.validation.DSAServerConfigValidator;
import ru.babobka.nodeconfigs.exception.EnvConfigCreationException;
import ru.babobka.nodeconfigs.service.ConfigProvider;
import ru.babobka.nodesecurity.keypair.KeyDecoder;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.log.LoggerInit;

import java.io.IOException;

public class DSAManager {
    static {
        Container.getInstance().put(container -> {
            container.put(new NodeDSAApplicationContainer());
        });
    }

    private static final DSAServerConfigValidator configValidator =
            Container.getInstance().get(DSAServerConfigValidator.class);
    private static final ConfigProvider configProvider =
            Container.getInstance().get(ConfigProvider.class);

    public static void main(String[] args) throws IOException, EnvConfigCreationException {
        DSAServerConfig config = configProvider.getConfig("dsa-server-config.yml", DSAServerConfig.class);
        configValidator.validate(config);
        LoggerInit.initPersistentConsoleLogger(config.getLoggerFolder(), "dsa-server");
        Container.getInstance().put(container -> {
            container.put(KeyDecoder.decodePublicKey(config.getKeyPair().getPubKey()));
            container.put(KeyDecoder.decodePrivateKey(config.getKeyPair().getPrivKey()));
        });
        Javalin webServer = NodeDSAApplicationContainer.createWebServer();
        webServer.start(config.getPort());
    }
}
