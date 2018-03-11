package ru.babobka.nodeift;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.babobka.nodebusiness.dao.CacheDAO;
import ru.babobka.nodemasterserver.exception.TaskExecutionException;
import ru.babobka.nodemasterserver.server.MasterServer;
import ru.babobka.nodemasterserver.service.TaskMonitoringService;
import ru.babobka.nodemasterserver.service.TaskService;
import ru.babobka.nodemasterserver.task.TaskExecutionResult;
import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodetester.master.MasterServerRunner;
import ru.babobka.nodetester.slave.SlaveServerRunner;
import ru.babobka.nodetester.slave.cluster.SlaveServerCluster;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.logger.SimpleLogger;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static ru.babobka.nodeift.PrimeCounterITCase.*;

/**
 * Created by 123 on 18.02.2018.
 */
public class CacheITCase {
    private static MasterServer masterServer;
    private static final String LOGIN = "test_user";
    private static final String PASSWORD = "test_password";
    private static TaskService taskService;
    private static TaskMonitoringService monitoringService;
    private static CacheDAO cacheDAO;

    @BeforeClass
    public static void setUp() {
        try {
            Container.getInstance().put(SimpleLogger.debugLogger("CacheITCase", System.getenv("NODE_LOGS")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Container.getInstance().put("enableCache", true);
        MasterServerRunner.init();
        SlaveServerRunner.init();
        masterServer = MasterServerRunner.runMasterServer();
        taskService = Container.getInstance().get(TaskService.class);
        monitoringService = Container.getInstance().get(TaskMonitoringService.class);
        cacheDAO = Container.getInstance().get(CacheDAO.class);
    }

    @AfterClass
    public static void tearDown() throws InterruptedException {
        masterServer.interrupt();
        masterServer.join();
        Container.getInstance().clear();
    }

    @After
    public void clear() {
        monitoringService.clear();
        cacheDAO.clear();
    }

    @Test
    public void testCacheCountPrimesLittleRangeOneSlave() throws IOException, TaskExecutionException, InterruptedException {
        try (SlaveServerCluster slaveServerCluster = new SlaveServerCluster(LOGIN, PASSWORD)) {
            slaveServerCluster.start();
            NodeRequest request = getLittleRangeRequest();
            for (int i = 0; i < 5; i++) {
                TaskExecutionResult result = taskService.executeTask(request);
                assertEquals(result.getResult().get("primeCount"), PRIME_COUNTER_LITTLE_RANGE_ANSWER);
            }
        }
        assertEquals(monitoringService.getCacheHitCount(), 0);
    }

    @Test
    public void testCacheCountPrimesLargeRangeOneSlave() throws IOException, TaskExecutionException, InterruptedException {
        final int requests = 5;
        try (SlaveServerCluster slaveServerCluster = new SlaveServerCluster(LOGIN, PASSWORD)) {
            slaveServerCluster.start();
            NodeRequest request = getLargeRangeRequest();
            for (int i = 0; i < requests; i++) {
                TaskExecutionResult result = taskService.executeTask(request);
                assertEquals(result.getResult().get("primeCount"), PRIME_COUNTER_LARGE_RANGE_ANSWER);
            }
        }
        assertEquals(monitoringService.getCacheHitCount(), requests - 1);
    }

    @Test
    public void testCacheCountPrimesLargeRangeOneSlaveClosedCluster() throws IOException, TaskExecutionException, InterruptedException {
        final int requests = 5;
        NodeRequest request = getLargeRangeRequest();
        try (SlaveServerCluster slaveServerCluster = new SlaveServerCluster(LOGIN, PASSWORD)) {
            slaveServerCluster.start();
            TaskExecutionResult result = taskService.executeTask(request);
            assertEquals(result.getResult().get("primeCount"), PRIME_COUNTER_LARGE_RANGE_ANSWER);
        }
        for (int i = 0; i < requests - 1; i++) {
            TaskExecutionResult result = taskService.executeTask(request);
            assertEquals(result.getResult().get("primeCount"), PRIME_COUNTER_LARGE_RANGE_ANSWER);
        }
        assertEquals(monitoringService.getCacheHitCount(), requests - 1);
    }
}