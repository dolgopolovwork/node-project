package ru.babobka.nodeift;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.babobka.nodebusiness.dao.cache.CacheDAO;
import ru.babobka.nodebusiness.debug.DebugCredentials;
import ru.babobka.nodebusiness.monitoring.TaskMonitoringService;
import ru.babobka.nodeconfigs.master.MasterServerConfig;
import ru.babobka.nodemasterserver.server.MasterServer;
import ru.babobka.nodesecurity.keypair.KeyDecoder;
import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodetask.service.TaskService;
import ru.babobka.nodetester.key.TesterKey;
import ru.babobka.nodetester.master.MasterServerRunner;
import ru.babobka.nodetester.slave.SlaveServerRunner;
import ru.babobka.nodetester.slave.cluster.SlaveServerCluster;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.container.Properties;
import ru.babobka.nodeutils.log.LoggerInit;
import ru.babobka.nodeutils.util.TextUtil;

import java.io.IOException;
import java.security.PublicKey;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static ru.babobka.nodeift.PrimeCounterITCase.*;

/**
 * Created by 123 on 18.02.2018.
 */
public class CacheITCase {
    private static final Logger logger = Logger.getLogger(CacheITCase.class);
    protected static MasterServer masterServer;
    protected static TaskService taskService;
    protected static TaskMonitoringService monitoringService;
    protected static CacheDAO cacheDAO;

    @BeforeClass
    public static void setUp() throws IOException {
        LoggerInit.initPersistentConsoleDebugLogger(TextUtil.getLogFolder(), CacheITCase.class.getSimpleName());
        Properties.put(TesterKey.ENABLE_CACHE, true);
        MasterServerRunner.init();
        MasterServerConfig masterServerConfig = Container.getInstance().get(MasterServerConfig.class);
        PublicKey serverPublicKey = KeyDecoder.decodePublicKeyUnsafe(masterServerConfig.getKeyPair().getPubKey());
        SlaveServerRunner.init(serverPublicKey);
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
    public void testCacheCountPrimesLittleRangeOneSlave() throws IOException {
        try (SlaveServerCluster slaveServerCluster = new SlaveServerCluster(DebugCredentials.USER_NAME)) {
            slaveServerCluster.start();
            NodeRequest request = getLittleRangeRequest();
            for (int i = 0; i < getTests(); i++) {
                taskService.executeTask(request,
                        result -> assertEquals((int) result.getData().get("primeCount"), PRIME_COUNTER_LITTLE_RANGE_ANSWER),
                        error -> {
                            logger.error(error);
                            fail();
                        });
            }
        }
        assertEquals(monitoringService.getCacheHitCount(), 0);
    }

    @Test
    public void testCacheCountPrimesLargeRangeOneSlave() throws IOException {
        final int requests = getTests();
        try (SlaveServerCluster slaveServerCluster = new SlaveServerCluster(DebugCredentials.USER_NAME)) {
            slaveServerCluster.start();
            NodeRequest request = getLargeRangeRequest();
            for (int i = 0; i < requests; i++) {
                taskService.executeTask(request,
                        result -> assertEquals((int) result.getData().get("primeCount"), PRIME_COUNTER_LARGE_RANGE_ANSWER),
                        error -> {
                            logger.error(error);
                            fail();
                        });
            }
        }
        assertEquals(monitoringService.getCacheHitCount(), requests - 1);
    }

    @Test
    public void testCacheCountPrimesLargeRangeOneSlaveClosedCluster() throws IOException {
        final int requests = getTests();
        NodeRequest request = getLargeRangeRequest();
        try (SlaveServerCluster slaveServerCluster = new SlaveServerCluster(DebugCredentials.USER_NAME)) {
            slaveServerCluster.start();
            taskService.executeTask(request,
                    result -> assertEquals((int) result.getData().get("primeCount"), PRIME_COUNTER_LARGE_RANGE_ANSWER),
                    error -> {
                        logger.error(error);
                        fail();
                    });
        }
        for (int i = 0; i < requests - 1; i++) {
            taskService.executeTask(request,
                    result -> assertEquals((int) result.getData().get("primeCount"), PRIME_COUNTER_LARGE_RANGE_ANSWER),
                    error -> {
                        logger.error(error);
                        fail();
                    });
        }
        assertEquals(monitoringService.getCacheHitCount(), requests - 1);
    }

    protected int getTests() {
        return 15;
    }
}
