package ru.babobka.nodemasterserver.service;

import org.junit.Before;
import org.junit.Test;
import ru.babobka.nodebusiness.model.User;
import ru.babobka.nodebusiness.service.NodeUsersService;
import ru.babobka.nodemasterserver.server.config.MasterServerConfig;
import ru.babobka.nodemasterserver.server.config.SecurityConfig;
import ru.babobka.nodemasterserver.slave.Sessions;
import ru.babobka.nodesecurity.auth.AuthResult;
import ru.babobka.nodesecurity.config.SrpConfig;
import ru.babobka.nodesecurity.rsa.RSAConfig;
import ru.babobka.nodesecurity.rsa.RSAConfigFactory;
import ru.babobka.nodesecurity.service.RSAService;
import ru.babobka.nodesecurity.service.SRPService;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.logger.NodeLogger;
import ru.babobka.nodeutils.network.NodeConnection;

import java.io.IOException;
import java.math.BigInteger;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Created by 123 on 04.05.2018.
 */
public class MasterAuthServiceTest {
    private NodeUsersService nodeUsersService;
    private NodeLogger nodeLogger;
    private SrpConfig srpConfig;
    private SRPService SRPService;
    private MasterAuthService masterAuthService;
    private Sessions sessions;
    private MasterServerConfig masterServerConfig;
    private RSAConfig rsaConfig;
    private RSAService rsaService;

    @Before
    public void setUp() {
        rsaService = mock(RSAService.class);
        masterServerConfig = new MasterServerConfig();
        rsaConfig = RSAConfigFactory.create(128);
        SecurityConfig securityConfig = new SecurityConfig();
        securityConfig.setRsaConfig(rsaConfig);
        masterServerConfig.setSecurity(securityConfig);
        nodeUsersService = mock(NodeUsersService.class);
        nodeLogger = mock(NodeLogger.class);
        srpConfig = mock(SrpConfig.class);
        sessions = mock(Sessions.class);
        SRPService = mock(SRPService.class);
        Container.getInstance().put(container -> {
            container.put(nodeUsersService);
            container.put(masterServerConfig);
            container.put(nodeLogger);
            container.put(srpConfig);
            container.put(SRPService);
            container.put(sessions);
            container.put(rsaService);
        });
        masterAuthService = spy(new MasterAuthService());
    }

    @Test
    public void testAuthNoUserFound() throws IOException {
        String login = "abc";
        NodeConnection connection = mock(NodeConnection.class);
        when(connection.receive()).thenReturn(login);
        when(nodeUsersService.get(login)).thenReturn(null);
        AuthResult authResult = masterAuthService.authClient(connection);
        assertFalse(authResult.isSuccess());
        verify(masterAuthService, never()).srpHostAuth(any(NodeConnection.class), any(User.class));
    }

    @Test
    public void testAuthSessionIsTaken() throws IOException {
        String login = "abc";
        NodeConnection connection = mock(NodeConnection.class);
        when(connection.receive()).thenReturn(login);
        when(sessions.contains(login)).thenReturn(true);
        AuthResult authResult = masterAuthService.authClient(connection);
        assertFalse(authResult.isSuccess());
        verify(nodeUsersService, never()).get(login);
        verify(masterAuthService, never()).srpHostAuth(any(NodeConnection.class), any(User.class));
    }

    @Test
    public void testAuthUserFoundFailed() throws IOException {
        String login = "abc";
        User user = mock(User.class);
        NodeConnection connection = mock(NodeConnection.class);
        when(connection.receive()).thenReturn(login);
        when(nodeUsersService.get(login)).thenReturn(user);
        doReturn(AuthResult.fail()).when(masterAuthService).srpHostAuth(connection, user);
        AuthResult authResult = masterAuthService.authClient(connection);
        assertFalse(authResult.isSuccess());
        verify(masterAuthService).srpHostAuth(connection, user);
    }

    @Test
    public void testAuthUserFoundSuccess() throws IOException {
        String login = "abc";
        User user = new User();
        user.setName("abc");
        NodeConnection connection = mock(NodeConnection.class);
        when(connection.receive()).thenReturn(login);
        when(nodeUsersService.get(login)).thenReturn(user);
        doReturn(AuthResult.success(user.getName(), new byte[]{1, 2, 3})).when(masterAuthService).srpHostAuth(connection, user);
        AuthResult authResult = masterAuthService.authClient(connection);
        assertTrue(authResult.isSuccess());
        assertEquals(authResult.getUserName(), user.getName());
        verify(masterAuthService).srpHostAuth(connection, user);
    }

    @Test
    public void testAuthServerFail() throws IOException {
        BigInteger encryptedNonce = BigInteger.TEN;
        BigInteger decryptedNonce = BigInteger.ONE;
        NodeConnection connection = mock(NodeConnection.class);
        when(connection.receive()).thenReturn(encryptedNonce, false);
        when(rsaService.decrypt(encryptedNonce, rsaConfig.getPrivateKey())).thenReturn(decryptedNonce);
        assertFalse(masterAuthService.authServer(connection));
        verify(connection).send(decryptedNonce);
    }

    @Test
    public void testAuthServerSuccess() throws IOException {
        BigInteger encryptedNonce = BigInteger.TEN;
        BigInteger decryptedNonce = BigInteger.ONE;
        NodeConnection connection = mock(NodeConnection.class);
        when(connection.receive()).thenReturn(encryptedNonce, true);
        when(rsaService.decrypt(encryptedNonce, rsaConfig.getPrivateKey())).thenReturn(decryptedNonce);
        assertTrue(masterAuthService.authServer(connection));
        verify(connection).send(decryptedNonce);
    }
}
