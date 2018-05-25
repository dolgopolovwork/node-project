package ru.babobka.nodeift;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.babobka.nodemasterserver.server.MasterServer;
import ru.babobka.nodemasterserver.server.config.MasterServerConfig;
import ru.babobka.nodemasterserver.slave.Sessions;
import ru.babobka.nodesecurity.rsa.RSAPublicKey;
import ru.babobka.nodeslaveserver.exception.AuthFailException;
import ru.babobka.nodeslaveserver.server.SlaveServer;
import ru.babobka.nodetester.master.MasterServerRunner;
import ru.babobka.nodetester.slave.SlaveServerRunner;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.enums.Env;
import ru.babobka.nodeutils.logger.NodeLogger;
import ru.babobka.nodeutils.logger.SimpleLogger;
import ru.babobka.nodeutils.logger.SimpleLoggerFactory;
import ru.babobka.nodeutils.util.TextUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Created by 123 on 18.05.2018.
 */
public class SessionAuthITCase {

    protected static MasterServer masterServer;

    @BeforeClass
    public static void setUp() throws IOException {
        Container.getInstance().put(SimpleLoggerFactory.debugLogger(SessionAuthITCase.class.getSimpleName(), TextUtil.getEnv(Env.NODE_LOGS)));
        MasterServerRunner.init();
        Container.getInstance().get(MasterServerConfig.class).getModes().setSingleSessionMode(true);
        MasterServerConfig masterServerConfig = Container.getInstance().get(MasterServerConfig.class);
        RSAPublicKey publicKey = masterServerConfig.getSecurity().getRsaConfig().getPublicKey();
        SlaveServerRunner.init(publicKey);
        masterServer = MasterServerRunner.runMasterServer();
    }

    @AfterClass
    public static void tearDown() throws InterruptedException {
        masterServer.interrupt();
        masterServer.join();
        Container.getInstance().clear();
    }

    @Before
    public void waitUntilClearSession() throws InterruptedException {
        Sessions sessions = Container.getInstance().get(Sessions.class);
        while (!sessions.isEmpty()) {
            Thread.sleep(500);
        }
    }

    @Test
    public void testAuthTwoSessions() throws IOException, InterruptedException {
        SlaveServer slaveServer1 = null;
        SlaveServer slaveServer2 = null;
        try {
            slaveServer1 = SlaveServerRunner.runSlaveServer(TestCredentials.USER_NAME, TestCredentials.PASSWORD);
            try {
                slaveServer2 = SlaveServerRunner.runSlaveServer(TestCredentials.USER_NAME, TestCredentials.PASSWORD);
                fail();
            } catch (AuthFailException expected) {
                //that's ok
            }
        } finally {
            interruptAndJoin(slaveServer1, slaveServer2);
        }
    }

    @Test
    public void testAuthMultipleSessions() throws IOException, InterruptedException {
        for (int i = 0; i < 10; i++) {
            SlaveServer slaveServer = SlaveServerRunner.runSlaveServer(TestCredentials.USER_NAME, TestCredentials.PASSWORD);
            interruptAndJoin(slaveServer);
            waitUntilClearSession();
        }
    }

    @Test
    public void testAuthMultipleParallelSessions() throws IOException, InterruptedException {
        List<SlaveServer> slaveServers = Collections.synchronizedList(new ArrayList<>());
        AtomicInteger successfulAuthentications = new AtomicInteger();
        Thread[] authThreads = new Thread[10];
        for (int i = 0; i < authThreads.length; i++) {
            authThreads[i] = new Thread(() -> {
                try {
                    Thread.sleep(new Random().nextInt(1000));
                    slaveServers.add(SlaveServerRunner.runSlaveServer(TestCredentials.USER_NAME, TestCredentials.PASSWORD));
                    successfulAuthentications.incrementAndGet();
                } catch (IOException | InterruptedException expected) {
                    Thread.currentThread().interrupt();
                    //that's ok
                }
            });
            authThreads[i].start();
        }
        joinAll(authThreads);
        interruptAndJoin(slaveServers);
        assertEquals(successfulAuthentications.get(), 1);
    }

    @Test
    public void testAuthOneSession() throws IOException, InterruptedException {
        SlaveServer slaveServer1 = null;
        try {
            slaveServer1 = SlaveServerRunner.runSlaveServer(TestCredentials.USER_NAME, TestCredentials.PASSWORD);
        } finally {
            interruptAndJoin(slaveServer1);
        }
    }

    private void joinAll(Thread[] threads) {
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void interruptAndJoin(SlaveServer slaveServer) {
        try {
            if (slaveServer != null) {
                slaveServer.interrupt();
                slaveServer.join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void interruptAndJoin(SlaveServer... slaveServers) throws InterruptedException {
        for (SlaveServer slaveServer : slaveServers) {
            interruptAndJoin(slaveServer);
        }
    }

    private void interruptAndJoin(List<SlaveServer> slaveServers) throws InterruptedException {
        for (SlaveServer slaveServer : slaveServers) {
            interruptAndJoin(slaveServer);
        }
    }

}
