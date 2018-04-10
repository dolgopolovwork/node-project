package ru.babobka.nodeift;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.babobka.nodemasterserver.server.MasterServer;
import ru.babobka.nodeslaveserver.exception.SlaveAuthFailException;
import ru.babobka.nodeslaveserver.server.SlaveServer;
import ru.babobka.nodetester.master.MasterServerRunner;
import ru.babobka.nodetester.slave.SlaveServerRunner;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.enums.Env;
import ru.babobka.nodeutils.logger.SimpleLogger;
import ru.babobka.nodeutils.util.TextUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

/**
 * Created by 123 on 06.11.2017.
 */
public class AuthITCase {

    protected static MasterServer masterServer;

    @BeforeClass
    public static void setUp() throws IOException {
        Container.getInstance().put(SimpleLogger.debugLogger(AuthITCase.class.getSimpleName(), TextUtil.getEnv(Env.NODE_LOGS)));
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

    @Test(expected = SlaveAuthFailException.class)
    public void testAuthFail() throws IOException {
        SlaveServerRunner.runSlaveServer("bad login", "bad password");
    }

    @Test
    public void testAuthSuccess() throws IOException {
        SlaveServer slaveServer = SlaveServerRunner.runSlaveServer("test_user", "test_password");
        slaveServer.interrupt();
    }

    @Test
    public void testMassAuthSuccess() throws IOException {
        int slaves = getTests();
        List<SlaveServer> slaveServerList = new ArrayList<>(slaves);
        for (int i = 0; i < slaves; i++) {
            slaveServerList.add(SlaveServerRunner.runSlaveServer("test_user", "test_password"));
        }
        for (SlaveServer slaveServer : slaveServerList) {
            slaveServer.interrupt();
        }
    }

    @Test
    public void testMassAuthSuccessParallel() throws IOException, InterruptedException {
        int cores = Runtime.getRuntime().availableProcessors();
        final AtomicBoolean authFail = new AtomicBoolean(false);
        Thread[] authThreads = new Thread[cores];
        for (int i = 0; i < cores; i++) {
            authThreads[i] = new Thread(() -> {
                for (int j = 0; j < getTests(); j++) {
                    if (authFail.get()) {
                        break;
                    }
                    try {
                        SlaveServerRunner.runSlaveServer("test_user", "test_password");
                    } catch (IOException e) {
                        e.printStackTrace();
                        authFail.set(true);
                        break;
                    }
                }
            });
            authThreads[i].start();
        }

        for (Thread thread : authThreads) {
            thread.join();
        }
        assertFalse(authFail.get());
    }

    @Test
    public void testMassAuthFail() throws IOException {
        int slaves = getTests();
        for (int i = 0; i < slaves; i++) {
            try {
                SlaveServerRunner.runSlaveServer("bad_user", "bad_password");
                fail();
            } catch (SlaveAuthFailException e) {
                //that's ok
            }
        }
    }

    protected int getTests() {
        return 100;
    }
}
