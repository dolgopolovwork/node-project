package ru.babobka.nodeift.container.master;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.testcontainers.containers.GenericContainer;
import ru.babobka.nodeclient.rpc.RpcClient;
import ru.babobka.nodeift.container.AbstractContainerITCase;
import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodeserials.NodeResponse;
import ru.babobka.nodeserials.enumerations.ResponseStatus;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeoutException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static ru.babobka.nodeift.EllipticCurveITCase.createFactorTest;
import static ru.babobka.nodeift.PrimeCounterITCase.PRIME_COUNTER_LARGE_RANGE_ANSWER;
import static ru.babobka.nodeift.PrimeCounterITCase.getLargeRangeRequest;

public class MasterThreeSlavesRMQITCase extends AbstractContainerITCase {

    private static final GenericContainer postgres = createPostgres();
    private static final GenericContainer master = createMasterWithRMQ();
    private static final GenericContainer rmq = createRMQ();
    private static final List<GenericContainer> slaves
            = Arrays.asList(createSlave(), createSlave(), createSlave());

    @BeforeClass
    public static void runContainers() throws InterruptedException {
        postgres.start();
        rmq.start();
        master.start();
        Thread.sleep(MASTER_SERVER_WAIT_MILLIS);
        slaves.forEach(GenericContainer::start);
        Thread.sleep(SLAVE_SERVER_WAIT_MILLIS);
    }

    @AfterClass
    public static void stopContainer() {
        postgres.stop();
        rmq.close();
        master.close();
        slaves.forEach(GenericContainer::stop);
    }

    @Test
    public void testFactor() throws IOException, InterruptedException, TimeoutException {
        assertTrue(isMasterHealthy(master));
        assertEquals(3, getMasterClusterSize(master));
        int executedTaskSize = getMasterTaskMonitoring(master).getExecutedTasks();
        try (RpcClient client = new RpcClient("localhost", getRmqPort(rmq), RPC_REPLY_QUEUE)) {
            int bits = 45;
            BigInteger p = BigInteger.probablePrime(bits, new Random());
            BigInteger q = BigInteger.probablePrime(bits, new Random());
            NodeRequest request = createFactorTest(p, q);
            NodeResponse response = client.call(request);
            BigInteger factor = response.getDataValue("factor");
            assertTrue(factor.equals(p) || factor.equals(q));
        }
        assertEquals(executedTaskSize + 1, getMasterTaskMonitoring(master).getExecutedTasks());
    }

    @Test
    public void testFactorMassive() throws IOException, InterruptedException, TimeoutException {
        assertTrue(isMasterHealthy(master));
        assertEquals(3, getMasterClusterSize(master));
        int executedTaskSize = getMasterTaskMonitoring(master).getExecutedTasks();
        int tests = 25;
        try (RpcClient client = new RpcClient("localhost", getRmqPort(rmq), RPC_REPLY_QUEUE)) {
            int bits = 35;
            for (int i = 0; i < tests; i++) {
                BigInteger p = BigInteger.probablePrime(bits, new Random());
                BigInteger q = BigInteger.probablePrime(bits, new Random());
                NodeRequest request = createFactorTest(p, q);
                NodeResponse response = client.call(request);
                BigInteger factor = response.getDataValue("factor");
                assertTrue(factor.equals(p) || factor.equals(q));
            }
        }
        assertEquals(executedTaskSize + tests, getMasterTaskMonitoring(master).getExecutedTasks());
    }

    @Test
    public void testPrimeCount() throws IOException, InterruptedException, TimeoutException {
        assertTrue(isMasterHealthy(master));
        assertEquals(3, getMasterClusterSize(master));
        int executedTaskSize = getMasterTaskMonitoring(master).getExecutedTasks();
        try (RpcClient client = new RpcClient("localhost", getRmqPort(rmq), RPC_REPLY_QUEUE)) {
            NodeResponse response = client.call(getLargeRangeRequest());
            assertEquals((int) response.getDataValue("primeCount"), PRIME_COUNTER_LARGE_RANGE_ANSWER);
        }
        assertEquals(executedTaskSize + 1, getMasterTaskMonitoring(master).getExecutedTasks());
    }

    @Test
    public void testFactorTooBigRequest() throws IOException, InterruptedException, TimeoutException {
        assertTrue(isMasterHealthy(master));
        assertEquals(3, getMasterClusterSize(master));
        int executedTaskSize = getMasterTaskMonitoring(master).getExecutedTasks();
        try (RpcClient client = new RpcClient("localhost", getRmqPort(rmq), RPC_REPLY_QUEUE)) {
            int bits = 256;
            BigInteger p = BigInteger.probablePrime(bits, new Random());
            BigInteger q = BigInteger.probablePrime(bits, new Random());
            NodeRequest request = createFactorTest(p, q);
            NodeResponse response = client.call(request);
            assertEquals(response.getStatus(), ResponseStatus.VALIDATION_ERROR);
        }
        assertEquals(executedTaskSize, getMasterTaskMonitoring(master).getExecutedTasks());
    }
}
