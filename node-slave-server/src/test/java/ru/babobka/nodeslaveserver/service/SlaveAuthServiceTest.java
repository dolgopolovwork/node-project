package ru.babobka.nodeslaveserver.service;

import org.junit.Before;
import org.junit.Test;
import ru.babobka.nodesecurity.auth.AuthResult;
import ru.babobka.nodesecurity.service.SecurityService;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.logger.SimpleLogger;
import ru.babobka.nodeutils.network.NodeConnection;

import java.io.IOException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Created by 123 on 05.05.2018.
 */
public class SlaveAuthServiceTest {
    private SlaveAuthService slaveAuthService;
    private SimpleLogger simpleLogger;
    private SecurityService securityService;

    @Before
    public void setUp() {
        simpleLogger = mock(SimpleLogger.class);
        securityService = mock(SecurityService.class);
        Container.getInstance().put(container -> {
            container.put(simpleLogger);
            container.put(securityService);
        });
        slaveAuthService = spy(new SlaveAuthService());
    }

    @Test
    public void testAuthUserNotFound() throws IOException {
        String login = "abc";
        String password = "xyz";
        NodeConnection connection = mock(NodeConnection.class);
        when(connection.receive()).thenReturn(false);
        AuthResult authResult = slaveAuthService.auth(connection, login, password);
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
        AuthResult authResult = slaveAuthService.auth(connection, login, password);
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
        AuthResult authResult = slaveAuthService.auth(connection, login, password);
        assertTrue(authResult.isSuccess());
        assertEquals(authResult.getUserName(), login);
        verify(connection).send(login);
    }
}
