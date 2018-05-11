package ru.babobka.nodeift.lags;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import ru.babobka.nodeift.AuthITCase;
import ru.babobka.nodetester.master.MasterServerRunner;
import ru.babobka.nodetester.network.LaggyNodeConnectionFactory;
import ru.babobka.nodetester.slave.SlaveServerRunner;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.enums.Env;
import ru.babobka.nodeutils.logger.SimpleLogger;
import ru.babobka.nodeutils.util.TextUtil;

import java.io.IOException;

/**
 * Created by 123 on 06.11.2017.
 */
public class LagAuthITCase extends AuthITCase {

    @BeforeClass
    public static void setUp() throws IOException {
        Container.getInstance().put(SimpleLogger.debugLogger(LagAuthITCase.class.getSimpleName(), TextUtil.getEnv(Env.NODE_LOGS)));
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

    @Override
    protected int getTests() {
        return 10;
    }

}