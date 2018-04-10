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
import ru.babobka.nodeutils.enums.Env;
import ru.babobka.nodeutils.logger.SimpleLogger;
import ru.babobka.nodeutils.util.TextUtil;

import java.io.IOException;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import static org.junit.Assert.assertTrue;

/**
 * Created by 123 on 23.11.2017.
 */
public class EllipticCurveITCase {

    private static final String LOGIN = "test_user";
    private static final String PASSWORD = "test_password";
    private static final String TASK_NAME = "ru.babobka.factor.task.EllipticCurveFactorTask";
    protected static MasterServer masterServer;
    protected static TaskService taskService;

    @BeforeClass
    public static void setUp() throws IOException {
        Container.getInstance().put(SimpleLogger.debugLogger(EllipticCurveITCase.class.getSimpleName(), TextUtil.getEnv(Env.NODE_LOGS)));
        MasterServerRunner.init();
        SlaveServerRunner.init();
        masterServer = MasterServerRunner.runMasterServer();
        taskService = Container.getInstance().get(TaskService.class);
    }

    @AfterClass
    public static void tearDown() throws InterruptedException {
        masterServer.interrupt();
        masterServer.join();
        Container.getInstance().clear();
    }

    private static void createFactorTest(int primeBitLength, TaskService taskService) throws TaskExecutionException {
        BigInteger p = BigInteger.probablePrime(primeBitLength, new Random());
        BigInteger q = BigInteger.probablePrime(primeBitLength, new Random());
        NodeRequest request = createFactorRequest(p.multiply(q));
        TaskExecutionResult result = taskService.executeTask(request);
        BigInteger factor = (BigInteger) result.getResult().get("factor");
        assertTrue(factor.equals(p) || factor.equals(q));
    }

    private static NodeRequest createFactorRequest(BigInteger number) {
        Map<String, Serializable> data = new HashMap<>();
        data.put("number", number);
        return NodeRequest.regular(UUID.randomUUID(), TASK_NAME, data);
    }

    static NodeRequest createFactorTest(BigInteger p, BigInteger q) {
        return createFactorRequest(p.multiply(q));
    }

    @Test
    public void testFactorMediumNumberOneSlave() throws IOException, TaskExecutionException, InterruptedException {
        try (SlaveServerCluster slaveServerCluster = new SlaveServerCluster(LOGIN, PASSWORD)) {
            slaveServerCluster.start();
            int bits = 32;
            createFactorTest(bits, taskService);
        }
    }

    @Test
    public void testFactorMediumNumberTwoSlaves() throws IOException, TaskExecutionException, InterruptedException {
        try (SlaveServerCluster slaveServerCluster = new SlaveServerCluster(LOGIN, PASSWORD, 2)) {
            slaveServerCluster.start();
            int bits = 32;
            createFactorTest(bits, taskService);
        }
    }

    @Test
    public void testFactorMediumNumberTwoSlavesMassive() throws IOException, TaskExecutionException, InterruptedException {
        try (SlaveServerCluster slaveServerCluster = new SlaveServerCluster(LOGIN, PASSWORD, 2)) {
            slaveServerCluster.start();
            int bits = 32;
            for (int i = 0; i < 50; i++) {
                createFactorTest(bits, taskService);
            }
        }
    }

    @Test
    public void testFactorBigNumberOneSlave() throws IOException, TaskExecutionException, InterruptedException {
        try (SlaveServerCluster slaveServerCluster = new SlaveServerCluster(LOGIN, PASSWORD)) {
            slaveServerCluster.start();
            int bits = 40;
            createFactorTest(bits, taskService);
        }
    }

    @Test
    public void testFactorBigNumberOneSlaveMassive() throws IOException, TaskExecutionException, InterruptedException {
        try (SlaveServerCluster slaveServerCluster = new SlaveServerCluster(LOGIN, PASSWORD)) {
            slaveServerCluster.start();
            int bits = 40;
            for (int i = 0; i < 25; i++) {
                createFactorTest(bits, taskService);
            }
        }
    }

    @Test
    public void testFactorBigNumberTwoSlaves() throws IOException, TaskExecutionException, InterruptedException {
        try (SlaveServerCluster slaveServerCluster = new SlaveServerCluster(LOGIN, PASSWORD, 2)) {
            slaveServerCluster.start();
            int bits = 40;
            createFactorTest(bits, taskService);
        }
    }

    @Test
    public void testFactorBigNumberTwoSlavesMassive() throws IOException, TaskExecutionException, InterruptedException {
        try (SlaveServerCluster slaveServerCluster = new SlaveServerCluster(LOGIN, PASSWORD, 2)) {
            slaveServerCluster.start();
            int bits = 40;
            for (int i = 0; i < 25; i++) {
                createFactorTest(bits, taskService);
            }
        }
    }

    @Test
    public void testFactorBigNumberTwoSlavesMassiveGlitchy() throws IOException, TaskExecutionException, InterruptedException {
        try (SlaveServerCluster slaveServerCluster = new SlaveServerCluster(LOGIN, PASSWORD, 2, true)) {
            slaveServerCluster.start();
            int bits = 40;
            for (int i = 0; i < 10; i++) {
                createFactorTest(bits, taskService);
            }
        }
    }

    @Test
    public void testFactorExtraBigNumberOneSlave() throws IOException, TaskExecutionException, InterruptedException {
        try (SlaveServerCluster slaveServerCluster = new SlaveServerCluster(LOGIN, PASSWORD)) {
            slaveServerCluster.start();
            int bits = 45;
            createFactorTest(bits, taskService);
        }
    }

    @Test
    public void testFactorExtraBigNumberOneSlaveMassive() throws IOException, TaskExecutionException, InterruptedException {
        try (SlaveServerCluster slaveServerCluster = new SlaveServerCluster(LOGIN, PASSWORD)) {
            slaveServerCluster.start();
            int bits = 45;
            for (int i = 0; i < 10; i++) {
                createFactorTest(bits, taskService);
            }
        }
    }

    @Test
    public void testFactorExtraBigNumberTwoSlaves() throws IOException, TaskExecutionException, InterruptedException {
        try (SlaveServerCluster slaveServerCluster = new SlaveServerCluster(LOGIN, PASSWORD, 2)) {
            slaveServerCluster.start();
            int bits = 45;
            createFactorTest(bits, taskService);
        }
    }

    @Test
    public void testFactorExtraBigNumberTwoSlavesMassive() throws IOException, TaskExecutionException, InterruptedException {
        try (SlaveServerCluster slaveServerCluster = new SlaveServerCluster(LOGIN, PASSWORD, 2)) {
            slaveServerCluster.start();
            int bits = 45;
            for (int i = 0; i < 10; i++) {
                createFactorTest(bits, taskService);
            }
        }
    }
}
