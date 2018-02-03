package ru.babobka.nodeift;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.babobka.nodemasterserver.exception.TaskExecutionException;
import ru.babobka.nodemasterserver.server.MasterServer;
import ru.babobka.nodemasterserver.service.TaskService;
import ru.babobka.nodemasterserver.task.TaskExecutionResult;
import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodetester.master.MasterServerRunner;
import ru.babobka.nodetester.slave.SlaveServerRunner;
import ru.babobka.nodetester.slave.cluster.SlaveServerCluster;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.logger.SimpleLogger;
import ru.babobka.nodeutils.util.MathUtil;

import java.io.IOException;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

/**
 * Created by 123 on 28.01.2018.
 */
public class PollardDlpITCase {

    private static final String LOGIN = "test_user";
    private static final String PASSWORD = "test_password";
    private static final String TASK_NAME = "ru.babobka.dlp.task.PollardDlpTask";
    private static MasterServer masterServer;
    private static TaskService taskService;

    @BeforeClass
    public static void setUp() {
        try {
            Container.getInstance().put(SimpleLogger.debugLogger("PollardDlpITCase", System.getenv("NODE_LOGS"), "PollardDlpITCase"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        MasterServerRunner.init();
        SlaveServerRunner.init();
        masterServer = MasterServerRunner.runMasterServer();
        taskService = Container.getInstance().get(TaskService.class);
    }

    @AfterClass
    public static void tearDown() {
        masterServer.interrupt();
    }

    private static void createDlpTest(int modBitLength, TaskService taskService) throws TaskExecutionException {
        NodeRequest request = createDlpRequest(modBitLength);
        TaskExecutionResult result = taskService.executeTask(request);
        BigInteger x = (BigInteger) result.getResult().get("x");
        BigInteger y = (BigInteger) result.getResult().get("y");
        BigInteger mod = (BigInteger) result.getResult().get("mod");
        BigInteger exp = (BigInteger) result.getResult().get("exp");
        assertEquals(x.modPow(exp, mod), y);
    }

    private static NodeRequest createDlpRequest(int modBitLength) {
        MathUtil.SafePrime safePrime = MathUtil.getSafePrime(modBitLength);
        BigInteger gen = MathUtil.getGenerator(safePrime);
        Map<String, Serializable> data = new HashMap<>();
        data.put("x", gen);
        data.put("y", BigInteger.valueOf(32));
        data.put("mod", safePrime.getPrime());
        return NodeRequest.regular(UUID.randomUUID(), TASK_NAME, data);
    }

    @Test
    public void testDlpMediumNumberOneSlave() throws IOException, TaskExecutionException, InterruptedException {
        try (SlaveServerCluster slaveServerCluster = new SlaveServerCluster(LOGIN, PASSWORD)) {
            slaveServerCluster.start();
            int bits = 25;
            createDlpTest(bits, taskService);
        }
    }

    @Test
    public void testDlpMediumNumberTwoSlaves() throws IOException, TaskExecutionException, InterruptedException {
        try (SlaveServerCluster slaveServerCluster = new SlaveServerCluster(LOGIN, PASSWORD, 2)) {
            slaveServerCluster.start();
            int bits = 25;
            createDlpTest(bits, taskService);
        }
    }

    @Test
    public void testDlpMediumNumberTwoSlavesMassive() throws IOException, TaskExecutionException, InterruptedException {
        try (SlaveServerCluster slaveServerCluster = new SlaveServerCluster(LOGIN, PASSWORD, 2)) {
            slaveServerCluster.start();
            int bits = 25;
            for (int i = 0; i < 25; i++) {
                createDlpTest(bits, taskService);
            }
        }
    }

    @Test
    public void testDlpBigNumberOneSlave() throws IOException, TaskExecutionException, InterruptedException {
        try (SlaveServerCluster slaveServerCluster = new SlaveServerCluster(LOGIN, PASSWORD)) {
            slaveServerCluster.start();
            int bits = 32;
            createDlpTest(bits, taskService);
        }
    }

    @Test
    public void testDlpBigNumberOneSlaveMassive() throws IOException, TaskExecutionException, InterruptedException {
        try (SlaveServerCluster slaveServerCluster = new SlaveServerCluster(LOGIN, PASSWORD)) {
            slaveServerCluster.start();
            int bits = 32;
            for (int i = 0; i < 15; i++) {
                createDlpTest(bits, taskService);
            }
        }
    }

    @Test
    public void testDlpBigNumberTwoSlaves() throws IOException, TaskExecutionException, InterruptedException {
        try (SlaveServerCluster slaveServerCluster = new SlaveServerCluster(LOGIN, PASSWORD, 2)) {
            slaveServerCluster.start();
            int bits = 32;
            createDlpTest(bits, taskService);
        }
    }

    @Test
    public void testDlpBigNumberTwoSlavesMassive() throws IOException, TaskExecutionException, InterruptedException {
        try (SlaveServerCluster slaveServerCluster = new SlaveServerCluster(LOGIN, PASSWORD, 2)) {
            slaveServerCluster.start();
            int bits = 32;
            for (int i = 0; i < 15; i++) {
                createDlpTest(bits, taskService);
            }
        }
    }

    @Test
    public void testDlpBigNumberTwoSlavesMassiveGlitchy() throws IOException, TaskExecutionException, InterruptedException {
        try (SlaveServerCluster slaveServerCluster = new SlaveServerCluster(LOGIN, PASSWORD, 2, true)) {
            slaveServerCluster.start();
            int bits = 32;
            for (int i = 0; i < 15; i++) {
                createDlpTest(bits, taskService);
            }
        }
    }

    @Test
    public void testDlpExtraBigNumberOneSlave() throws IOException, TaskExecutionException, InterruptedException {
        try (SlaveServerCluster slaveServerCluster = new SlaveServerCluster(LOGIN, PASSWORD)) {
            slaveServerCluster.start();
            int bits = 40;
            createDlpTest(bits, taskService);
        }
    }

    @Test
    public void testDlpExtraBigNumberOneSlaveMassive() throws IOException, TaskExecutionException, InterruptedException {
        try (SlaveServerCluster slaveServerCluster = new SlaveServerCluster(LOGIN, PASSWORD)) {
            slaveServerCluster.start();
            int bits = 40;
            for (int i = 0; i < 5; i++) {
                createDlpTest(bits, taskService);
            }
        }
    }

    @Test
    public void testDlpExtraBigNumberTwoSlaves() throws IOException, TaskExecutionException, InterruptedException {
        try (SlaveServerCluster slaveServerCluster = new SlaveServerCluster(LOGIN, PASSWORD, 2)) {
            slaveServerCluster.start();
            int bits = 40;
            createDlpTest(bits, taskService);
        }
    }

    @Test
    public void testDlpExtraBigNumberTwoSlavesMassive() throws IOException, TaskExecutionException, InterruptedException {
        try (SlaveServerCluster slaveServerCluster = new SlaveServerCluster(LOGIN, PASSWORD, 2)) {
            slaveServerCluster.start();
            int bits = 40;
            for (int i = 0; i < 5; i++) {
                createDlpTest(bits, taskService);
            }
        }
    }
}
