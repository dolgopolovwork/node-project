package ru.babobka.nodemasterserver.slave;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.babobka.nodebusiness.service.AuthService;
import ru.babobka.nodebusiness.service.MasterAuthService;
import ru.babobka.nodemasterserver.server.MasterServerConfig;
import ru.babobka.nodetask.TaskPool;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.logger.SimpleLogger;
import ru.babobka.nodeutils.network.NodeConnection;
import ru.babobka.nodeutils.network.NodeConnectionFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

import static org.mockito.Mockito.*;

/**
 * Created by 123 on 19.09.2017.
 */
public class IncomingSlaveListenerThreadTest {

    private IncomingSlaveListenerThread incomingSlaveListenerThread;
    private NodeConnectionFactory nodeConnectionFactory;
    private SlaveFactory slaveFactory;
    private SimpleLogger logger;
    private SlavesStorage slavesStorage;
    private AuthService authService;
    private ServerSocket serverSocket;
    private TaskPool taskPool;
    private MasterServerConfig masterServerConfig;

    @Before
    public void setUp() {
        masterServerConfig = mock(MasterServerConfig.class);
        nodeConnectionFactory = mock(NodeConnectionFactory.class);
        slaveFactory = mock(SlaveFactory.class);
        logger = mock(SimpleLogger.class);
        slavesStorage = mock(SlavesStorage.class);
        authService = mock(MasterAuthService.class);
        serverSocket = mock(ServerSocket.class);
        taskPool = mock(TaskPool.class);
        Container.getInstance().put(nodeConnectionFactory);
        Container.getInstance().put(slaveFactory);
        Container.getInstance().put(masterServerConfig);
        Container.getInstance().put(logger);
        Container.getInstance().put(slavesStorage);
        Container.getInstance().put(authService);
        Container.getInstance().put("masterServerTaskPool", taskPool);
        incomingSlaveListenerThread = new IncomingSlaveListenerThread(serverSocket);
    }

    @After
    public void tearDown() {
        Container.getInstance().clear();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorNullServerSocket() {
        new IncomingSlaveListenerThread(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorClosedServerSocket() {
        when(serverSocket.isClosed()).thenReturn(true);
        new IncomingSlaveListenerThread(serverSocket);
    }

    @Test
    public void testOnExit() throws IOException {
        incomingSlaveListenerThread.onExit();
        verify(serverSocket).close();
    }

    @Test
    public void testOnExitException() throws IOException {
        doThrow(new IOException()).when(serverSocket).close();
        incomingSlaveListenerThread.onExit();
        verify(logger).error(any(Exception.class));
    }

    @Test
    public void testOnAwake() throws IOException {
        Socket socket = mock(Socket.class);
        when(serverSocket.accept()).thenReturn(socket);
        NodeConnection connection = mock(NodeConnection.class);
        when(nodeConnectionFactory.create(socket)).thenReturn(connection);
        when(authService.auth(connection)).thenReturn(true);
        Slave slave = mock(Slave.class);
        Set<String> availableTasks = new HashSet<>();
        when(connection.receive()).thenReturn(availableTasks);
        when(slaveFactory.create(availableTasks, connection)).thenReturn(slave);
        when(taskPool.containsAnyOfTask(any(Set.class))).thenReturn(true);
        incomingSlaveListenerThread.onCycle();
        verify(slavesStorage).add(slave);
        verify(slave).start();
    }

    @Test
    public void testOnAwakeAuthFail() throws IOException {
        Socket socket = mock(Socket.class);
        when(serverSocket.accept()).thenReturn(socket);
        NodeConnection connection = mock(NodeConnection.class);
        when(nodeConnectionFactory.create(socket)).thenReturn(connection);
        when(authService.auth(connection)).thenReturn(false);
        incomingSlaveListenerThread.onCycle();
        verify(connection).close();
    }

    @Test
    public void testOnAwakeIOException() throws IOException {
        when(serverSocket.isClosed()).thenReturn(true);
        when(serverSocket.accept()).thenThrow(new IOException());
        incomingSlaveListenerThread.onCycle();
        verify(logger).error(any(IOException.class));
    }

    @Test
    public void testInterrupt() throws IOException {
        incomingSlaveListenerThread.interrupt();
        verify(serverSocket).close();
    }

}
