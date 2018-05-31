package ru.babobka.nodemasterserver.slave;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.babobka.nodemasterserver.key.MasterServerKey;
import ru.babobka.nodemasterserver.listener.OnSlaveExitListener;
import ru.babobka.nodemasterserver.server.config.MasterServerConfig;
import ru.babobka.nodemasterserver.server.config.ModeConfig;
import ru.babobka.nodemasterserver.server.config.TimeoutConfig;
import ru.babobka.nodemasterserver.service.MasterAuthService;
import ru.babobka.nodesecurity.auth.AuthResult;
import ru.babobka.nodesecurity.data.SecureDataFactory;
import ru.babobka.nodesecurity.network.SecureNodeConnection;
import ru.babobka.nodesecurity.service.SRPService;
import ru.babobka.nodetask.TaskPool;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.logger.DummyNodeLogger;
import ru.babobka.nodeutils.logger.NodeLogger;
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
    private NodeLogger nodeLogger;
    private SlavesStorage slavesStorage;
    private MasterAuthService authService;
    private ServerSocket serverSocket;
    private TaskPool taskPool;
    private MasterServerConfig masterServerConfig;
    private Sessions sessions;

    @Before
    public void setUp() {
        sessions = mock(Sessions.class);
        masterServerConfig = mock(MasterServerConfig.class);
        nodeConnectionFactory = mock(NodeConnectionFactory.class);
        slaveFactory = mock(SlaveFactory.class);
        nodeLogger = spy(new DummyNodeLogger());
        slavesStorage = mock(SlavesStorage.class);
        authService = mock(MasterAuthService.class);
        serverSocket = mock(ServerSocket.class);
        taskPool = mock(TaskPool.class);
        Container.getInstance().put(container -> {
            container.put(nodeConnectionFactory);
            container.put(slaveFactory);
            container.put(masterServerConfig);
            container.put(nodeLogger);
            container.put(slavesStorage);
            container.put(authService);
            container.put(MasterServerKey.MASTER_SERVER_TASK_POOL, taskPool);
            container.put(mock(SRPService.class));
            container.put(mock(SecureDataFactory.class));
            container.put(sessions);
        });

        incomingSlaveListenerThread = spy(new IncomingSlaveListenerThread(serverSocket));
    }

    @After
    public void tearDown() {
        Container.getInstance().clear();
    }

    @Test(expected = NullPointerException.class)
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
        verify(nodeLogger).error(any(Exception.class));
    }

    @Test
    public void testOnAwake() throws IOException {
        String userName = "abc";
        Socket socket = mock(Socket.class);
        TimeoutConfig timeoutConfig = new TimeoutConfig();
        when(masterServerConfig.getTimeouts()).thenReturn(timeoutConfig);
        ModeConfig modeConfig = new ModeConfig();
        when(masterServerConfig.getModes()).thenReturn(modeConfig);
        when(serverSocket.accept()).thenReturn(socket);
        NodeConnection connection = mock(NodeConnection.class);
        when(nodeConnectionFactory.create(socket)).thenReturn(connection);
        when(authService.authServer(connection)).thenReturn(true);
        when(authService.authClient(connection)).thenReturn(AuthResult.success(userName, new byte[]{0}));
        Slave slave = mock(Slave.class);
        Set<String> availableTasks = new HashSet<>();
        when(connection.receive()).thenReturn(availableTasks);
        when(slaveFactory.create(eq(availableTasks), any(SecureNodeConnection.class), any(OnSlaveExitListener.class))).thenReturn(slave);
        when(taskPool.containsAnyOfTask(any(Set.class))).thenReturn(true);
        doReturn(true).when(incomingSlaveListenerThread).runNewSlave(eq(availableTasks), eq(userName), any(SecureNodeConnection.class));
        incomingSlaveListenerThread.onCycle();
        verify(incomingSlaveListenerThread, times(2)).success(any(NodeConnection.class));
        verify(sessions, never()).remove(userName);
    }

    @Test
    public void testOnAwakeFailedSlaveRun() throws IOException {
        String userName = "abc";
        Socket socket = mock(Socket.class);
        TimeoutConfig timeoutConfig = new TimeoutConfig();
        when(masterServerConfig.getTimeouts()).thenReturn(timeoutConfig);
        ModeConfig modeConfig = new ModeConfig();
        when(masterServerConfig.getModes()).thenReturn(modeConfig);
        when(serverSocket.accept()).thenReturn(socket);
        NodeConnection connection = mock(NodeConnection.class);
        when(nodeConnectionFactory.create(socket)).thenReturn(connection);
        when(authService.authServer(connection)).thenReturn(true);
        when(authService.authClient(connection)).thenReturn(AuthResult.success(userName, new byte[]{0}));
        Slave slave = mock(Slave.class);
        Set<String> availableTasks = new HashSet<>();
        when(connection.receive()).thenReturn(availableTasks);
        when(slaveFactory.create(eq(availableTasks), any(SecureNodeConnection.class), any(OnSlaveExitListener.class))).thenReturn(slave);
        when(taskPool.containsAnyOfTask(any(Set.class))).thenReturn(true);
        doReturn(false).when(incomingSlaveListenerThread).runNewSlave(eq(availableTasks), eq(userName), any(SecureNodeConnection.class));
        incomingSlaveListenerThread.onCycle();
        verify(incomingSlaveListenerThread).success(any(NodeConnection.class));
        verify(incomingSlaveListenerThread).fail(any(NodeConnection.class));
        verify(sessions).remove(userName);
    }

    @Test
    public void testOnAwakeServerAuthFail() throws IOException {
        Socket socket = mock(Socket.class);
        TimeoutConfig timeoutConfig = new TimeoutConfig();
        when(masterServerConfig.getTimeouts()).thenReturn(timeoutConfig);
        ModeConfig modeConfig = new ModeConfig();
        when(masterServerConfig.getModes()).thenReturn(modeConfig);
        when(serverSocket.accept()).thenReturn(socket);
        NodeConnection connection = mock(NodeConnection.class);
        when(nodeConnectionFactory.create(socket)).thenReturn(connection);
        when(authService.authServer(connection)).thenReturn(false);
        when(authService.authClient(connection)).thenReturn(AuthResult.success("abc", new byte[]{0}));
        Slave slave = mock(Slave.class);
        Set<String> availableTasks = new HashSet<>();
        when(connection.receive()).thenReturn(availableTasks);
        when(slaveFactory.create(eq(availableTasks), any(SecureNodeConnection.class), any(OnSlaveExitListener.class))).thenReturn(slave);
        when(taskPool.containsAnyOfTask(any(Set.class))).thenReturn(true);
        incomingSlaveListenerThread.onCycle();
        verify(connection).close();
        verify(slavesStorage, never()).add(slave);
        verify(slave, never()).start();
    }

    @Test
    public void testOnAwakeAuthFail() throws IOException {
        Socket socket = mock(Socket.class);
        TimeoutConfig timeoutConfig = new TimeoutConfig();
        when(masterServerConfig.getTimeouts()).thenReturn(timeoutConfig);
        when(serverSocket.accept()).thenReturn(socket);
        NodeConnection connection = mock(NodeConnection.class);
        when(nodeConnectionFactory.create(socket)).thenReturn(connection);
        when(authService.authClient(connection)).thenReturn(AuthResult.fail());
        incomingSlaveListenerThread.onCycle();
        verify(connection).close();

    }

    @Test
    public void testOnAwakeSessionCreatedFail() throws IOException {
        String login = "abc";
        Socket socket = mock(Socket.class);
        TimeoutConfig timeoutConfig = new TimeoutConfig();
        when(masterServerConfig.getTimeouts()).thenReturn(timeoutConfig);
        when(serverSocket.accept()).thenReturn(socket);
        NodeConnection connection = mock(NodeConnection.class);
        when(nodeConnectionFactory.create(socket)).thenReturn(connection);
        AuthResult authResult = AuthResult.success(login, new byte[]{1, 2, 3});
        when(authService.authClient(connection)).thenReturn(authResult);
        when(authService.authServer(connection)).thenReturn(true);
        doReturn(false).when(incomingSlaveListenerThread).isAbleToRunNewSlave(authResult);
        Set<String> availableTasks = new HashSet<>();
        when(connection.receive()).thenReturn(availableTasks);
        when(taskPool.containsAnyOfTask(any(Set.class))).thenReturn(true);
        incomingSlaveListenerThread.onCycle();
        verify(connection).send(false);
        verify(connection).close();
        verify(slavesStorage, never()).add(any(Slave.class));
    }

    @Test
    public void testOnAwakeIOException() throws IOException {
        when(serverSocket.isClosed()).thenReturn(true);
        when(serverSocket.accept()).thenThrow(new IOException());
        incomingSlaveListenerThread.onCycle();
        verify(nodeLogger).error(any(IOException.class));
    }

    @Test
    public void testInterrupt() throws IOException {
        incomingSlaveListenerThread.interrupt();
        verify(serverSocket).close();
    }

}
