package ru.babobka.nodetester.network;

import lombok.NonNull;
import org.apache.log4j.Logger;
import ru.babobka.nodeutils.func.Callback;
import ru.babobka.nodeutils.network.NodeConnection;

import java.io.IOException;

public class TestableNodeConnection implements NodeConnection {

    private static final Logger logger = Logger.getLogger(TestableNodeConnection.class);

    private final Callback<Object> receiveCallback;

    public TestableNodeConnection(@NonNull Callback<Object> receiveCallback) {
        this.receiveCallback = receiveCallback;
    }

    @Override
    public void setReadTimeOut(int timeOutMillis) throws IOException {

    }

    @Override
    public <T> T receive() throws IOException {
        return null;
    }

    @Override
    public void send(Object object) throws IOException {
        receiveCallback.callback(object);
    }

    @Override
    public void sendIgnoreException(Object object) {
        try {
            send(object);
        } catch (Exception e) {
            logger.error("cannot send", e);
        }
    }

    @Override
    public void sendThrowRuntime(Object object) {
        try {
            send(object);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() {

    }

    @Override
    public boolean isClosed() {
        return false;
    }
}
