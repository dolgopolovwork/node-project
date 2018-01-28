package ru.babobka.nodeslaveserver.server;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.babobka.nodeslaveserver.exception.SlaveAuthFailException;
import ru.babobka.nodeslaveserver.service.AuthService;
import ru.babobka.nodetask.TaskPool;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.logger.SimpleLogger;
import ru.babobka.nodeutils.network.NodeConnection;

import java.io.IOException;
import java.util.HashSet;

import static org.mockito.Mockito.*;

/**
 * Created by 123 on 05.09.2017.
 */
public class SlaveServerTest {
    private AuthService authService;
    private SimpleLogger simpleLogger;
    private TaskPool taskPool;

    @Before
    public void setUp() {
        authService = mock(AuthService.class);
        simpleLogger = mock(SimpleLogger.class);
        taskPool = mock(TaskPool.class);
        Container.getInstance().put(authService);
        Container.getInstance().put(simpleLogger);
        Container.getInstance().put("slaveServerTaskPool", taskPool);
    }

    @After
    public void tearDown() {
        Container.getInstance().clear();
    }

    @Test(expected = SlaveAuthFailException.class)
    public void testAuthFail() throws IOException {
        NodeConnection connection = mock(NodeConnection.class);
        when(authService.auth(eq(connection), anyString(), anyString())).thenReturn(false);
        new SlaveServer(connection, "abc", "xyz");
    }

    @Test
    public void testAuthSuccess() throws IOException {
        NodeConnection connection = mock(NodeConnection.class);
        when(taskPool.getTaskNames()).thenReturn(new HashSet<>());
        when(authService.auth(eq(connection), anyString(), anyString())).thenReturn(true);
        when(connection.receive()).thenReturn(true);
        new SlaveServer(connection, "abc", "xyz");
        verify(simpleLogger).info("Auth success");
        verify(connection).send(anySet());
    }

    @Test
    public void testClear() throws IOException {
        NodeConnection connection = mock(NodeConnection.class);
        when(taskPool.getTaskNames()).thenReturn(new HashSet<>());
        when(authService.auth(eq(connection), anyString(), anyString())).thenReturn(true);
        when(connection.receive()).thenReturn(true);
        SlaveServer slaveServer = new SlaveServer(connection, "abc", "xyz");
        slaveServer.clear();
        verify(connection).close();
    }
}
