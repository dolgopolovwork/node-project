package ru.babobka.nodeutils.network;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.babobka.nodeutils.container.ApplicationContainer;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.util.StreamUtil;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

import static org.mockito.Mockito.*;

/**
 * Created by 123 on 01.09.2017.
 */
public class NodeConnectionTest {
    private Socket socket;
    private NodeConnection connection;
    private StreamUtil streamUtil;

    @Before
    public void setUp() {
        socket = mock(Socket.class);
        streamUtil = mock(StreamUtil.class);
        new ApplicationContainer() {
            @Override
            public void contain(Container container) {
                container.put(streamUtil);
            }
        }.contain(Container.getInstance());
        connection = new NodeConnection(socket);
    }

    @After
    public void tearDown() {
        Container.getInstance().clear();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorNullSocket() {
        new NodeConnection(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorClosedSocket() {
        Socket socket = mock(Socket.class);
        when(socket.isClosed()).thenReturn(true);
        new NodeConnection(socket);
    }

    @Test
    public void testReceive() throws IOException {
        connection.receive();
        verify(streamUtil).receiveObject(socket);
    }

    @Test(expected = IOException.class)
    public void testReceiveException() throws IOException {
        when(streamUtil.receiveObject(socket)).thenThrow(new IOException());
        connection.receive();
    }

    @Test
    public void testSend() throws IOException {
        Object object = new Object();
        connection.send(object);
        verify(streamUtil).sendObject(object, socket);
    }

    @Test(expected = IOException.class)
    public void testSendException() throws IOException {
        doThrow(new IOException()).when(streamUtil).sendObject(any(Object.class), eq(socket));
        connection.send(new Object());
    }

    @Test
    public void testClose() throws IOException {
        when(socket.isClosed()).thenReturn(false);
        connection.close();
        verify(socket).close();
    }

    @Test
    public void testCloseException() throws IOException {
        IOException exception = mock(IOException.class);
        doThrow(exception).when(socket).close();
        connection.close();
        verify(exception).printStackTrace();
    }

    @Test
    public void testCloseIsClosed() throws IOException {
        when(socket.isClosed()).thenReturn(true);
        connection.close();
        verify(socket, times(0)).close();
    }

    @Test
    public void testSetReadTimeout() throws IOException {
        int readTimeoutMillis = 10;
        connection.setReadTimeOut(readTimeoutMillis);
        verify(socket).setSoTimeout(readTimeoutMillis);
    }

    @Test(expected = IOException.class)
    public void testSetReadTimeoutExcetion() throws IOException {
        doThrow(new SocketException()).when(socket).setSoTimeout(anyInt());
        connection.setReadTimeOut(10);
    }
}
