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

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static ru.babobka.nodeift.EllipticCurveITCase.createFactorTest;

@Ignore
public class MasterSlaveMassSuicideResurrectionITCase extends AbstractContainerITCase {

    private static final GenericContainer master = createMaster();
    private static final List<GenericContainer> slaves
            = new ArrayList<>(Arrays.asList(createSlave(), createSlave(), createSlave()));

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
        slaves.forEach(GenericContainer::stop);
        Thread.sleep(5_000);
        slaves.clear();
        assertTrue(isMasterHealthy(master));
        assertEquals(0, getMasterClusterSize(master));
        slaves.addAll(Arrays.asList(createSlave(), createSlave(), createSlave()));
        slaves.forEach(GenericContainer::start);
        Thread.sleep(5_000);
        assertTrue(isMasterHealthy(master));
        assertEquals(3, getMasterClusterSize(master));
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
}
