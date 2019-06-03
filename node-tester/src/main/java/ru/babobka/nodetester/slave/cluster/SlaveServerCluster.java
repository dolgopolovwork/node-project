package ru.babobka.nodetester.slave.cluster;

import lombok.NonNull;
import ru.babobka.nodeslaveserver.server.SlaveServer;
import ru.babobka.nodetester.slave.GlitchThread;
import ru.babobka.nodetester.slave.SlaveServerRunner;

import java.io.IOException;
import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 123 on 11.11.2017.
 */
public class SlaveServerCluster extends AbstractCluster {
    private final List<SlaveServer> slaveServerList = new ArrayList<>();
    private final Thread glitchThread;

    public SlaveServerCluster(String login, @NonNull PrivateKey privateKey, int slaves, boolean glitchMode) throws IOException {
        super(login, slaves);
        for (int i = 0; i < slaves; i++) {
            slaveServerList.add(SlaveServerRunner.getSlaveServer(login, privateKey));
        }
        if (glitchMode) {
            glitchThread = createGlitchThread(login, privateKey, slaveServerList);
        } else {
            glitchThread = null;
        }
    }

    static GlitchThread createGlitchThread(String login, PrivateKey privateKey, List<SlaveServer> slaveServerList) {
        return new GlitchThread(login, privateKey, slaveServerList);
    }

    public SlaveServerCluster(String login, PrivateKey privateKey, int slaves) throws IOException {
        this(login, privateKey, slaves, false);
    }

    public SlaveServerCluster(String login, PrivateKey privateKey) throws IOException {
        this(login, privateKey, 1, false);
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
