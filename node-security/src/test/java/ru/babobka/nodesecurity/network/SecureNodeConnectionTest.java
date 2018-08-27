package ru.babobka.nodesecurity.network;

import org.junit.Before;
import org.junit.Test;
import ru.babobka.nodesecurity.data.SecureDataFactory;
import ru.babobka.nodesecurity.data.SecureNodeRequest;
import ru.babobka.nodesecurity.data.SecureNodeResponse;
import ru.babobka.nodesecurity.exception.NodeSecurityException;
import ru.babobka.nodesecurity.service.SRPService;
import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodeserials.NodeResponse;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.network.NodeConnection;
import ru.babobka.nodeutils.time.TimerInvoker;

import java.io.IOException;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

/**
 * Created by 123 on 04.05.2018.
 */
public class SecureNodeConnectionTest {

    private SecureDataFactory secureDataFactory;
    private SRPService SRPService;

    @Before
    public void setUp() {
        secureDataFactory = mock(SecureDataFactory.class);
        SRPService = mock(SRPService.class);
        Container.getInstance().put(container -> {
            container.put(secureDataFactory);
            container.put(SRPService);
            container.put(TimerInvoker.create(10_000));
        });
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullConnection() {
        new SecureNodeConnection(null, new byte[]{1, 2, 3});
    }

    @Test(expected = IllegalArgumentException.class)
    public void testClosedConnection() {
        NodeConnection connection = mock(NodeConnection.class);
        when(connection.isClosed()).thenReturn(true);
        new SecureNodeConnection(connection, new byte[]{1, 2, 3});
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullSecretKey() {
        NodeConnection connection = mock(NodeConnection.class);
        when(connection.isClosed()).thenReturn(false);
        new SecureNodeConnection(connection, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEmptySecretKey() {
        NodeConnection connection = mock(NodeConnection.class);
        when(connection.isClosed()).thenReturn(false);
        new SecureNodeConnection(connection, new byte[]{});
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSendNull() throws IOException {
        NodeConnection connection = mock(NodeConnection.class);
        when(connection.isClosed()).thenReturn(false);
        SecureNodeConnection secureNodeConnection = new SecureNodeConnection(connection, new byte[]{1, 2, 3});
        secureNodeConnection.send((Object) null);
    }

    @Test(expected = NodeSecurityException.class)
    public void testSendInsecureObject() throws IOException {
        NodeConnection connection = mock(NodeConnection.class);
        when(connection.isClosed()).thenReturn(false);
        SecureNodeConnection secureNodeConnection = new SecureNodeConnection(connection, new byte[]{1, 2, 3});
        secureNodeConnection.send("abc");
    }

    @Test
    public void testSendHeartBeatingRequest() throws IOException {
        NodeConnection connection = mock(NodeConnection.class);
        when(connection.isClosed()).thenReturn(false);
        SecureNodeConnection secureNodeConnection = new SecureNodeConnection(connection, new byte[]{1, 2, 3});
        NodeRequest request = NodeRequest.heartBeat();
        secureNodeConnection.send(request);
        verify(connection).send(request);
    }

    @Test
    public void testSendHeartBeatingResponse() throws IOException {
        NodeConnection connection = mock(NodeConnection.class);
        when(connection.isClosed()).thenReturn(false);
        SecureNodeConnection secureNodeConnection = new SecureNodeConnection(connection, new byte[]{1, 2, 3});
        NodeResponse response = NodeResponse.heartBeat();
        secureNodeConnection.send(response);
        verify(connection).send(response);
    }

    @Test
    public void testSendResponse() throws IOException {
        NodeConnection connection = mock(NodeConnection.class);
        when(connection.isClosed()).thenReturn(false);
        byte[] secretKey = {1, 2, 3};
        SecureNodeConnection secureNodeConnection = new SecureNodeConnection(connection, secretKey);
        NodeResponse response = NodeResponse.stopped(UUID.randomUUID());
        SecureNodeResponse secureNodeResponse = mock(SecureNodeResponse.class);
        when(secureDataFactory.create(response, secretKey)).thenReturn(secureNodeResponse);
        secureNodeConnection.send(response);
        verify(connection).send(secureNodeResponse);
    }

    @Test
    public void testSendRequest() throws IOException {
        NodeConnection connection = mock(NodeConnection.class);
        when(connection.isClosed()).thenReturn(false);
        byte[] secretKey = {1, 2, 3};
        SecureNodeConnection secureNodeConnection = new SecureNodeConnection(connection, secretKey);
        NodeRequest request = NodeRequest.stop(UUID.randomUUID());
        SecureNodeRequest secureNodeRequest = mock(SecureNodeRequest.class);
        when(secureDataFactory.create(request, secretKey)).thenReturn(secureNodeRequest);
        secureNodeConnection.send(request);
        verify(connection).send(secureNodeRequest);
    }

    @Test
    public void testReceiveInsecure() throws IOException {
        NodeRequest insecureRequest = mock(NodeRequest.class);
        byte[] secretKey = {1, 2, 3};
        when(SRPService.isSecure(insecureRequest, secretKey)).thenReturn(false);
        NodeConnection connection = mock(NodeConnection.class);
        when(connection.receive()).thenReturn(insecureRequest);
        when(connection.isClosed()).thenReturn(false);
        SecureNodeConnection secureNodeConnection = new SecureNodeConnection(connection, secretKey);
        try {
            secureNodeConnection.receive();
            fail();
        } catch (NodeSecurityException expected) {
            //that's ok
        }
        verify(connection).close();
    }

    @Test
    public void testReceive() throws IOException {
        NodeRequest request = mock(NodeRequest.class);
        byte[] secretKey = {1, 2, 3};
        when(SRPService.isSecure(request, secretKey)).thenReturn(true);
        NodeConnection connection = mock(NodeConnection.class);
        when(connection.receive()).thenReturn(request);
        when(connection.isClosed()).thenReturn(false);
        SecureNodeConnection secureNodeConnection = new SecureNodeConnection(connection, secretKey);
        assertEquals(request, secureNodeConnection.receive());
        verify(connection, never()).close();
    }

}
