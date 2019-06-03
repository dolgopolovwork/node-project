package ru.babobka.nodeift;

import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import ru.babobka.nodeconfigs.master.MasterServerConfig;
import ru.babobka.nodemasterserver.server.MasterServer;
import ru.babobka.nodesecurity.keypair.KeyDecoder;
import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodeserials.data.Data;
import ru.babobka.nodetask.service.TaskService;
import ru.babobka.nodetester.master.MasterServerRunner;
import ru.babobka.nodetester.slave.SlaveServerRunner;
import ru.babobka.nodetester.slave.cluster.SlaveServerCluster;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.enums.Env;
import ru.babobka.nodeutils.log.LoggerInit;
import ru.babobka.nodeutils.util.TextUtil;

import java.io.IOException;
import java.math.BigInteger;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Random;
import java.util.UUID;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Created by 123 on 23.11.2017.
 */
public class EllipticCurveITCase {

    private static final Logger logger = Logger.getLogger(EllipticCurveITCase.class);
    private static final String TASK_NAME = "ru.babobka.factor.task.EllipticCurveFactorTask";
    protected static MasterServer masterServer;
    private static TaskService taskService;

    @BeforeClass
    public static void setUp() throws InvalidKeySpecException {
        LoggerInit.initPersistentConsoleDebugLogger(TextUtil.getEnv(Env.NODE_LOGS), EllipticCurveITCase.class.getSimpleName());
        MasterServerRunner.init();
        MasterServerConfig masterServerConfig = Container.getInstance().get(MasterServerConfig.class);
        PublicKey serverPubKey = KeyDecoder.decodePublicKey(masterServerConfig.getKeyPair().getPubKey());
        SlaveServerRunner.init(serverPubKey);
        masterServer = MasterServerRunner.runMasterServer();
        taskService = Container.getInstance().get(TaskService.class);
    }

    @AfterClass
    public static void tearDown() throws InterruptedException {
        masterServer.interrupt();
        masterServer.join();
        Container.getInstance().clear();
    }

    private static void createFactorTest(int primeBitLength, TaskService taskService) {
        BigInteger p = BigInteger.probablePrime(primeBitLength, new Random());
        BigInteger q = BigInteger.probablePrime(primeBitLength, new Random());
        NodeRequest request = createFactorRequest(p.multiply(q));
        taskService.executeTask(request, result -> {
            BigInteger factor = result.getData().get("factor");
            assertTrue(factor.equals(p) || factor.equals(q));
        }, error -> {
            logger.error(error);
            fail();
        });
    }

    private static NodeRequest createFactorRequest(BigInteger number) {
        Data data = new Data();
        data.put("number", number);
        return NodeRequest.regular(UUID.randomUUID(), TASK_NAME, data);
    }

    public static NodeRequest createFactorTest(BigInteger p, BigInteger q) {
        return createFactorRequest(p.multiply(q));
    }

    @Test
    public void testFactorMediumNumberOneSlave() throws IOException {
        try (SlaveServerCluster slaveServerCluster = new SlaveServerCluster(TestCredentials.USER_NAME, TestCredentials.PRIV_KEY)) {
            slaveServerCluster.start();
            int bits = 32;
            createFactorTest(bits, taskService);
        }
    }

    @Test
    public void testFactorMediumNumberTwoSlaves() throws IOException {
        try (SlaveServerCluster slaveServerCluster = new SlaveServerCluster(TestCredentials.USER_NAME, TestCredentials.PRIV_KEY, 2)) {
            slaveServerCluster.start();
            int bits = 32;
            createFactorTest(bits, taskService);
        }
    }

    @Test
    public void testFactorMediumNumberTwoSlavesMassive() throws IOException {
        try (SlaveServerCluster slaveServerCluster = new SlaveServerCluster(TestCredentials.USER_NAME, TestCredentials.PRIV_KEY, 2)) {
            slaveServerCluster.start();
            int bits = 32;
            for (int i = 0; i < 50; i++) {
                createFactorTest(bits, taskService);
            }
        }
    }

    @Test
    public void testFactorBigNumberOneSlave() throws IOException {
        try (SlaveServerCluster slaveServerCluster = new SlaveServerCluster(TestCredentials.USER_NAME, TestCredentials.PRIV_KEY)) {
            slaveServerCluster.start();
            int bits = 40;
            createFactorTest(bits, taskService);
        }
    }

    @Test
    public void testFactorBigNumberOneSlaveMassive() throws IOException {
        try (SlaveServerCluster slaveServerCluster = new SlaveServerCluster(TestCredentials.USER_NAME, TestCredentials.PRIV_KEY)) {
            slaveServerCluster.start();
            int bits = 40;
            for (int i = 0; i < 25; i++) {
                createFactorTest(bits, taskService);
            }
        }
    }

    @Test
    public void testFactorBigNumberTwoSlaves() throws IOException {
        try (SlaveServerCluster slaveServerCluster = new SlaveServerCluster(TestCredentials.USER_NAME, TestCredentials.PRIV_KEY, 2)) {
            slaveServerCluster.start();
            int bits = 40;
            createFactorTest(bits, taskService);
        }
    }

    @Test
    public void testFactorBigNumberTwoSlavesMassive() throws IOException {
        try (SlaveServerCluster slaveServerCluster = new SlaveServerCluster(TestCredentials.USER_NAME, TestCredentials.PRIV_KEY, 2)) {
            slaveServerCluster.start();
            int bits = 40;
            for (int i = 0; i < 25; i++) {
                createFactorTest(bits, taskService);
            }
        }
    }

    @Test
    public void testFactorBigNumberTwoSlavesMassiveGlitchy() throws IOException {
        try (SlaveServerCluster slaveServerCluster = new SlaveServerCluster(TestCredentials.USER_NAME, TestCredentials.PRIV_KEY, 2, true)) {
            slaveServerCluster.start();
            int bits = 40;
            for (int i = 0; i < 10; i++) {
                createFactorTest(bits, taskService);
            }
        }
    }

    @Test
    public void testFactorExtraBigNumberOneSlave() throws IOException {
        try (SlaveServerCluster slaveServerCluster = new SlaveServerCluster(TestCredentials.USER_NAME, TestCredentials.PRIV_KEY)) {
            slaveServerCluster.start();
            int bits = 45;
            createFactorTest(bits, taskService);
        }
    }

    @Test
    public void testFactorExtraBigNumberOneSlaveMassive() throws IOException {
        try (SlaveServerCluster slaveServerCluster = new SlaveServerCluster(TestCredentials.USER_NAME, TestCredentials.PRIV_KEY)) {
            slaveServerCluster.start();
            int bits = 45;
            for (int i = 0; i < 10; i++) {
                createFactorTest(bits, taskService);
            }
        }
    }

    @Test
    public void testFactorExtraBigNumberTwoSlaves() throws IOException {
        try (SlaveServerCluster slaveServerCluster = new SlaveServerCluster(TestCredentials.USER_NAME, TestCredentials.PRIV_KEY, 2)) {
            slaveServerCluster.start();
            int bits = 45;
            createFactorTest(bits, taskService);
        }
    }

    @Test
    public void testFactorExtraBigNumberTwoSlavesMassive() throws IOException {
        try (SlaveServerCluster slaveServerCluster = new SlaveServerCluster(TestCredentials.USER_NAME, TestCredentials.PRIV_KEY, 2)) {
            slaveServerCluster.start();
            int bits = 45;
            for (int i = 0; i < 10; i++) {
                createFactorTest(bits, taskService);
            }
        }
    }
}
