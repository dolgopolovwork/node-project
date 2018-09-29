package ru.babobka.nodeift;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.babobka.nodeconfigs.master.MasterServerConfig;
import ru.babobka.nodemasterserver.exception.TaskExecutionException;
import ru.babobka.nodemasterserver.server.MasterServer;
import ru.babobka.nodemasterserver.service.TaskService;
import ru.babobka.nodemasterserver.task.TaskExecutionResult;
import ru.babobka.nodesecurity.rsa.RSAPublicKey;
import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodeserials.data.Data;
import ru.babobka.nodetester.master.MasterServerRunner;
import ru.babobka.nodetester.slave.SlaveServerRunner;
import ru.babobka.nodetester.slave.cluster.SlaveServerCluster;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.enums.Env;
import ru.babobka.nodeutils.logger.SimpleLoggerFactory;
import ru.babobka.nodeutils.math.SafePrime;
import ru.babobka.nodeutils.util.MathUtil;
import ru.babobka.nodeutils.util.TextUtil;

import java.io.IOException;
import java.math.BigInteger;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Created by 123 on 28.01.2018.
 */
public class PollardDlpITCase {

    protected static MasterServer masterServer;
    protected static TaskService taskService;

    @BeforeClass
    public static void setUp() throws IOException {
        Container.getInstance().put(SimpleLoggerFactory.debugLogger(PollardDlpITCase.class.getSimpleName(), TextUtil.getEnv(Env.NODE_LOGS)));
        MasterServerRunner.init();
        MasterServerConfig masterServerConfig = Container.getInstance().get(MasterServerConfig.class);
        RSAPublicKey publicKey = masterServerConfig.getSecurity().getRsaConfig().getPublicKey();
        SlaveServerRunner.init(publicKey);
        masterServer = MasterServerRunner.runMasterServer();
        taskService = Container.getInstance().get(TaskService.class);
    }

    @AfterClass
    public static void tearDown() throws InterruptedException {
        masterServer.interrupt();
        masterServer.join();
        Container.getInstance().clear();
    }

    private void createDlpTest(int modBitLength, TaskService taskService) throws TaskExecutionException {
        NodeRequest request = createDlpRequest(modBitLength);
        TaskExecutionResult result = taskService.executeTask(request);
        BigInteger x = result.getData().get("x");
        BigInteger y = result.getData().get("y");
        BigInteger mod = result.getData().get("mod");
        BigInteger exp = result.getData().get("exp");
        if (exp == null) {
            System.err.print("failed request " + request);
            fail();
        }
        assertEquals(x.modPow(exp, mod), y);
    }

    protected NodeRequest createDlpRequest(int modBitLength) {
        SafePrime safePrime = SafePrime.random(modBitLength);
        BigInteger gen = MathUtil.getGenerator(safePrime);
        Data data = new Data();
        data.put("x", gen);
        data.put("y", BigInteger.valueOf(32));
        data.put("mod", safePrime.getPrime());
        return NodeRequest.regular(UUID.randomUUID(), "ru.babobka.dlp.task.PollardDlpTask", data);
    }

    @Test
    public void testDlpMediumNumberOneSlave() throws IOException, TaskExecutionException, InterruptedException {
        try (SlaveServerCluster slaveServerCluster = new SlaveServerCluster(TestCredentials.USER_NAME, TestCredentials.PASSWORD)) {
            slaveServerCluster.start();
            int bits = 25;
            createDlpTest(bits, taskService);
        }
    }

    @Test
    public void testDlpMediumNumberTwoSlaves() throws IOException, TaskExecutionException, InterruptedException {
        try (SlaveServerCluster slaveServerCluster = new SlaveServerCluster(TestCredentials.USER_NAME, TestCredentials.PASSWORD, 2)) {
            slaveServerCluster.start();
            int bits = 25;
            createDlpTest(bits, taskService);
        }
    }

    @Test
    public void testDlpMediumNumberTwoSlavesMassive() throws IOException, TaskExecutionException, InterruptedException {
        try (SlaveServerCluster slaveServerCluster = new SlaveServerCluster(TestCredentials.USER_NAME, TestCredentials.PASSWORD, 2)) {
            slaveServerCluster.start();
            int bits = 25;
            for (int i = 0; i < 25; i++) {
                createDlpTest(bits, taskService);
            }
        }
    }

    @Test
    public void testDlpBigNumberOneSlave() throws IOException, TaskExecutionException, InterruptedException {
        try (SlaveServerCluster slaveServerCluster = new SlaveServerCluster(TestCredentials.USER_NAME, TestCredentials.PASSWORD)) {
            slaveServerCluster.start();
            int bits = 32;
            createDlpTest(bits, taskService);
        }
    }

    @Test
    public void testDlpBigNumberOneSlaveMassive() throws IOException, TaskExecutionException, InterruptedException {
        try (SlaveServerCluster slaveServerCluster = new SlaveServerCluster(TestCredentials.USER_NAME, TestCredentials.PASSWORD)) {
            slaveServerCluster.start();
            int bits = 32;
            for (int i = 0; i < 15; i++) {
                createDlpTest(bits, taskService);
            }
        }
    }

    @Test
    public void testDlpBigNumberTwoSlaves() throws IOException, TaskExecutionException, InterruptedException {
        try (SlaveServerCluster slaveServerCluster = new SlaveServerCluster(TestCredentials.USER_NAME, TestCredentials.PASSWORD, 2)) {
            slaveServerCluster.start();
            int bits = 32;
            createDlpTest(bits, taskService);
        }
    }

    @Test
    public void testDlpBigNumberTwoSlavesMassive() throws IOException, TaskExecutionException, InterruptedException {
        try (SlaveServerCluster slaveServerCluster = new SlaveServerCluster(TestCredentials.USER_NAME, TestCredentials.PASSWORD, 2)) {
            slaveServerCluster.start();
            int bits = 32;
            for (int i = 0; i < 15; i++) {
                createDlpTest(bits, taskService);
            }
        }
    }

    @Test
    public void testDlpBigNumberTwoSlavesMassiveGlitchy() throws IOException, TaskExecutionException, InterruptedException {
        try (SlaveServerCluster slaveServerCluster = new SlaveServerCluster(TestCredentials.USER_NAME, TestCredentials.PASSWORD, 2, true)) {
            slaveServerCluster.start();
            int bits = 32;
            for (int i = 0; i < 135; i++) {
                createDlpTest(bits, taskService);
            }
        }
    }

    @Test
    public void testDlpExtraBigNumberOneSlave() throws IOException, TaskExecutionException, InterruptedException {
        try (SlaveServerCluster slaveServerCluster = new SlaveServerCluster(TestCredentials.USER_NAME, TestCredentials.PASSWORD)) {
            slaveServerCluster.start();
            int bits = 40;
            createDlpTest(bits, taskService);
        }
    }

    @Test
    public void testDlpExtraBigNumberOneSlaveMassive() throws IOException, TaskExecutionException, InterruptedException {
        try (SlaveServerCluster slaveServerCluster = new SlaveServerCluster(TestCredentials.USER_NAME, TestCredentials.PASSWORD)) {
            slaveServerCluster.start();
            int bits = 40;
            for (int i = 0; i < 5; i++) {
                createDlpTest(bits, taskService);
            }
        }
    }

    @Test
    public void testDlpExtraBigNumberTwoSlaves() throws IOException, TaskExecutionException, InterruptedException {
        try (SlaveServerCluster slaveServerCluster = new SlaveServerCluster(TestCredentials.USER_NAME, TestCredentials.PASSWORD, 2)) {
            slaveServerCluster.start();
            int bits = 40;
            createDlpTest(bits, taskService);
        }
    }

    @Test
    public void testDlpExtraBigNumberTwoSlavesMassive() throws IOException, TaskExecutionException, InterruptedException {
        try (SlaveServerCluster slaveServerCluster = new SlaveServerCluster(TestCredentials.USER_NAME, TestCredentials.PASSWORD, 2)) {
            slaveServerCluster.start();
            int bits = 40;
            for (int i = 0; i < 5; i++) {
                createDlpTest(bits, taskService);
            }
        }
    }
}
