package ru.babobka.nodeift;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.babobka.nodeift.master.MasterServerRunner;
import ru.babobka.nodeift.slave.SlaveServerCluster;
import ru.babobka.nodeift.slave.SlaveServerRunner;
import ru.babobka.nodemasterserver.exception.TaskExecutionException;
import ru.babobka.nodemasterserver.server.MasterServer;
import ru.babobka.nodemasterserver.service.TaskService;
import ru.babobka.nodemasterserver.task.TaskExecutionResult;
import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodeutils.container.ApplicationContainer;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.logger.SimpleLogger;

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
public class EllipticCurveTest {

    private static final String LOGIN = "test_user";
    private static final String PASSWORD = "test_password";
    private static final String TASK_NAME = "ru.babobka.factor.task.EllipticCurveFactorTask";
    private static MasterServer masterServer;
    private static TaskService taskService;

    @BeforeClass
    public static void setUp() {
        new ApplicationContainer() {
            @Override
            public void contain(Container container) {
                try {
                    container.put(new SimpleLogger("EllipticCurveTest", System.getenv("NODE_IFT_LOGS"), "EllipticCurveTest", true));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.contain(Container.getInstance());
        MasterServerRunner.init();
        SlaveServerRunner.init();
        masterServer = MasterServerRunner.runMasterServer();
        taskService = Container.getInstance().get(TaskService.class);
    }

    @AfterClass
    public static void tearDown() {
        masterServer.interrupt();
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
        SimpleLogger logger = Container.getInstance().get(SimpleLogger.class);
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
