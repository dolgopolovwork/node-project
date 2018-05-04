package ru.babobka.nodeift.lags;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import ru.babobka.nodeift.AuthCommonTasksITCase;
import ru.babobka.nodetester.master.MasterServerRunner;
import ru.babobka.nodetester.network.LaggyNodeConnectionFactory;
import ru.babobka.nodetester.slave.SlaveServerRunner;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.enums.Env;
import ru.babobka.nodeutils.logger.SimpleLogger;
import ru.babobka.nodeutils.util.TextUtil;

import java.io.IOException;

/**
 * Created by 123 on 08.04.2018.
 */
public class LagAuthCommonTasksITCase extends AuthCommonTasksITCase {

    @BeforeClass
    public static void setUp() throws IOException {
        Container.getInstance().put(SimpleLogger.debugLogger(LagAuthCommonTasksITCase.class.getSimpleName(), TextUtil.getEnv(Env.NODE_LOGS)));
        Container.getInstance().put(new LaggyNodeConnectionFactory());
        MasterServerRunner.init();
        SlaveServerRunner.init();
        masterServer = MasterServerRunner.runMasterServer();
    }

    @AfterClass
    public static void tearDown() throws InterruptedException {
        masterServer.interrupt();
        masterServer.join();
        Container.getInstance().clear();
    }
}
