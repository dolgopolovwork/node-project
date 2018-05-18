package ru.babobka.nodemasterserver.service;

import org.junit.Before;
import org.junit.Test;
import ru.babobka.nodebusiness.model.User;
import ru.babobka.nodebusiness.service.NodeUsersService;
import ru.babobka.nodemasterserver.slave.Sessions;
import ru.babobka.nodesecurity.auth.AuthResult;
import ru.babobka.nodesecurity.config.SrpConfig;
import ru.babobka.nodesecurity.service.SecurityService;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.logger.SimpleLogger;
import ru.babobka.nodeutils.network.NodeConnection;

import java.io.IOException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Created by 123 on 04.05.2018.
 */
public class MasterAuthServiceTest {
    private NodeUsersService nodeUsersService;
    private SimpleLogger simpleLogger;
    private SrpConfig srpConfig;
    private SecurityService securityService;
    private MasterAuthService masterAuthService;
    private Sessions sessions;

    @Before
    public void setUp() {
        nodeUsersService = mock(NodeUsersService.class);
        simpleLogger = mock(SimpleLogger.class);
        srpConfig = mock(SrpConfig.class);
        sessions = mock(Sessions.class);
        securityService = mock(SecurityService.class);
        Container.getInstance().put(container -> {
            container.put(nodeUsersService);
            container.put(simpleLogger);
            container.put(srpConfig);
            container.put(securityService);
            container.put(sessions);
        });
        masterAuthService = spy(new MasterAuthService());
    }

    @Test
    public void testAuthNoUserFound() throws IOException {
        String login = "abc";
        NodeConnection connection = mock(NodeConnection.class);
        when(connection.receive()).thenReturn(login);
        when(nodeUsersService.get(login)).thenReturn(null);
        AuthResult authResult = masterAuthService.auth(connection);
        assertFalse(authResult.isSuccess());
        verify(masterAuthService, never()).srpHostAuth(any(NodeConnection.class), any(User.class));
    }

    @Test
    public void testAuthSessionIsTaken() throws IOException {
        String login = "abc";
        NodeConnection connection = mock(NodeConnection.class);
        when(connection.receive()).thenReturn(login);
        when(sessions.contains(login)).thenReturn(true);
        AuthResult authResult = masterAuthService.auth(connection);
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
        AuthResult authResult = masterAuthService.auth(connection);
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
        AuthResult authResult = masterAuthService.auth(connection);
        assertTrue(authResult.isSuccess());
        assertEquals(authResult.getUserName(), user.getName());
        verify(masterAuthService).srpHostAuth(connection, user);
    }
}
