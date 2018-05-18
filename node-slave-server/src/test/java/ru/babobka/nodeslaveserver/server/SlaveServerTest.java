package ru.babobka.nodeslaveserver.server;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.babobka.nodesecurity.auth.AuthResult;
import ru.babobka.nodesecurity.data.SecureDataFactory;
import ru.babobka.nodesecurity.service.SecurityService;
import ru.babobka.nodeslaveserver.exception.SlaveAuthFailException;
import ru.babobka.nodeslaveserver.key.SlaveServerKey;
import ru.babobka.nodeslaveserver.service.SlaveAuthService;
import ru.babobka.nodetask.TaskPool;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.logger.SimpleLogger;
import ru.babobka.nodeutils.network.NodeConnection;
import ru.babobka.nodeutils.network.NodeConnectionFactory;

import java.io.IOException;
import java.net.Socket;
import java.util.HashSet;

import static org.mockito.Mockito.*;

/**
 * Created by 123 on 05.09.2017.
 */
public class SlaveServerTest {
    private SlaveAuthService authService;
    private SimpleLogger simpleLogger;
    private TaskPool taskPool;
    private NodeConnectionFactory nodeConnectionFactory;

    @Before
    public void setUp() {
        nodeConnectionFactory = mock(NodeConnectionFactory.class);
        authService = mock(SlaveAuthService.class);
        simpleLogger = mock(SimpleLogger.class);
        taskPool = mock(TaskPool.class);
        Container.getInstance().put(container -> {
            container.put(authService);
            container.put(simpleLogger);
            container.put(nodeConnectionFactory);
            container.put(SlaveServerKey.SLAVE_SERVER_TASK_POOL, taskPool);
            container.put(mock(SecurityService.class));
            container.put(mock(SecureDataFactory.class));
        });

    }

    @After
    public void tearDown() {
        Container.getInstance().clear();
    }

    @Test(expected = SlaveAuthFailException.class)
    public void testAuthFail() throws IOException {
        NodeConnection connection = mock(NodeConnection.class);
        when(authService.auth(eq(connection), anyString(), anyString())).thenReturn(AuthResult.fail());
        when(nodeConnectionFactory.create(any(Socket.class))).thenReturn(connection);
        new SlaveServer(mock(Socket.class), "abc", "xyz");
    }

    @Test
    public void testAuthSuccess() throws IOException {
        NodeConnection connection = mock(NodeConnection.class);
        when(taskPool.getTaskNames()).thenReturn(new HashSet<>());
        when(authService.auth(eq(connection), anyString(), anyString())).thenReturn(AuthResult.success("abc", new byte[]{0}));
        when(connection.receive()).thenReturn(true);
        when(nodeConnectionFactory.create(any(Socket.class))).thenReturn(connection);
        new SlaveServer(mock(Socket.class), "abc", "xyz");
        verify(simpleLogger).info("auth success");
        verify(connection).send(anySet());
    }

    @Test(expected = SlaveAuthFailException.class)
    public void testFailedSessions() throws IOException {
        NodeConnection connection = mock(NodeConnection.class);
        when(taskPool.getTaskNames()).thenReturn(new HashSet<>());
        when(authService.auth(eq(connection), anyString(), anyString())).thenReturn(AuthResult.success("abc", new byte[]{0}));
        when(connection.receive()).thenReturn(true, false);
        when(nodeConnectionFactory.create(any(Socket.class))).thenReturn(connection);
        new SlaveServer(mock(Socket.class), "abc", "xyz");
        verify(simpleLogger).info("auth success");
        verify(connection).send(anySet());
    }

    @Test
    public void testClear() throws IOException {
        NodeConnection connection = mock(NodeConnection.class);
        when(taskPool.getTaskNames()).thenReturn(new HashSet<>());
        when(authService.auth(eq(connection), anyString(), anyString())).thenReturn(AuthResult.success("abc", new byte[]{0}));
        when(connection.receive()).thenReturn(true);
        when(nodeConnectionFactory.create(any(Socket.class))).thenReturn(connection);
        SlaveServer slaveServer = new SlaveServer(mock(Socket.class), "abc", "xyz");
        slaveServer.clear();
        verify(connection).close();
    }
}
