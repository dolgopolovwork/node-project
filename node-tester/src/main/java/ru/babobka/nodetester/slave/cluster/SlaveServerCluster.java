package ru.babobka.nodetester.slave.cluster;

import ru.babobka.nodeslaveserver.server.SlaveServer;
import ru.babobka.nodetester.slave.GlitchThread;
import ru.babobka.nodetester.slave.SlaveServerRunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 123 on 11.11.2017.
 */
public class SlaveServerCluster extends AbstractCluster {
    private final List<SlaveServer> slaveServerList = new ArrayList<>();
    private final Thread glitchThread;

    public SlaveServerCluster(String login, String password, int slaves, boolean glitchMode) throws IOException {
        super(login, password, slaves);
        for (int i = 0; i < slaves; i++) {
            slaveServerList.add(SlaveServerRunner.getSlaveServer(login, password));
        }
        if (glitchMode) {
            glitchThread = createGlitchThread(login, password, slaveServerList);
        } else {
            glitchThread = null;
        }
    }

    static GlitchThread createGlitchThread(String login, String password, List<SlaveServer> slaveServerList) {
        return new GlitchThread(login, password, slaveServerList);
    }

    public SlaveServerCluster(String login, String password, int slaves) throws IOException {
        this(login, password, slaves, false);
    }

    public SlaveServerCluster(String login, String password) throws IOException {
        this(login, password, 1, false);
    }

    @Override
    protected void startImpl() {
        synchronized (slaveServerList) {
            for (SlaveServer slaveServer : slaveServerList) {
                slaveServer.start();
            }
        }
        if (glitchThread != null) {
            glitchThread.start();
        }
    }

    @Override
    protected void closeImpl() {
        interruptGlitchThread();
        synchronized (slaveServerList) {
            for (SlaveServer slaveServer : slaveServerList) {
                slaveServer.interrupt();
                try {
                    slaveServer.join();
                } catch (InterruptedException e) {
                    slaveServer.interrupt();
                }
            }
        }
    }

    protected void interruptGlitchThread() {
        if (glitchThread == null) {
            return;
        }
        glitchThread.interrupt();
        try {
            glitchThread.join();
        } catch (InterruptedException e) {
            glitchThread.interrupt();
        }
    }
}
