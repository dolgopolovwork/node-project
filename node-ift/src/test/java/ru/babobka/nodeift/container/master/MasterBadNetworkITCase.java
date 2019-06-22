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
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static ru.babobka.nodeift.EllipticCurveITCase.createFactorTest;

public class MasterBadNetworkITCase extends AbstractContainerITCase {
    private static GenericContainer master = createMaster();
    private static final List<GenericContainer> slaves
            = Arrays.asList(createSlave(), createSlave(), createSlave());

    @BeforeClass
    public static void runContainers() throws InterruptedException {
        master.start();
        Thread.sleep(MASTER_SERVER_WAIT_MILLIS);
        slaves.forEach(GenericContainer::start);
        Thread.sleep(SLAVE_SERVER_WAIT_MILLIS);
    }

    @AfterClass
    public static void stopContainer() {
        master.close();
        slaves.forEach(GenericContainer::stop);
    }

    @Test
    public void testFactorMassive() throws IOException, InterruptedException, ExecutionException {
        int tests = 5;
        int bits = 35;
        for (int i = 0; i < tests; i++) {
            master.stop();
            Thread.sleep(5_000);
            master.start();
            Thread.sleep(10_000);
            assertTrue(isMasterHealthy(master));
            assertEquals(3, getMasterClusterSize(master));
            try (Client client = new Client("localhost", getMasterClientPort(master))) {
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
