package ru.babobka.nodeift;

import org.junit.*;
import ru.babobka.nodeconfigs.master.MasterServerConfig;
import ru.babobka.nodemasterserver.server.MasterServer;
import ru.babobka.nodesecurity.rsa.RSAPublicKey;
import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodeserials.NodeResponse;
import ru.babobka.nodeserials.enumerations.ResponseStatus;
import ru.babobka.nodeslaveserver.controller.AbstractSocketController;
import ru.babobka.nodeslaveserver.controller.MasterBackedSocketController;
import ru.babobka.nodetester.master.MasterServerRunner;
import ru.babobka.nodetester.network.TestableNodeConnection;
import ru.babobka.nodetester.slave.SlaveServerRunner;
import ru.babobka.nodetester.slave.cluster.SlaveServerCluster;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.enums.Env;
import ru.babobka.nodeutils.log.LoggerInit;
import ru.babobka.nodeutils.network.NodeConnection;
import ru.babobka.nodeutils.react.PubSub;
import ru.babobka.nodeutils.thread.PrettyNamedThreadPoolFactory;
import ru.babobka.nodeutils.util.TextUtil;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static ru.babobka.nodeift.EllipticCurveITCase.createFactorTest;
import static ru.babobka.nodeift.PrimeCounterITCase.PRIME_COUNTER_LARGE_RANGE_ANSWER;
import static ru.babobka.nodeift.PrimeCounterITCase.getLargeRangeRequest;

public class MasterBackedSlaveITCase {
    private static MasterServer masterServer;
    private ExecutorService socketControllerThreadPool;
    private PubSub<NodeRequest> requestStream;

    @Before
    public void before() {
        requestStream = new PubSub<>();
        socketControllerThreadPool = PrettyNamedThreadPoolFactory.fixedDaemonThreadPool("socket_controller");
    }

    @After
    public void after() {
        socketControllerThreadPool.shutdownNow();
        requestStream.close();
    }

    @BeforeClass
    public static void setUp() {
        Container.getInstance().clear();
        LoggerInit.initPersistentConsoleDebugLogger(TextUtil.getEnv(Env.NODE_LOGS), MasterBackedSlaveITCase.class.getSimpleName());
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

    @Test
    public void testPrimeCountTwoSlaves() throws IOException, InterruptedException {
        CountDownLatch responseWaiter = new CountDownLatch(1);
        AtomicReference<NodeResponse> responseReference = new AtomicReference<>();
        NodeConnection connection = new TestableNodeConnection(receivedObject -> {
            if (receivedObject instanceof NodeResponse) {
                NodeResponse response = (NodeResponse) receivedObject;
                responseReference.set(response);
                responseWaiter.countDown();
            }
        });
        try (SlaveServerCluster slaveServerCluster
                     = new SlaveServerCluster(TestCredentials.USER_NAME, TestCredentials.PASSWORD, 2);
             AbstractSocketController controller = new MasterBackedSocketController(connection, socketControllerThreadPool, requestStream)) {
            slaveServerCluster.start();
            controller.onExecute(getLargeRangeRequest());
            responseWaiter.await(2, TimeUnit.MINUTES);
            assertEquals((int) responseReference.get().getDataValue("primeCount"), PRIME_COUNTER_LARGE_RANGE_ANSWER);
        }
    }

    @Test
    public void testPrimeCountNoSlaves() throws IOException, InterruptedException {
        CountDownLatch responseWaiter = new CountDownLatch(1);
        AtomicReference<NodeResponse> responseReference = new AtomicReference<>();
        NodeConnection connection = new TestableNodeConnection(receivedObject -> {
            if (receivedObject instanceof NodeResponse) {
                NodeResponse response = (NodeResponse) receivedObject;
                responseReference.set(response);
                responseWaiter.countDown();
            }
        });
        try (AbstractSocketController controller = new MasterBackedSocketController(connection, socketControllerThreadPool, requestStream)) {
            controller.onExecute(getLargeRangeRequest());
            responseWaiter.await(2, TimeUnit.MINUTES);
            assertEquals(responseReference.get().getStatus(), ResponseStatus.NO_NODES);
        }
    }

    @Test
    public void testFactorTwoSlaves() throws IOException, InterruptedException {
        CountDownLatch responseWaiter = new CountDownLatch(1);
        AtomicReference<NodeResponse> responseReference = new AtomicReference<>();
        NodeConnection connection = new TestableNodeConnection(receivedObject -> {
            if (receivedObject instanceof NodeResponse) {
                NodeResponse response = (NodeResponse) receivedObject;
                responseReference.set(response);
                responseWaiter.countDown();
            }
        });
        try (SlaveServerCluster slaveServerCluster = new SlaveServerCluster(TestCredentials.USER_NAME, TestCredentials.PASSWORD, 2);
             AbstractSocketController controller = new MasterBackedSocketController(connection, socketControllerThreadPool, requestStream)) {
            slaveServerCluster.start();
            int bits = 45;
            BigInteger p = BigInteger.probablePrime(bits, new Random());
            BigInteger q = BigInteger.probablePrime(bits, new Random());
            NodeRequest request = createFactorTest(p, q);
            controller.onExecute(request);
            responseWaiter.await(2, TimeUnit.MINUTES);
            BigInteger factor = responseReference.get().getDataValue("factor");
            assertTrue(factor.equals(p) || factor.equals(q));
        }
    }

    @Test
    public void testFactorTwoSlavesMassive() throws IOException, InterruptedException {
        AtomicInteger successfulAnswers = new AtomicInteger();
        CountDownLatch responseWaiter = new CountDownLatch(getTests());
        NodeConnection connection = new TestableNodeConnection(receivedObject -> {
            if (receivedObject instanceof NodeResponse) {
                NodeResponse response = (NodeResponse) receivedObject;
                BigInteger number = response.getData().get("number");
                BigInteger factor = response.getData().get("factor");
                if (!factor.equals(BigInteger.ONE) && number.mod(factor).equals(BigInteger.ZERO)) {
                    successfulAnswers.incrementAndGet();
                }
                responseWaiter.countDown();
            }
        });
        try (SlaveServerCluster slaveServerCluster = new SlaveServerCluster(TestCredentials.USER_NAME, TestCredentials.PASSWORD, 2);
             AbstractSocketController controller = new MasterBackedSocketController(connection, socketControllerThreadPool, requestStream)) {
            slaveServerCluster.start();
            int bits = 35;
            for (int i = 0; i < getTests(); i++) {
                BigInteger p = BigInteger.probablePrime(bits, new Random());
                BigInteger q = BigInteger.probablePrime(bits, new Random());
                NodeRequest request = createFactorTest(p, q);
                controller.onExecute(request);
            }
            responseWaiter.await(2, TimeUnit.MINUTES);
            assertEquals(getTests(), successfulAnswers.get());
        }
    }

    @Test
    public void testPrimeCountTwoSlavesMassive() throws IOException, InterruptedException, ExecutionException {
        AtomicInteger successfulAnswers = new AtomicInteger();
        CountDownLatch responseWaiter = new CountDownLatch(getTests());
        NodeConnection connection = new TestableNodeConnection(receivedObject -> {
            if (receivedObject instanceof NodeResponse) {
                NodeResponse response = (NodeResponse) receivedObject;
                int primeCount = response.getDataValue("primeCount");
                if (primeCount == PRIME_COUNTER_LARGE_RANGE_ANSWER) {
                    successfulAnswers.incrementAndGet();
                }
                responseWaiter.countDown();
            }
        });

        try (SlaveServerCluster slaveServerCluster = new SlaveServerCluster(TestCredentials.USER_NAME, TestCredentials.PASSWORD, 2);
             AbstractSocketController controller = new MasterBackedSocketController(connection, socketControllerThreadPool, requestStream)) {
            slaveServerCluster.start();
            for (int i = 0; i < getTests(); i++) {
                controller.onExecute(getLargeRangeRequest());
            }
            responseWaiter.await(2, TimeUnit.MINUTES);
            assertEquals(getTests(), successfulAnswers.get());
        }
    }

    @Test
    public void testFactorOneSlaveTooBigRequest() throws IOException, InterruptedException {
        CountDownLatch responseWaiter = new CountDownLatch(1);
        AtomicReference<NodeResponse> responseReference = new AtomicReference<>();
        NodeConnection connection = new TestableNodeConnection(receivedObject -> {
            if (receivedObject instanceof NodeResponse) {
                NodeResponse response = (NodeResponse) receivedObject;
                responseReference.set(response);
                responseWaiter.countDown();
            }
        });
        try (SlaveServerCluster slaveServerCluster = new SlaveServerCluster(TestCredentials.USER_NAME, TestCredentials.PASSWORD);
             AbstractSocketController controller = new MasterBackedSocketController(connection, socketControllerThreadPool, requestStream)) {
            slaveServerCluster.start();
            int bits = 256;
            BigInteger p = BigInteger.probablePrime(bits, new Random());
            BigInteger q = BigInteger.probablePrime(bits, new Random());
            NodeRequest request = createFactorTest(p, q);
            controller.onExecute(request);
            responseWaiter.await(2, TimeUnit.MINUTES);
            NodeResponse response = responseReference.get();
            assertEquals(response.getStatus(), ResponseStatus.VALIDATION_ERROR);
        }
    }

    protected int getTests() {
        return 35;
    }
}
