package ru.babobka.nodeslaveserver.server;

import java.io.Closeable;
import java.io.IOException;

import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.logger.SimpleLogger;

public final class SlaveCluster implements Closeable {

    private final SimpleLogger logger = Container.getInstance().get(SimpleLogger.class);

    private final SlaveServer[] slaveServers;

    private volatile boolean started;

    public SlaveCluster(int slaves, String serverHost, int port, String login, String password) throws IOException {
        slaveServers = new SlaveServer[slaves];
        for (int i = 0; i < slaves; i++) {
            slaveServers[i] = new SlaveServer(serverHost, port, login, password);
        }
    }

    public synchronized void start() {
        if (!started) {
            started = true;
            logger.info("Starting cluster");
            for (SlaveServer slave : slaveServers) {
                slave.start();
            }
        } else {
            throw new IllegalStateException("Cluster have been already started");
        }
    }

    @Override
    public synchronized void close() throws IOException {
        logger.info("Stopping cluster");
        for (SlaveServer slave : slaveServers) {
            try {
                if (slave.isAlive()) {
                    slave.interrupt();
                    slave.join();
                }
            } catch (InterruptedException e) {
                logger.error(e);
            }
        }
    }

}
