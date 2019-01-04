package ru.babobka.nodeift.lags;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import ru.babobka.nodebusiness.dao.CacheDAO;
import ru.babobka.nodeconfigs.master.MasterServerConfig;
import ru.babobka.nodemasterserver.monitoring.TaskMonitoringService;
import ru.babobka.nodemasterserver.service.TaskService;
import ru.babobka.nodesecurity.rsa.RSAPublicKey;
import ru.babobka.nodetester.key.TesterKey;
import ru.babobka.nodetester.master.MasterServerRunner;
import ru.babobka.nodetester.network.LaggyNodeConnectionFactory;
import ru.babobka.nodetester.slave.SlaveServerRunner;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.container.Properties;
import ru.babobka.nodeutils.enums.Env;
import ru.babobka.nodeutils.logger.SimpleLoggerFactory;
import ru.babobka.nodeutils.util.TextUtil;

import java.io.IOException;

/**
 * Created by 123 on 18.02.2018.
 */
public class LagCacheITCase extends ru.babobka.nodeift.CacheITCase {

    @BeforeClass
    public static void setUp() throws IOException {
        Container.getInstance().put(SimpleLoggerFactory.debugLogger(LagCacheITCase.class.getSimpleName(), TextUtil.getEnv(Env.NODE_LOGS)));
        Properties.put(TesterKey.ENABLE_CACHE, true);
        Container.getInstance().put(new LaggyNodeConnectionFactory());
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

}
