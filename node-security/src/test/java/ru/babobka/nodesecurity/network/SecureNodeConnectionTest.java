package ru.babobka.nodesecurity.network;

import org.junit.Before;
import org.junit.Test;
import ru.babobka.nodesecurity.checker.SecureDataChecker;
import ru.babobka.nodesecurity.data.SecureNodeRequest;
import ru.babobka.nodesecurity.exception.NodeSecurityException;
import ru.babobka.nodesecurity.sign.DigitalSigner;
import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodeserials.NodeResponse;
import ru.babobka.nodeserials.data.Data;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.network.NodeConnection;

import java.io.IOException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

public class SecureNodeConnectionTest {

    private DigitalSigner digitalSigner;
    private SecureDataChecker secureDataChecker;

    @Before
    public void setUp() {
        digitalSigner = mock(DigitalSigner.class);
        secureDataChecker = mock(SecureDataChecker.class);
        Container.getInstance().put(container -> {
            container.put(digitalSigner);
            container.put(secureDataChecker);
        });
    }

    @Test(expected = IllegalArgumentException.class)
    public void testClosedConnection() {
        NodeConnection connection = mock(NodeConnection.class);
        when(connection.isClosed()).thenReturn(true);
        new SecureNodeConnection(connection, mock(PrivateKey.class), mock(PublicKey.class));
    }

    @Test(expected = RuntimeException.class)
    public void testSendThrowRuntime() throws IOException {
        NodeConnection connection = mock(NodeConnection.class);
        when(connection.isClosed()).thenReturn(false);
        SecureNodeConnection secureNodeConnection = spy(new SecureNodeConnection(connection, mock(PrivateKey.class), mock(PublicKey.class)));
        doThrow(new IOException()).when(secureNodeConnection).send((Object) any());
        secureNodeConnection.sendThrowRuntime(123);
    }

    @Test
    public void testSendIgnoreException() throws IOException {
        NodeConnection connection = mock(NodeConnection.class);
        when(connection.isClosed()).thenReturn(false);
        SecureNodeConnection secureNodeConnection = spy(new SecureNodeConnection(connection, mock(PrivateKey.class), mock(PublicKey.class)));
        doThrow(new IOException()).when(secureNodeConnection).send((Object) any());
        secureNodeConnection.sendIgnoreException(123);
    }

    @Test
    public void testClose() {
        NodeConnection connection = mock(NodeConnection.class);
        when(connection.isClosed()).thenReturn(false);
        SecureNodeConnection secureNodeConnection = new SecureNodeConnection(connection, mock(PrivateKey.class), mock(PublicKey.class));
        secureNodeConnection.close();
        verify(connection).close();

    }

    @Test
    public void testSetReadTimeOut() throws IOException {
        NodeConnection connection = mock(NodeConnection.class);
        when(connection.isClosed()).thenReturn(false);
        SecureNodeConnection secureNodeConnection =
                new SecureNodeConnection(connection, mock(PrivateKey.class), mock(PublicKey.class));
        int timeout = 1;
        secureNodeConnection.setReadTimeOut(timeout);
        verify(connection).setReadTimeOut(timeout);
    }

    @Test(expected = NodeSecurityException.class)
    public void testSendBadObject() throws IOException {
        NodeConnection connection = mock(NodeConnection.class);
        when(connection.isClosed()).thenReturn(false);
        new SecureNodeConnection(connection, mock(PrivateKey.class), mock(PublicKey.class)).send(123);
    }

    @Test
    public void testSendNodeResponse() throws IOException {
        NodeConnection connection = mock(NodeConnection.class);
        when(connection.isClosed()).thenReturn(false);
        SecureNodeConnection secureNodeConnection = spy(new SecureNodeConnection(connection, mock(PrivateKey.class), mock(PublicKey.class)));
        NodeResponse response = mock(NodeResponse.class);
        doNothing().when(secureNodeConnection).send(response);
        Object objectToSend = response;
        secureNodeConnection.send(objectToSend);
        verify(secureNodeConnection).send(response);
    }

    @Test
    public void testSendNodeRequest() throws IOException {
        NodeConnection connection = mock(NodeConnection.class);
        when(connection.isClosed()).thenReturn(false);
        SecureNodeConnection secureNodeConnection = spy(new SecureNodeConnection(connection, mock(PrivateKey.class), mock(PublicKey.class)));
        NodeRequest request = mock(NodeRequest.class);
        doNothing().when(secureNodeConnection).send(request);
        Object objectToSend = request;
        secureNodeConnection.send(objectToSend);
        verify(secureNodeConnection).send(request);
    }

    @Test
    public void testSendHeartBeatRequest() throws IOException {
        NodeConnection connection = mock(NodeConnection.class);
        when(connection.isClosed()).thenReturn(false);
        SecureNodeConnection secureNodeConnection = spy(new SecureNodeConnection(connection, mock(PrivateKey.class), mock(PublicKey.class)));
        NodeRequest request = NodeRequest.heartBeat();
        secureNodeConnection.send(request);
        verify(connection).send(request);
        verify(digitalSigner, never()).sign(eq(request), any());
    }

    @Test
    public void testSendNodeRequestDirectly() throws IOException {
        NodeConnection connection = mock(NodeConnection.class);
        when(connection.isClosed()).thenReturn(false);
        PrivateKey privateKey = mock(PrivateKey.class);
        SecureNodeConnection secureNodeConnection = spy(new SecureNodeConnection(connection, privateKey, mock(PublicKey.class)));
        NodeRequest request = NodeRequest.regular(UUID.randomUUID(), "test", new Data());
        secureNodeConnection.send(request);
        verify(connection).send(any());
        verify(digitalSigner).sign(request, privateKey);
    }

    @Test
    public void testSendHeartBeatResponse() throws IOException {
        NodeConnection connection = mock(NodeConnection.class);
        when(connection.isClosed()).thenReturn(false);
        SecureNodeConnection secureNodeConnection = spy(new SecureNodeConnection(connection, mock(PrivateKey.class), mock(PublicKey.class)));
        NodeResponse response = NodeResponse.heartBeat();
        secureNodeConnection.send(response);
        verify(connection).send(response);
        verify(digitalSigner, never()).sign(eq(response), any());
    }

    @Test
    public void testSendNodeResponseDirectly() throws IOException {
        NodeConnection connection = mock(NodeConnection.class);
        when(connection.isClosed()).thenReturn(false);
        PrivateKey privateKey = mock(PrivateKey.class);
        SecureNodeConnection secureNodeConnection = spy(new SecureNodeConnection(connection, privateKey, mock(PublicKey.class)));
        NodeResponse response = NodeResponse.dummy(UUID.randomUUID());
        secureNodeConnection.send(response);
        verify(connection).send(any());
        verify(digitalSigner).sign(response, privateKey);
    }

    @Test
    public void testReceiveNoCloseInsecureObject() throws IOException {
        NodeConnection connection = mock(NodeConnection.class);
        when(connection.isClosed()).thenReturn(false);
        PublicKey publicKey = mock(PublicKey.class);
        SecureNodeConnection secureNodeConnection = new SecureNodeConnection(connection, mock(PrivateKey.class), publicKey);
        SecureNodeRequest receivedRequest = mock(SecureNodeRequest.class);
        when(connection.receive()).thenReturn(receivedRequest);
        when(secureDataChecker.isSecure(receivedRequest, publicKey)).thenReturn(false);
        try {
            secureNodeConnection.receiveNoClose();
            fail();
        } catch (NodeSecurityException ignored) {
            verify(connection, never()).close();
        }
    }

    @Test
    public void testReceiveNoCloseObject() throws IOException {
        NodeConnection connection = mock(NodeConnection.class);
        when(connection.isClosed()).thenReturn(false);
        PublicKey publicKey = mock(PublicKey.class);
        SecureNodeConnection secureNodeConnection = new SecureNodeConnection(connection, mock(PrivateKey.class), publicKey);
        SecureNodeRequest receivedRequest = mock(SecureNodeRequest.class);
        when(connection.receive()).thenReturn(receivedRequest);
        when(secureDataChecker.isSecure(receivedRequest, publicKey)).thenReturn(true);
        assertEquals(receivedRequest, secureNodeConnection.receiveNoClose());
        verify(connection, never()).close();
    }

    @Test
    public void testReceiveBadRequest() throws IOException {
        NodeConnection connection = mock(NodeConnection.class);
        when(connection.isClosed()).thenReturn(false);
        SecureNodeConnection secureNodeConnection = spy(new SecureNodeConnection(connection, mock(PrivateKey.class), mock(PublicKey.class)));
        doThrow(new NodeSecurityException("")).when(secureNodeConnection).receiveNoClose();
        try {
            secureNodeConnection.receive();
            fail();
        } catch (NodeSecurityException ingored) {
            verify(connection).close();
        }
    }

    @Test
    public void testReceive() throws IOException {
        NodeConnection connection = mock(NodeConnection.class);
        when(connection.isClosed()).thenReturn(false);
        SecureNodeConnection secureNodeConnection = spy(new SecureNodeConnection(connection, mock(PrivateKey.class), mock(PublicKey.class)));
        NodeRequest request = NodeRequest.regular(UUID.randomUUID(), "test", new Data());
        doReturn(request).when(secureNodeConnection).receiveNoClose();
        assertEquals(request, secureNodeConnection.receive());
        verify(connection, never()).close();
    }
}
