package ru.babobka.nodeift.container.master;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.testcontainers.containers.GenericContainer;
import ru.babobka.nodeclient.Client;
import ru.babobka.nodeift.container.AbstractContainerITCase;
import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodeserials.NodeResponse;
import ru.babobka.nodeserials.enumerations.ResponseStatus;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static ru.babobka.nodeift.EllipticCurveITCase.createFactorTest;
import static ru.babobka.nodeift.PrimeCounterITCase.PRIME_COUNTER_LARGE_RANGE_ANSWER;
import static ru.babobka.nodeift.PrimeCounterITCase.getLargeRangeRequest;

public class MasterThreeSlavesITCase extends AbstractContainerITCase {

    private static final GenericContainer master = createMaster();
    private static final List<GenericContainer> slaves
            = Arrays.asList(createSlave(), createSlave(), createSlave());

    @BeforeClass
    public static void runContainers() throws InterruptedException {
        master.start();
        Thread.sleep(2_000);
        slaves.forEach(GenericContainer::start);
        Thread.sleep(5_000);
    }

    @AfterClass
    public static void stopContainer() {
        master.close();
        slaves.forEach(GenericContainer::stop);
    }

    @Test
    public void testFactor() throws IOException, InterruptedException, ExecutionException {
        assertTrue(isMasterHealthy(master));
        assertEquals(3, getMasterClusterSize(master));
        int executedTaskSize = getMasterTaskMonitoring(master).getExecutedTasks();
        try (Client client = new Client("localhost", getMasterClientPort(master))) {
            int bits = 45;
            BigInteger p = BigInteger.probablePrime(bits, new Random());
            BigInteger q = BigInteger.probablePrime(bits, new Random());
            NodeRequest request = createFactorTest(p, q);
            Future<NodeResponse> future = client.executeTask(request);
            NodeResponse response = future.get();
            BigInteger factor = response.getDataValue("factor");
            assertTrue(factor.equals(p) || factor.equals(q));
        }
        assertEquals(executedTaskSize + 1, getMasterTaskMonitoring(master).getExecutedTasks());
    }

    @Test
    public void testFactorMassive() throws IOException, InterruptedException, ExecutionException {
        assertTrue(isMasterHealthy(master));
        assertEquals(3, getMasterClusterSize(master));
        int executedTaskSize = getMasterTaskMonitoring(master).getExecutedTasks();
        int tests = 25;
        try (Client client = new Client("localhost", getMasterClientPort(master))) {
            int bits = 35;
            for (int i = 0; i < tests; i++) {
                BigInteger p = BigInteger.probablePrime(bits, new Random());
                BigInteger q = BigInteger.probablePrime(bits, new Random());
                NodeRequest request = createFactorTest(p, q);
                Future<NodeResponse> future = client.executeTask(request);
                NodeResponse response = future.get();
                BigInteger factor = response.getDataValue("factor");
                assertTrue(factor.equals(p) || factor.equals(q));
            }
        }
        assertEquals(executedTaskSize + tests, getMasterTaskMonitoring(master).getExecutedTasks());
    }

    @Test
    public void testPrimeCount() throws IOException, InterruptedException, ExecutionException {
        assertTrue(isMasterHealthy(master));
        assertEquals(3, getMasterClusterSize(master));
        int executedTaskSize = getMasterTaskMonitoring(master).getExecutedTasks();
        try (Client client = new Client("localhost", getMasterClientPort(master))) {
            Future<NodeResponse> future = client.executeTask(getLargeRangeRequest());
            NodeResponse response = future.get();
            assertEquals((int) response.getDataValue("primeCount"), PRIME_COUNTER_LARGE_RANGE_ANSWER);
        }
        assertEquals(executedTaskSize + 1, getMasterTaskMonitoring(master).getExecutedTasks());
    }

    @Test
    public void testFactorTooBigRequest() throws IOException, InterruptedException, ExecutionException {
        assertTrue(isMasterHealthy(master));
        assertEquals(3, getMasterClusterSize(master));
        int executedTaskSize = getMasterTaskMonitoring(master).getExecutedTasks();
        try (Client client = new Client("localhost", getMasterClientPort(master))) {
            int bits = 256;
            BigInteger p = BigInteger.probablePrime(bits, new Random());
            BigInteger q = BigInteger.probablePrime(bits, new Random());
            NodeRequest request = createFactorTest(p, q);
            Future<NodeResponse> future = client.executeTask(request);
            NodeResponse response = future.get();
            assertEquals(response.getStatus(), ResponseStatus.VALIDATION_ERROR);
        }
        assertEquals(executedTaskSize, getMasterTaskMonitoring(master).getExecutedTasks());
    }
}
