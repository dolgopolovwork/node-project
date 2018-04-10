package ru.babobka.nodeift.lags;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import ru.babobka.nodebusiness.dao.CacheDAO;
import ru.babobka.nodemasterserver.service.TaskMonitoringService;
import ru.babobka.nodemasterserver.service.TaskService;
import ru.babobka.nodetester.master.MasterServerRunner;
import ru.babobka.nodetester.network.LaggyNodeConnectionFactory;
import ru.babobka.nodetester.slave.SlaveServerRunner;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.container.Properties;
import ru.babobka.nodeutils.enums.Env;
import ru.babobka.nodeutils.logger.SimpleLogger;
import ru.babobka.nodeutils.util.TextUtil;

import java.io.IOException;

/**
 * Created by 123 on 18.02.2018.
 */
public class LagCacheITCase extends ru.babobka.nodeift.CacheITCase {

    @BeforeClass
    public static void setUp() throws IOException {
        Container.getInstance().put(SimpleLogger.debugLogger(LagCacheITCase.class.getSimpleName(), TextUtil.getEnv(Env.NODE_LOGS)));
        Properties.put("enableCache", true);
        Container.getInstance().put(new LaggyNodeConnectionFactory());
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

}
