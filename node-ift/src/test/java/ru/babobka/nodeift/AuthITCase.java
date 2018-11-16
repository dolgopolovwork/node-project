package ru.babobka.nodeift;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.babobka.nodeconfigs.master.MasterServerConfig;
import ru.babobka.nodeconfigs.slave.SlaveServerConfig;
import ru.babobka.nodemasterserver.server.MasterServer;
import ru.babobka.nodesecurity.rsa.RSAConfig;
import ru.babobka.nodesecurity.rsa.RSAConfigFactory;
import ru.babobka.nodesecurity.rsa.RSAPublicKey;
import ru.babobka.nodeslaveserver.exception.SlaveStartupException;
import ru.babobka.nodeslaveserver.server.SlaveServer;
import ru.babobka.nodetester.master.MasterServerRunner;
import ru.babobka.nodetester.slave.SlaveServerRunner;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.enums.Env;
import ru.babobka.nodeutils.logger.SimpleLoggerFactory;
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
        Container.getInstance().put(SimpleLoggerFactory.debugLogger(AuthITCase.class.getSimpleName(), TextUtil.getEnv(Env.NODE_LOGS)));
        MasterServerRunner.init();
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

    @Test(expected = SlaveStartupException.class)
    public void testAuthFail() throws IOException {
        SlaveServer slaveServer = SlaveServerRunner.runSlaveServer("bad login", "bad password");
        interruptAndJoin(slaveServer);
    }

    @Test
    public void testAuthSuccess() throws IOException {
        SlaveServer slaveServer = SlaveServerRunner.runSlaveServer(TestCredentials.USER_NAME, TestCredentials.PASSWORD);
        interruptAndJoin(slaveServer);
    }

    @Test
    public void testAuth() throws IOException {
        SlaveServer slaveServer = SlaveServerRunner.runSlaveServer(TestCredentials.USER_NAME, TestCredentials.PASSWORD);
        interruptAndJoin(slaveServer);
    }

    @Test
    public void testMassAuthSuccess() throws IOException {
        int slaves = getTests();
        List<SlaveServer> slaveServerList = new ArrayList<>(slaves);
        for (int i = 0; i < slaves; i++) {
            slaveServerList.add(SlaveServerRunner.runSlaveServer(TestCredentials.USER_NAME, TestCredentials.PASSWORD));
        }
        interruptAndJoin(slaveServerList);
    }

    @Test
    public void testMassAuthSuccessParallel() throws IOException {
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
                        SlaveServer slaveServer = SlaveServerRunner.runSlaveServer(TestCredentials.USER_NAME, TestCredentials.PASSWORD);
                        interruptAndJoin(slaveServer);
                    } catch (IOException e) {
                        e.printStackTrace();
                        authFail.set(true);
                        break;
                    }
                }
            });
            authThreads[i].start();
        }
        joinAll(authThreads);
        assertFalse(authFail.get());
    }

    @Test
    public void testMassAuthFail() throws IOException {
        int slaves = getTests();
        for (int i = 0; i < slaves; i++) {
            try {
                SlaveServer slaveServer = SlaveServerRunner.runSlaveServer("bad_user", "bad_password");
                interruptAndJoin(slaveServer);
                fail();
            } catch (SlaveStartupException e) {
                //that's ok
            }
        }
    }

    @Test(expected = SlaveStartupException.class)
    public void testServerAuthFail() throws IOException {
        SlaveServerConfig slaveServerConfig = Container.getInstance().get(SlaveServerConfig.class);
        try {
            RSAConfig rsaConfig = RSAConfigFactory.create(128);
            slaveServerConfig.setServerPublicKey(rsaConfig.getPublicKey());
            SlaveServer slaveServer = SlaveServerRunner.runSlaveServer(TestCredentials.USER_NAME, TestCredentials.PASSWORD);
            interruptAndJoin(slaveServer);
        } finally {
            //возвращаем публичный ключ обратно
            MasterServerConfig masterServerConfig = Container.getInstance().get(MasterServerConfig.class);
            slaveServerConfig.setServerPublicKey(masterServerConfig.getSecurity().getRsaConfig().getPublicKey());
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

    private void interruptAndJoin(List<SlaveServer> slaveServers) {
        for (SlaveServer slaveServer : slaveServers) {
            interruptAndJoin(slaveServer);
        }
    }

    protected int getTests() {
        return 100;
    }
}
