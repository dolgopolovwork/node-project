package ru.babobka.nodeift.slave;

import ru.babobka.nodeslaveserver.server.SlaveServer;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 123 on 11.11.2017.
 */
public class SlaveServerCluster implements Closeable {
    private List<SlaveServer> slaveServerList = new ArrayList<>();

    public SlaveServerCluster(String login, String password, int slaves) throws IOException {
        if (login == null || password == null) {
            throw new IllegalArgumentException("Both login and password must be set");
        } else if (slaves < 1) {
            throw new IllegalArgumentException("There must be at least one slave in cluster");
        }
        for (int i = 0; i < slaves; i++) {
            slaveServerList.add(SlaveServerRunner.getSlaveServer(login, password));
        }
    }

    public SlaveServerCluster(String login, String password) throws IOException {
        this(login, password, 1);
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
    }
}
