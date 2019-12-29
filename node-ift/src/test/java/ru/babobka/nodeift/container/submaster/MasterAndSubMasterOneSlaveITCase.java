package ru.babobka.nodeift.container.submaster;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.testcontainers.containers.GenericContainer;
import ru.babobka.nodeclient.Client;
import ru.babobka.nodeift.container.AbstractContainerITCase;
import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodeserials.NodeResponse;
import ru.babobka.nodeserials.enumerations.ResponseStatus;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static ru.babobka.nodeift.EllipticCurveITCase.createFactorTest;
import static ru.babobka.nodeift.PrimeCounterITCase.PRIME_COUNTER_LARGE_RANGE_ANSWER;
import static ru.babobka.nodeift.PrimeCounterITCase.getLargeRangeRequest;

public class MasterAndSubMasterOneSlaveITCase extends AbstractContainerITCase {

    private static final GenericContainer postgres = createPostgres();
    private static final GenericContainer master = createMaster();
    private static final GenericContainer submaster = createSubMaster();
    private static final GenericContainer submasterSlave = createSubMasterSlave();

    @BeforeClass
    public static void runContainers() throws InterruptedException {
        postgres.start();
        master.start();
        Thread.sleep(MASTER_SERVER_WAIT_MILLIS);
        submaster.start();
        Thread.sleep(MASTER_SERVER_WAIT_MILLIS);
        submasterSlave.start();
        Thread.sleep(MASTER_SERVER_WAIT_MILLIS);
    }

    @AfterClass
    public static void stopContainers() {
        postgres.stop();
        submasterSlave.stop();
        submaster.stop();
        master.stop();
    }

    @Test
    public void testFactor() throws IOException, InterruptedException, ExecutionException {
        assertTrue(isMasterHealthy(master));
        assertTrue(isSubmasterHealthy(submaster));
        assertEquals(1, getMasterClusterSize(master));
        assertEquals(1, getSubmasterClusterSize(submaster));
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
        assertTrue(isSubmasterHealthy(submaster));
        assertEquals(1, getMasterClusterSize(master));
        assertEquals(1, getSubmasterClusterSize(submaster));
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
        assertTrue(isSubmasterHealthy(submaster));
        assertEquals(1, getMasterClusterSize(master));
        assertEquals(1, getSubmasterClusterSize(submaster));
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
        assertTrue(isSubmasterHealthy(submaster));
        assertEquals(1, getMasterClusterSize(master));
        assertEquals(1, getSubmasterClusterSize(submaster));
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
