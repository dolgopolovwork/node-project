package ru.babobka.nodeift.slave;

import ru.babobka.nodeslaveserver.server.SlaveServer;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.logger.SimpleLogger;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 123 on 11.11.2017.
 */
public class SlaveServerCluster implements Closeable {
    private final SimpleLogger logger = Container.getInstance().get(SimpleLogger.class);
    private final List<SlaveServer> slaveServerList = new ArrayList<>();
    private final Thread glitchThread;

    public SlaveServerCluster(String login, String password, int slaves, boolean glitchMode) throws IOException {
        if (login == null || password == null) {
            throw new IllegalArgumentException("Both login and password must be set");
        } else if (slaves < 1) {
            throw new IllegalArgumentException("There must be at least one slave in cluster");
        }
        for (int i = 0; i < slaves; i++) {
            slaveServerList.add(SlaveServerRunner.getSlaveServer(login, password));
        }
        if (glitchMode) {
            glitchThread = new GlitchThread(login, password, slaveServerList);
            glitchThread.start();
        } else {
            glitchThread = null;
        }
    }

    public SlaveServerCluster(String login, String password, int slaves) throws IOException {
        this(login, password, slaves, false);
    }

    public SlaveServerCluster(String login, String password) throws IOException {
        this(login, password, 1, false);
    }

    public synchronized void start() throws InterruptedException {
        for (SlaveServer slaveServer : slaveServerList) {
            slaveServer.start();
        }
        Thread.sleep(1000);
    }

    @Override
    public synchronized void close() throws IOException {
        for (SlaveServer slaveServer : slaveServerList) {
            slaveServer.interrupt();
        }
        if (glitchThread != null) {
            glitchThread.interrupt();
            try {
                glitchThread.join();
            } catch (InterruptedException e) {
                glitchThread.interrupt();
                logger.error(e);
            }
        }
    }
}
