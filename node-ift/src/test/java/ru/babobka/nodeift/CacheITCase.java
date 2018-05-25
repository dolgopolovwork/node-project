package ru.babobka.nodeift;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.babobka.nodebusiness.dao.CacheDAO;
import ru.babobka.nodemasterserver.exception.TaskExecutionException;
import ru.babobka.nodemasterserver.monitoring.TaskMonitoringService;
import ru.babobka.nodemasterserver.server.MasterServer;
import ru.babobka.nodemasterserver.server.config.MasterServerConfig;
import ru.babobka.nodemasterserver.service.TaskService;
import ru.babobka.nodemasterserver.task.TaskExecutionResult;
import ru.babobka.nodesecurity.rsa.RSAPublicKey;
import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodetester.key.TesterKey;
import ru.babobka.nodetester.master.MasterServerRunner;
import ru.babobka.nodetester.slave.SlaveServerRunner;
import ru.babobka.nodetester.slave.cluster.SlaveServerCluster;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.container.Properties;
import ru.babobka.nodeutils.enums.Env;
import ru.babobka.nodeutils.logger.NodeLogger;
import ru.babobka.nodeutils.logger.SimpleLogger;
import ru.babobka.nodeutils.logger.SimpleLoggerFactory;
import ru.babobka.nodeutils.util.TextUtil;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static ru.babobka.nodeift.PrimeCounterITCase.*;

/**
 * Created by 123 on 18.02.2018.
 */
public class CacheITCase {
    protected static MasterServer masterServer;
    protected static TaskService taskService;
    protected static TaskMonitoringService monitoringService;
    protected static CacheDAO cacheDAO;

    @BeforeClass
    public static void setUp() throws IOException {
        Container.getInstance().put(SimpleLoggerFactory.debugLogger(CacheITCase.class.getSimpleName(), TextUtil.getEnv(Env.NODE_LOGS)));
        Properties.put(TesterKey.ENABLE_CACHE, true);
        MasterServerRunner.init();
        MasterServerConfig masterServerConfig = Container.getInstance().get(MasterServerConfig.class);
        RSAPublicKey publicKey = masterServerConfig.getSecurity().getRsaConfig().getPublicKey();
        SlaveServerRunner.init(publicKey);
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
        try (SlaveServerCluster slaveServerCluster = new SlaveServerCluster(TestCredentials.USER_NAME, TestCredentials.PASSWORD)) {
            slaveServerCluster.start();
            NodeRequest request = getLittleRangeRequest();
            for (int i = 0; i < getTests(); i++) {
                TaskExecutionResult result = taskService.executeTask(request);
                assertEquals(result.getResult().get("primeCount"), PRIME_COUNTER_LITTLE_RANGE_ANSWER);
            }
        }
        assertEquals(monitoringService.getCacheHitCount(), 0);
    }

    @Test
    public void testCacheCountPrimesLargeRangeOneSlave() throws IOException, TaskExecutionException, InterruptedException {
        final int requests = getTests();
        try (SlaveServerCluster slaveServerCluster = new SlaveServerCluster(TestCredentials.USER_NAME, TestCredentials.PASSWORD)) {
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
        final int requests = getTests();
        NodeRequest request = getLargeRangeRequest();
        try (SlaveServerCluster slaveServerCluster = new SlaveServerCluster(TestCredentials.USER_NAME, TestCredentials.PASSWORD)) {
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

    protected int getTests() {
        return 15;
    }
}
