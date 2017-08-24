package ru.babobka.nodeift;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.babobka.nodeift.master.MasterServerRunner;
import ru.babobka.nodeift.slave.SlaveServerRunner;
import ru.babobka.nodemasterserver.server.MasterServer;
import ru.babobka.nodeslaveserver.exception.SlaveAuthFailException;
import ru.babobka.nodeslaveserver.server.SlaveServer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Created by 123 on 06.11.2017.
 */
public class AuthTest {

    private static MasterServer masterServer;

    @BeforeClass
    public static void setUp() {
        MasterServerRunner.init();
        SlaveServerRunner.init();
        masterServer = MasterServerRunner.runMasterServer();
    }

    @AfterClass
    public static void tearDown() {
        masterServer.interrupt();
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
        int slaves = 100;
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
        final AtomicInteger authFails = new AtomicInteger();
        Thread[] authThreads = new Thread[cores];
        for (int i = 0; i < cores; i++) {
            authThreads[i] = new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < 20; i++) {
                        try {
                            SlaveServerRunner.runSlaveServer("test_user", "test_password");
                        } catch (IOException e) {
                            authFails.incrementAndGet();
                        }
                    }
                }
            });
            authThreads[i].start();
        }

        for (Thread thread : authThreads) {
            thread.join();
        }
        assertEquals(authFails.get(), 0);
    }

    @Test
    public void testMassAuthFail() throws IOException {
        int slaves = 100;
        for (int i = 0; i < slaves; i++) {
            try {
                SlaveServerRunner.runSlaveServer("bad_user", "bad_password");
                fail();
            } catch (SlaveAuthFailException e) {
                //that's ok
            }
        }
    }
}
