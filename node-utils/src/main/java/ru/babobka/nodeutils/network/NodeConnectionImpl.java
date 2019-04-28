package ru.babobka.nodeutils.network;

import org.apache.log4j.Logger;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.time.TimerInvoker;
import ru.babobka.nodeutils.util.StreamUtil;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

/**
 * Created by 123 on 12.07.2017.
 */
public class NodeConnectionImpl implements NodeConnection {

    private static final Logger logger = Logger.getLogger(NodeConnectionImpl.class);
    private final Socket socket;
    private final StreamUtil streamUtil = Container.getInstance().get(StreamUtil.class);
    private final TimerInvoker timerInvoker = Container.getInstance().get(TimerInvoker.class);

    public NodeConnectionImpl(Socket socket) {
        if (socket == null) {
            throw new IllegalArgumentException("socket is null");
        } else if (socket.isClosed()) {
            throw new IllegalArgumentException("socket is closed");
        }
        this.socket = socket;
    }

    public void setReadTimeOut(int timeOutMillis) throws IOException {
        try {
            synchronized (socket) {
                socket.setSoTimeout(timeOutMillis);
            }
        } catch (SocketException e) {
            throw new IOException(e);
        }
    }

    public <T> T receive() throws IOException {
        return streamUtil.receiveObject(socket);
    }

    public void send(Object object) throws IOException {
        try {
            timerInvoker.invoke(() -> {
                synchronized (socket) {
                    streamUtil.sendObject(object, socket);
                }
            }, "send(Object object)");
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            //Not going to happen
            throw new IOException(e);
        }
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

    public void close() {
        synchronized (socket) {
            if (socket.isClosed()) {
                return;
            }
            try {
                socket.close();
            } catch (IOException e) {
                logger.error("exception thrown", e);
            }
        }
    }

    public boolean isClosed() {
        synchronized (socket) {
            return socket.isClosed();
        }
    }

    @Override
    public String toString() {
        synchronized (socket) {
            return socket.toString();
        }
    }
}