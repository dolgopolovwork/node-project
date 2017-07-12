package ru.babobka.nodeutils.network;

import ru.babobka.nodeutils.util.StreamUtil;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

/**
 * Created by 123 on 12.07.2017.
 */
public class NodeConnection {

    private final Socket socket;

    public void setReadTimeOut(int timeOutMillis) throws IOException {
        try {
            this.socket.setSoTimeout(timeOutMillis);
        } catch (SocketException e) {
            throw new IOException(e);
        }
    }

    public NodeConnection(Socket socket) {
        this.socket = socket;
    }

    public <T> T receive() throws IOException {
        return StreamUtil.receiveObject(socket);
    }

    public void send(Object object) throws IOException {
        StreamUtil.sendObject(object, socket);
    }

    public void close() {
        if (!socket.isClosed()) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public int getServerPort() {
        return socket.getPort();
    }

    public int getLocalPort() {
        return socket.getLocalPort();
    }

    public String getHostName() {
        return socket.getInetAddress().getCanonicalHostName();
    }

    @Override
    public String toString() {
        return socket.toString();
    }
}
