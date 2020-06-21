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

    public SlaveServerCluster(String login, int slaves, boolean glitchMode) throws IOException {
        super(login, slaves);
        for (int i = 0; i < slaves; i++) {
            slaveServerList.add(SlaveServerRunner.getSlaveServer(login));
        }
        if (glitchMode) {
            glitchThread = createGlitchThread(login, slaveServerList);
        } else {
            glitchThread = null;
        }
    }

    static GlitchThread createGlitchThread(String login, List<SlaveServer> slaveServerList) {
        return new GlitchThread(login, slaveServerList);
    }

    public SlaveServerCluster(String login, int slaves) throws IOException {
        this(login, slaves, false);
    }

    public SlaveServerCluster(String login) throws IOException {
        this(login, 1, false);
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

    private void interruptGlitchThread() {
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
