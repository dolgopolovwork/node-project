package ru.babobka.nodeslaveserver.service;

import org.junit.Before;
import org.junit.Test;
import ru.babobka.nodesecurity.auth.AuthResult;
import ru.babobka.nodesecurity.rsa.RSAConfig;
import ru.babobka.nodesecurity.rsa.RSAConfigFactory;
import ru.babobka.nodesecurity.rsa.RSAPublicKey;
import ru.babobka.nodesecurity.service.RSAService;
import ru.babobka.nodesecurity.service.SRPService;
import ru.babobka.nodeconfigs.slave.SlaveServerConfig;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.logger.NodeLogger;
import ru.babobka.nodeutils.network.NodeConnection;

import java.io.IOException;
import java.math.BigInteger;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Created by 123 on 05.05.2018.
 */
public class SlaveAuthServiceTest {
    private SlaveAuthService slaveAuthService;
    private NodeLogger nodeLogger;
    private SRPService srpService;
    private SlaveServerConfig slaveServerConfig;
    private RSAService rsaService;

    @Before
    public void setUp() {
        slaveServerConfig = new SlaveServerConfig();
        RSAConfig rsaConfig = RSAConfigFactory.create(128);
        slaveServerConfig.setServerPublicKey(rsaConfig.getPublicKey());
        rsaService = mock(RSAService.class);
        nodeLogger = mock(NodeLogger.class);
        srpService = mock(SRPService.class);
        Container.getInstance().put(container -> {
            container.put(nodeLogger);
            container.put(srpService);
            container.put(rsaService);
            container.put(slaveServerConfig);
        });
        slaveAuthService = spy(new SlaveAuthService());
    }

    @Test
    public void testAuthUserNotFound() throws IOException {
        String login = "abc";
        String password = "xyz";
        NodeConnection connection = mock(NodeConnection.class);
        when(connection.receive()).thenReturn(false);
        AuthResult authResult = slaveAuthService.authClient(connection, login, password);
        assertFalse(authResult.isSuccess());
        verify(connection).send(login);
    }

    @Test
    public void testAuthFoundUserFail() throws IOException {
        String login = "abc";
        String password = "xyz";
        NodeConnection connection = mock(NodeConnection.class);
        when(connection.receive()).thenReturn(true);
        doReturn(AuthResult.fail()).when(slaveAuthService).srpUserAuth(connection, login, password);
        AuthResult authResult = slaveAuthService.authClient(connection, login, password);
        assertFalse(authResult.isSuccess());
        verify(connection).send(login);
    }

    @Test
    public void testAuthFoundUserSuccess() throws IOException {
        String login = "abc";
        String password = "xyz";
        NodeConnection connection = mock(NodeConnection.class);
        when(connection.receive()).thenReturn(true);
        doReturn(AuthResult.success(login, new byte[]{1, 2, 3})).when(slaveAuthService).srpUserAuth(connection, login, password);
        AuthResult authResult = slaveAuthService.authClient(connection, login, password);
        assertTrue(authResult.isSuccess());
        assertEquals(authResult.getUserName(), login);
        verify(connection).send(login);
    }

    @Test
    public void testAuthServerFail() throws IOException {
        BigInteger nonce = BigInteger.TEN;
        BigInteger encryptedNonce = BigInteger.ONE;
        RSAPublicKey publicKey = slaveServerConfig.getServerPublicKey();
        when(slaveAuthService.getNonce(publicKey)).thenReturn(nonce);
        when(rsaService.encrypt(nonce, publicKey)).thenReturn(encryptedNonce);
        NodeConnection connection = mock(NodeConnection.class);
        when(connection.receive()).thenReturn(BigInteger.ZERO);
        assertFalse(slaveAuthService.authServer(connection));
        verify(connection).send(encryptedNonce);
        verify(connection).send(false);
    }

    @Test
    public void testAuthServerSuccess() throws IOException {
        BigInteger nonce = BigInteger.TEN;
        BigInteger encryptedNonce = BigInteger.ONE;
        RSAPublicKey publicKey = slaveServerConfig.getServerPublicKey();
        when(slaveAuthService.getNonce(publicKey)).thenReturn(nonce);
        when(rsaService.encrypt(nonce, publicKey)).thenReturn(encryptedNonce);
        NodeConnection connection = mock(NodeConnection.class);
        when(connection.receive()).thenReturn(nonce);
        assertTrue(slaveAuthService.authServer(connection));
        verify(connection).send(encryptedNonce);
        verify(connection).send(true);
    }
}