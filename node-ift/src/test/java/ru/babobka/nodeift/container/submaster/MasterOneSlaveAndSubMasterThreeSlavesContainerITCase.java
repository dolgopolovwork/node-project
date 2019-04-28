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

public class MasterOneSlaveAndSubMasterThreeSlavesContainerITCase extends AbstractContainerITCase {

    private static final GenericContainer master = createMaster();
    private static final GenericContainer slave = createSlave();
    private static final GenericContainer submaster = createSubMaster();
    private static final List<GenericContainer> submasterSlaves
            = Arrays.asList(createSubMasterSlave(), createSubMasterSlave(), createSubMasterSlave());

    @BeforeClass
    public static void runContainers() throws InterruptedException {
        master.start();
        Thread.sleep(2_000);
        submaster.start();
        Thread.sleep(2_000);
        slave.start();
        Thread.sleep(2_000);
        submasterSlaves.forEach(GenericContainer::start);
        Thread.sleep(2_000);
    }

    @AfterClass
    public static void stopContainers() {
        slave.stop();
        submasterSlaves.forEach(GenericContainer::stop);
        submaster.stop();
        master.stop();
    }

    @Test
    public void testFactor() throws IOException, InterruptedException, ExecutionException {
        assertTrue(isMasterHealthy(master));
        assertTrue(isSubmasterHealthy(submaster));
        assertEquals(2, getMasterClusterSize(master));
        assertEquals(3, getSubmasterClusterSize(submaster));
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
    }

    @Test
    public void testFactorMassive() throws IOException, InterruptedException, ExecutionException {
        assertTrue(isMasterHealthy(master));
        assertTrue(isSubmasterHealthy(submaster));
        assertEquals(2, getMasterClusterSize(master));
        assertEquals(3, getSubmasterClusterSize(submaster));
        try (Client client = new Client("localhost", getMasterClientPort(master))) {
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

    @Test
    public void testPrimeCount() throws IOException, InterruptedException, ExecutionException {
        assertTrue(isMasterHealthy(master));
        assertTrue(isSubmasterHealthy(submaster));
        assertEquals(2, getMasterClusterSize(master));
        assertEquals(3, getSubmasterClusterSize(submaster));
        try (Client client = new Client("localhost", getMasterClientPort(master))) {
            Future<NodeResponse> future = client.executeTask(getLargeRangeRequest());
            NodeResponse response = future.get();
            assertEquals((int) response.getDataValue("primeCount"), PRIME_COUNTER_LARGE_RANGE_ANSWER);
        }
    }

    @Test
    public void testFactorTooBigRequest() throws IOException, InterruptedException, ExecutionException {
        assertTrue(isMasterHealthy(master));
        assertTrue(isSubmasterHealthy(submaster));
        assertEquals(2, getMasterClusterSize(master));
        assertEquals(3, getSubmasterClusterSize(submaster));
        try (Client client = new Client("localhost", getMasterClientPort(master))) {
            int bits = 256;
            BigInteger p = BigInteger.probablePrime(bits, new Random());
            BigInteger q = BigInteger.probablePrime(bits, new Random());
            NodeRequest request = createFactorTest(p, q);
            Future<NodeResponse> future = client.executeTask(request);
            NodeResponse response = future.get();
            assertEquals(response.getStatus(), ResponseStatus.VALIDATION_ERROR);
        }
    }
}
