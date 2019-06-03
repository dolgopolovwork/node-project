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
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static ru.babobka.nodeift.EllipticCurveITCase.createFactorTest;

public class SlowMasterStartupITCase extends AbstractContainerITCase {

    private static final GenericContainer master = createMaster();
    private static final GenericContainer slave = createSlave();

    @BeforeClass
    public static void runContainers() throws InterruptedException {
        slave.start();
        Thread.sleep(5_000);
        master.start();
        Thread.sleep(5_000);
    }

    @AfterClass
    public static void stopContainer() {
        master.close();
        slave.close();
    }

    @Test
    public void testFactorMassive() throws IOException, InterruptedException, ExecutionException {
        assertTrue(isMasterHealthy(master));
        assertEquals(1, getMasterClusterSize(master));
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
}
