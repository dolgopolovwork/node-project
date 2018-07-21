package ru.babobka.nodeift.lags;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import ru.babobka.nodemasterserver.server.config.MasterServerConfig;
import ru.babobka.nodesecurity.rsa.RSAPublicKey;
import ru.babobka.nodetester.master.MasterServerRunner;
import ru.babobka.nodetester.network.LaggyNodeConnectionFactory;
import ru.babobka.nodetester.slave.SlaveServerRunner;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.enums.Env;
import ru.babobka.nodeutils.logger.SimpleLoggerFactory;
import ru.babobka.nodeutils.util.TextUtil;

/**
 * Created by 123 on 12.12.2017.
 */
public class LagClientITCase extends ru.babobka.nodeift.ClientITCase {

    @BeforeClass
    public static void setUp() {
        Container.getInstance().put(container -> {
            container.put(SimpleLoggerFactory.debugLogger(LagClientITCase.class.getSimpleName(), TextUtil.getEnv(Env.NODE_LOGS)));
            container.put(new LaggyNodeConnectionFactory());
        });
        MasterServerRunner.init();
        MasterServerConfig masterServerConfig = Container.getInstance().get(MasterServerConfig.class);
        RSAPublicKey publicKey = masterServerConfig.getSecurity().getRsaConfig().getPublicKey();
        SlaveServerRunner.init(publicKey);
        masterServer = MasterServerRunner.runMasterServer();
    }

    @AfterClass
    public static void tearDown() throws InterruptedException {
        masterServer.interrupt();
        masterServer.join();
        Container.getInstance().clear();
    }

    @Override
    protected int getTests() {
        return 10;
    }


}
