package ru.babobka.nodeift;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.babobka.dlp.ServerConfig;
import ru.babobka.dlp.mapper.NodeRequestsListMapper;
import ru.babobka.dlp.mapper.PollardDistResultMapper;
import ru.babobka.dlp.model.dist.DlpTaskDist;
import ru.babobka.dlp.service.DlpDistClient;
import ru.babobka.nodemasterserver.server.MasterServer;
import ru.babobka.nodemasterserver.server.config.MasterServerConfig;
import ru.babobka.nodesecurity.rsa.RSAPublicKey;
import ru.babobka.nodetester.master.MasterServerRunner;
import ru.babobka.nodetester.slave.SlaveServerRunner;
import ru.babobka.nodetester.slave.cluster.SlaveServerCluster;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.enums.Env;
import ru.babobka.nodeutils.logger.SimpleLoggerFactory;
import ru.babobka.nodeutils.util.TextUtil;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertEquals;

/**
 * Created by 123 on 17.07.2018.
 */
public class DistDlpClientITCase {

    protected static MasterServer masterServer;
    private static ServerConfig serverConfig;

    @BeforeClass
    public static void setUp() {
        Container.getInstance().put(container -> {
            container.put(SimpleLoggerFactory.debugLogger(ClientITCase.class.getSimpleName(), TextUtil.getEnv(Env.NODE_LOGS)));
            container.put(new NodeRequestsListMapper());
            container.put(new PollardDistResultMapper());
        });
        MasterServerRunner.init();
        MasterServerConfig masterServerConfig = Container.getInstance().get(MasterServerConfig.class);
        RSAPublicKey publicKey = masterServerConfig.getSecurity().getRsaConfig().getPublicKey();
        SlaveServerRunner.init(publicKey);
        masterServer = MasterServerRunner.runMasterServer();
        MasterServerConfig masterConfig = Container.getInstance().get(MasterServerConfig.class);
        serverConfig = new ServerConfig(
                "127.0.0.1",
                masterConfig.getPorts().getClientListenerPort(),
                masterConfig.getPorts().getWebListenerPort());
    }

    @AfterClass
    public static void tearDown() throws InterruptedException {
        masterServer.interrupt();
        masterServer.join();
        Container.getInstance().clear();
    }

    @Test
    public void testDistDlpFiveSlaves() throws IOException, InterruptedException, ExecutionException {
        int slaves = 5;
        int loops = 10;
        int modBits = 25;
        try (SlaveServerCluster slaveServerCluster = new SlaveServerCluster(TestCredentials.USER_NAME, TestCredentials.PASSWORD, slaves)) {
            slaveServerCluster.start();
            DlpTaskDist dlpTaskDist = PollardDlpDistITCase.createDlpTaskDist(modBits, loops);
            DlpDistClient dlpDistClient = new DlpDistClient(
                    serverConfig,
                    dlpTaskDist);
            dlpDistClient.start();
            assertEquals(dlpTaskDist.getGen().pow(dlpDistClient.getResult()), dlpTaskDist.getY());
        }
    }

    @Test
    public void testDistDlpFiveSlavesMassive() throws IOException, InterruptedException, ExecutionException {
        int slaves = 5;
        int loops = 15;
        int modBits = 25;
        try (SlaveServerCluster slaveServerCluster = new SlaveServerCluster(TestCredentials.USER_NAME, TestCredentials.PASSWORD, slaves)) {
            slaveServerCluster.start();
            DlpTaskDist dlpTaskDist = PollardDlpDistITCase.createDlpTaskDist(modBits, loops);
            for (int i = 0; i < 15; i++) {
                DlpDistClient dlpDistClient = new DlpDistClient(
                        serverConfig,
                        dlpTaskDist);
                dlpDistClient.start();
                assertEquals(dlpTaskDist.getGen().pow(dlpDistClient.getResult()), dlpTaskDist.getY());
            }
        }
    }

    @Test
    public void testDistDlpFiveSlavesMassiveMaxLoops() throws IOException, InterruptedException, ExecutionException {
        int slaves = 5;
        int loops = Integer.MAX_VALUE;
        int modBits = 30;
        try (SlaveServerCluster slaveServerCluster = new SlaveServerCluster(TestCredentials.USER_NAME, TestCredentials.PASSWORD, slaves)) {
            slaveServerCluster.start();
            DlpTaskDist dlpTaskDist = PollardDlpDistITCase.createDlpTaskDist(modBits, loops);
            for (int i = 0; i < 15; i++) {
                DlpDistClient dlpDistClient = new DlpDistClient(
                        serverConfig,
                        dlpTaskDist);
                dlpDistClient.start();
                assertEquals(dlpTaskDist.getGen().pow(dlpDistClient.getResult()), dlpTaskDist.getY());
            }
        }
    }

    @Test
    public void testDistDlpFiveSlavesMassiveAutomaticLoops() throws IOException, InterruptedException, ExecutionException {
        int slaves = 5;
        int modBits = 30;
        try (SlaveServerCluster slaveServerCluster = new SlaveServerCluster(TestCredentials.USER_NAME, TestCredentials.PASSWORD, slaves)) {
            slaveServerCluster.start();
            DlpTaskDist dlpTaskDist = PollardDlpDistITCase.createDlpTaskDist(modBits);
            for (int i = 0; i < 15; i++) {
                DlpDistClient dlpDistClient = new DlpDistClient(
                        serverConfig,
                        dlpTaskDist);
                dlpDistClient.start();
                assertEquals(dlpTaskDist.getGen().pow(dlpDistClient.getResult()), dlpTaskDist.getY());
            }
        }
    }
}
