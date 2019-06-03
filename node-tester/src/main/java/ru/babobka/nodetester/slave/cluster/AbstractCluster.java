package ru.babobka.nodetester.slave.cluster;

import lombok.NonNull;
import ru.babobka.nodeutils.util.TextUtil;

import java.io.Closeable;
import java.io.IOException;
import java.security.PrivateKey;

/**
 * Created by 123 on 01.02.2018.
 */
public abstract class AbstractCluster implements Closeable {
    private boolean closed;
    private boolean started;

    public AbstractCluster(String login, int slaves) {
        if (TextUtil.isEmpty(login)) {
            throw new IllegalArgumentException("login was not set");
        } else if (slaves < 1) {
            throw new IllegalArgumentException("there must be at least one slave in cluster");
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
