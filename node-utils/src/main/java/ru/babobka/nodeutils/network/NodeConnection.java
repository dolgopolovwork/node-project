package ru.babobka.nodeutils.network;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetAddress;

/**
 * Created by 123 on 25.04.2018.
 */
public interface NodeConnection extends Closeable {

    void setReadTimeOut(int timeOutMillis) throws IOException;

    <T> T receive() throws IOException;

    void send(Object object) throws IOException;

    void sendIgnoreException(Object object);

    void sendThrowRuntime(Object object);

    void close();

    boolean isClosed();

    InetAddress getAddress();

}
