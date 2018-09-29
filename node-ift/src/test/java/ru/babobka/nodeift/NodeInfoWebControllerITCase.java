package ru.babobka.nodeift;

import org.apache.http.client.fluent.Request;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.babobka.nodeconfigs.master.MasterServerConfig;
import ru.babobka.nodemasterserver.server.MasterServer;
import ru.babobka.nodesecurity.rsa.RSAPublicKey;
import ru.babobka.nodetester.master.MasterServerRunner;
import ru.babobka.nodetester.slave.SlaveServerRunner;
import ru.babobka.nodetester.slave.cluster.SlaveServerCluster;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.enums.Env;
import ru.babobka.nodeutils.logger.SimpleLoggerFactory;
import ru.babobka.nodeutils.util.TextUtil;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

/**
 * Created by 123 on 22.07.2018.
 */
public class NodeInfoWebControllerITCase {
    private static MasterServer masterServer;
    private static MasterServerConfig config;

    @BeforeClass
    public static void setUp() throws IOException {
        Container.getInstance().put(SimpleLoggerFactory.debugLogger(NodeInfoWebControllerITCase.class.getSimpleName(), TextUtil.getEnv(Env.NODE_LOGS)));
        MasterServerRunner.init();
        MasterServerConfig masterServerConfig = Container.getInstance().get(MasterServerConfig.class);
        RSAPublicKey publicKey = masterServerConfig.getSecurity().getRsaConfig().getPublicKey();
        SlaveServerRunner.init(publicKey);
        masterServer = MasterServerRunner.runMasterServer();
        config = Container.getInstance().get(MasterServerConfig.class);
    }

    @AfterClass
    public static void tearDown() throws InterruptedException {
        masterServer.interrupt();
        masterServer.join();
        Container.getInstance().clear();
    }

    @Test
    public void testGetClusterSize() throws IOException {
        int slaves = 5;
        try (SlaveServerCluster slaveServerCluster = new SlaveServerCluster(TestCredentials.USER_NAME, TestCredentials.PASSWORD, slaves)) {
            slaveServerCluster.start();
            String content = Request.Get("http://127.0.0.1:" + config.getPorts().getWebListenerPort() + "/serverInfo?infoType=clusterSize")
                    .execute().returnContent().toString();
            assertEquals(slaves, Integer.valueOf(content).intValue());
        }
    }
}
