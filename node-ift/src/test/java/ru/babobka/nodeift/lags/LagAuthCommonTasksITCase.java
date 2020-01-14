package ru.babobka.nodeift.lags;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import ru.babobka.nodeconfigs.master.MasterServerConfig;
import ru.babobka.nodeift.AuthCommonTasksITCase;
import ru.babobka.nodesecurity.keypair.KeyDecoder;
import ru.babobka.nodetester.master.MasterServerRunner;
import ru.babobka.nodetester.network.LaggyNodeConnectionFactory;
import ru.babobka.nodetester.slave.SlaveServerRunner;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.log.LoggerInit;
import ru.babobka.nodeutils.util.TextUtil;

import java.io.IOException;
import java.security.PublicKey;

/**
 * Created by 123 on 08.04.2018.
 */
public class LagAuthCommonTasksITCase extends AuthCommonTasksITCase {

    @BeforeClass
    public static void setUp() throws IOException {
        LoggerInit.initPersistentConsoleDebugLogger(TextUtil.getLogFolder(), LagAuthCommonTasksITCase.class.getSimpleName());
        Container.getInstance().put(new LaggyNodeConnectionFactory());
        MasterServerRunner.init();
        MasterServerConfig masterServerConfig = Container.getInstance().get(MasterServerConfig.class);
        PublicKey serverPublicKey = KeyDecoder.decodePublicKeyUnsafe(masterServerConfig.getKeyPair().getPubKey());
        SlaveServerRunner.init(serverPublicKey);
        masterServer = MasterServerRunner.runMasterServer();
    }

    @AfterClass
    public static void tearDown() throws InterruptedException {
        masterServer.interrupt();
        masterServer.join();
        Container.getInstance().clear();
    }
}
