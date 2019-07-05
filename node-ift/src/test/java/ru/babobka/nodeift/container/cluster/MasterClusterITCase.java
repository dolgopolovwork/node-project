package ru.babobka.nodeift.container.cluster;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.testcontainers.containers.GenericContainer;
import ru.babobka.nodeclient.rpc.RpcClient;
import ru.babobka.nodeift.container.AbstractContainerITCase;
import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodeserials.NodeResponse;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Random;
import java.util.concurrent.TimeoutException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static ru.babobka.nodeift.EllipticCurveITCase.createFactorTest;
import static ru.babobka.nodeift.container.ContainerConfigs.SLAVE_SERVER_CONFIG_ENV_PREFIX;

public class MasterClusterITCase extends AbstractContainerITCase {

    private static final String FIRST_REGION = "first.region";
    private static final String SECOND_REGION = "second.region";
    private static final GenericContainer rmq = createRMQ();
    private static final GenericContainer masterFirstRegion = createMasterWithRMQ().withNetworkAliases(FIRST_REGION);
    private static final GenericContainer slaveFirstRegion = createSlave().withEnv(SLAVE_SERVER_CONFIG_ENV_PREFIX + "_MASTERSERVERHOST", FIRST_REGION);
    private static final GenericContainer masterSecondRegion = createMasterWithRMQ().withNetworkAliases(SECOND_REGION);
    private static final GenericContainer slaveSecondRegion = createSlave().withEnv(SLAVE_SERVER_CONFIG_ENV_PREFIX + "_MASTERSERVERHOST", SECOND_REGION);

    @BeforeClass
    public static void runContainers() throws InterruptedException {
        rmq.start();
        masterFirstRegion.start();
        masterSecondRegion.start();
        Thread.sleep(MASTER_SERVER_WAIT_MILLIS);
        slaveFirstRegion.start();
        slaveSecondRegion.start();
        Thread.sleep(SLAVE_SERVER_WAIT_MILLIS);
    }

    @AfterClass
    public static void stopContainer() {
        masterFirstRegion.close();
        masterSecondRegion.close();
        rmq.close();
        slaveFirstRegion.close();
        slaveSecondRegion.close();
    }

    @Test
    public void testFactor() throws IOException, InterruptedException, TimeoutException {
        assertTrue(isMasterHealthy(masterFirstRegion));
        assertTrue(isMasterHealthy(masterSecondRegion));
        assertEquals(1, getMasterClusterSize(masterFirstRegion));
        assertEquals(1, getMasterClusterSize(masterSecondRegion));
        int tests = 10;
        try (RpcClient client = new RpcClient("localhost", getRmqPort(rmq), RPC_REPLY_QUEUE)) {
            for (int i = 0; i < tests; i++) {
                int bits = 45;
                BigInteger p = BigInteger.probablePrime(bits, new Random());
                BigInteger q = BigInteger.probablePrime(bits, new Random());
                NodeRequest request = createFactorTest(p, q);
                NodeResponse response = client.call(request);
                BigInteger factor = response.getDataValue("factor");
                assertTrue(factor.equals(p) || factor.equals(q));
            }
        }
        assertEquals(tests / 2, getMasterTaskMonitoring(masterFirstRegion).getExecutedTasks());
        assertEquals(tests / 2, getMasterTaskMonitoring(masterSecondRegion).getExecutedTasks());
    }
}
