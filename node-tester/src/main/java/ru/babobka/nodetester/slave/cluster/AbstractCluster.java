package ru.babobka.nodetester.slave.cluster;

import ru.babobka.nodeutils.util.ArrayUtil;

import java.io.Closeable;
import java.io.IOException;

/**
 * Created by 123 on 01.02.2018.
 */
public abstract class AbstractCluster implements Closeable {
    private boolean closed;
    private boolean started;

    public AbstractCluster(String login, String password, int slaves) throws IOException {
        ArrayUtil.validateNonNull(login, password);
        if (slaves < 1) {
            throw new IllegalArgumentException("There must be at least one slave in cluster");
        }
    }

    public synchronized void start() {
        if (started) {
            return;
        }
        startImpl();
        started = true;
    }

    @Override
    public synchronized void close() throws IOException {
        if (closed) {
            return;
        }
        closeImpl();
        closed = true;
    }

    protected abstract void closeImpl();

    protected abstract void startImpl();
}
