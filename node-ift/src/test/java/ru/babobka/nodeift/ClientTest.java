package ru.babobka.nodeift;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.babobka.nodeclient.Client;
import ru.babobka.nodeift.master.MasterServerRunner;
import ru.babobka.nodeift.slave.SlaveServerCluster;
import ru.babobka.nodeift.slave.SlaveServerRunner;
import ru.babobka.nodemasterserver.server.MasterServer;
import ru.babobka.nodemasterserver.server.MasterServerConfig;
import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodeserials.NodeResponse;
import ru.babobka.nodeserials.enumerations.ResponseStatus;
import ru.babobka.nodeslaveserver.server.SlaveServerConfig;
import ru.babobka.nodeutils.container.ApplicationContainer;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.logger.SimpleLogger;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static ru.babobka.nodeift.EllipticCurveTest.createFactorTest;
import static ru.babobka.nodeift.PrimeCounterTest.PRIME_COUNTER_LARGE_RANGE_ANSWER;
import static ru.babobka.nodeift.PrimeCounterTest.getLargeRangeRequest;

/**
 * Created by 123 on 12.12.2017.
 */
public class ClientTest {
    private static final String LOGIN = "test_user";
    private static final String PASSWORD = "test_password";
    private static final String TASK_NAME = "ru.babobka.factor.task.EllipticCurveFactorTask";
    private static MasterServer masterServer;

    @BeforeClass
    public static void setUp() {
        new ApplicationContainer() {
            @Override
            public void contain(Container container) {
                try {
                    container.put(new SimpleLogger("ClientTest", System.getenv("NODE_IFT_LOGS"), "clientTest", true));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.contain(Container.getInstance());
        MasterServerRunner.init();
        SlaveServerRunner.init();
        masterServer = MasterServerRunner.runMasterServer();
    }

    @AfterClass
    public static void tearDown() {
        masterServer.interrupt();
    }

    @Test
    public void testPrimeCountNoSlaves() throws IOException, InterruptedException, ExecutionException {
        MasterServerConfig masterConfig = Container.getInstance().get(MasterServerConfig.class);
        SlaveServerConfig slaveServerConfig = Container.getInstance().get(SlaveServerConfig.class);
        try (Client client = new Client(slaveServerConfig.getServerHost(), masterConfig.getClientListenerPort())) {
            Future<NodeResponse> future = client.executeTask(getLargeRangeRequest());
            NodeResponse response = future.get();
            assertEquals(response.getStatus(), ResponseStatus.FAILED);
        }
    }

    @Test
    public void testPrimeCountTwoSlaves() throws IOException, InterruptedException, ExecutionException {
        MasterServerConfig masterConfig = Container.getInstance().get(MasterServerConfig.class);
        SlaveServerConfig slaveServerConfig = Container.getInstance().get(SlaveServerConfig.class);
        try (SlaveServerCluster slaveServerCluster = new SlaveServerCluster(LOGIN, PASSWORD, 2);
             Client client = new Client(slaveServerConfig.getServerHost(), masterConfig.getClientListenerPort())) {
            slaveServerCluster.start();
            Future<NodeResponse> future = client.executeTask(getLargeRangeRequest());
            NodeResponse response = future.get();
            assertEquals((int) response.getDataValue("primeCount"), PRIME_COUNTER_LARGE_RANGE_ANSWER);
        }
    }

    @Test
    public void testPrimeCountTwoSlavesMassive() throws IOException, InterruptedException, ExecutionException {
        MasterServerConfig masterConfig = Container.getInstance().get(MasterServerConfig.class);
        SlaveServerConfig slaveServerConfig = Container.getInstance().get(SlaveServerConfig.class);
        try (SlaveServerCluster slaveServerCluster = new SlaveServerCluster(LOGIN, PASSWORD, 2);
             Client client = new Client(slaveServerConfig.getServerHost(), masterConfig.getClientListenerPort())) {
            slaveServerCluster.start();
            for (int i = 0; i < 25; i++) {
                Future<NodeResponse> future = client.executeTask(getLargeRangeRequest());
                NodeResponse response = future.get();
                assertEquals((int) response.getDataValue("primeCount"), PRIME_COUNTER_LARGE_RANGE_ANSWER);
            }
        }
    }

    @Test
    public void testFactorTwoSlaves() throws IOException, InterruptedException, ExecutionException {
        MasterServerConfig masterConfig = Container.getInstance().get(MasterServerConfig.class);
        SlaveServerConfig slaveServerConfig = Container.getInstance().get(SlaveServerConfig.class);
        try (SlaveServerCluster slaveServerCluster = new SlaveServerCluster(LOGIN, PASSWORD, 2);
             Client client = new Client(slaveServerConfig.getServerHost(), masterConfig.getClientListenerPort())) {
            slaveServerCluster.start();
            int bits = 45;
            BigInteger p = BigInteger.probablePrime(bits, new Random());
            BigInteger q = BigInteger.probablePrime(bits, new Random());
            NodeRequest request = createFactorTest(p, q);
            Future<NodeResponse> future = client.executeTask(request);
            NodeResponse response = future.get();
            BigInteger factor = response.getDataValue("factor");
            assertTrue(factor.equals(p) || factor.equals(q));
        }
    }

    @Test
    public void testFactorTwoSlavesMassive() throws IOException, InterruptedException, ExecutionException {
        MasterServerConfig masterConfig = Container.getInstance().get(MasterServerConfig.class);
        SlaveServerConfig slaveServerConfig = Container.getInstance().get(SlaveServerConfig.class);
        try (SlaveServerCluster slaveServerCluster = new SlaveServerCluster(LOGIN, PASSWORD, 2);
             Client client = new Client(slaveServerConfig.getServerHost(), masterConfig.getClientListenerPort())) {
            slaveServerCluster.start();
            int bits = 35;
            for (int i = 0; i < 25; i++) {
                BigInteger p = BigInteger.probablePrime(bits, new Random());
                BigInteger q = BigInteger.probablePrime(bits, new Random());
                NodeRequest request = createFactorTest(p, q);
                Future<NodeResponse> future = client.executeTask(request);
                NodeResponse response = future.get();
                BigInteger factor = response.getDataValue("factor");
                assertTrue(factor.equals(p) || factor.equals(q));
            }
        }
    }

}
