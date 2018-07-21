package ru.babobka.nodeutils.network;

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
        timerInvoker.invoke(() -> {
            synchronized (socket) {
                try {
                    streamUtil.sendObject(object, socket);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }, "send(Object object)");

    }

    public void close() {
        synchronized (socket) {
            if (socket.isClosed()) {
                return;
            }
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
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