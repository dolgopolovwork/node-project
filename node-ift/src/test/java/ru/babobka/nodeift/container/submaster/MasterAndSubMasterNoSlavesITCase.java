package ru.babobka.nodeift.container.submaster;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.testcontainers.containers.GenericContainer;
import ru.babobka.nodeclient.Client;
import ru.babobka.nodeift.container.AbstractContainerITCase;
import ru.babobka.nodeserials.NodeResponse;
import ru.babobka.nodeserials.enumerations.ResponseStatus;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static ru.babobka.nodeift.PrimeCounterITCase.getLargeRangeRequest;

public class MasterAndSubMasterNoSlavesITCase extends AbstractContainerITCase {

    private static final GenericContainer postgres = createPostgres();
    private static final GenericContainer master = createMaster();
    private static final GenericContainer submaster = createSubMaster();

    @BeforeClass
    public static void runContainers() throws InterruptedException {
        postgres.start();
        master.start();
        Thread.sleep(MASTER_SERVER_WAIT_MILLIS);
        submaster.start();
        Thread.sleep(MASTER_SERVER_WAIT_MILLIS);
    }

    @AfterClass
    public static void stopContainer() {
        postgres.stop();
        master.close();
        submaster.close();
    }

    @Test
    public void testPrimeCountNoSlaves() throws IOException, InterruptedException, ExecutionException {
        assertTrue(isMasterHealthy(master));
        assertEquals(0, getMasterClusterSize(master));
        assertTrue(isSubmasterHealthy(submaster));
        assertEquals(0, getSubmasterClusterSize(submaster));
        int executedTaskSize = getMasterTaskMonitoring(master).getExecutedTasks();
        try (Client client = new Client("localhost", getMasterClientPort(master))) {
            Future<NodeResponse> future = client.executeTask(getLargeRangeRequest());
            NodeResponse response = future.get();
            assertEquals(response.getStatus(), ResponseStatus.NO_NODES);
        }
        assertEquals(executedTaskSize, getMasterTaskMonitoring(master).getExecutedTasks());
    }
}
