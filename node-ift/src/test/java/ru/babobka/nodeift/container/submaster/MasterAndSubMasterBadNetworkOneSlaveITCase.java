package ru.babobka.nodeift.container.submaster;

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
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static ru.babobka.nodeift.EllipticCurveITCase.createFactorTest;

@Ignore
public class MasterAndSubMasterBadNetworkOneSlaveITCase extends AbstractContainerITCase {

    private static final GenericContainer master = createMaster();
    private static final GenericContainer submaster = createSubMaster();
    private static final GenericContainer submasterSlave = createSubMasterSlave();

    @BeforeClass
    public static void runContainers() throws InterruptedException {
        master.start();
        Thread.sleep(2_000);
        submaster.start();
        Thread.sleep(2_000);
        submasterSlave.start();
        Thread.sleep(5_000);
    }

    @AfterClass
    public static void stopContainers() {
        submasterSlave.stop();
        submaster.stop();
        master.stop();
    }

    @Test
    public void testFactorMassive() throws IOException, InterruptedException, ExecutionException {
        int tests = 5;
        int bits = 35;
        for (int i = 0; i < tests; i++) {
            submaster.stop();
            Thread.sleep(5_000);
            submaster.start();
            Thread.sleep(10_000);
            assertTrue(isMasterHealthy(master));
            assertTrue(isSubmasterHealthy(submaster));
            assertEquals(1, getMasterClusterSize(master));
            assertEquals(1, getSubmasterClusterSize(submaster));
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
