package ru.babobka.nodemasterserver.client;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.babobka.nodemasterserver.server.MasterServerConfig;
import ru.babobka.nodemasterserver.service.TaskService;
import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodeserials.enumerations.RequestStatus;
import ru.babobka.nodeutils.container.ApplicationContainer;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.logger.SimpleLogger;
import ru.babobka.nodeutils.network.NodeConnection;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

/**
 * Created by 123 on 04.11.2017.
 */
public class IncomingClientListenerThreadTest {

    private MasterServerConfig config;
    private SimpleLogger logger;
    private ExecutorService executorService;
    private TaskService taskService;
    private IncomingClientListenerThread incomingClientsThread;
    private ServerSocket serverSocket;

    @Before
    public void setUp() {
        serverSocket = mock(ServerSocket.class);
        config = mock(MasterServerConfig.class);
        logger = mock(SimpleLogger.class);
        executorService = mock(ExecutorService.class);
        taskService = mock(TaskService.class);
        new ApplicationContainer() {
            @Override
            public void contain(Container container) {
                container.put("clientsThreadPool", executorService);
                container.put(logger);
                container.put(config);
                container.put(taskService);
            }
        }.contain(Container.getInstance());
        incomingClientsThread = spy(new IncomingClientListenerThread(serverSocket));
    }

    @After
    public void tearDown() {
        Container.getInstance().clear();
    }


    @Test(expected = IllegalArgumentException.class)
    public void testNullArg() {
        new IncomingClientListenerThread(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testClosedSocketArg() {
        when(serverSocket.isClosed()).thenReturn(true);
        new IncomingClientListenerThread(serverSocket);
    }

    @Test
    public void testInterrupt() throws IOException {
        incomingClientsThread.interrupt();
        verify(executorService).shutdownNow();
        verify(serverSocket).close();
    }

    @Test
    public void testInterruptCloseException() throws IOException {
        doThrow(new IOException()).when(serverSocket).close();
        incomingClientsThread.interrupt();
        verify(executorService).shutdownNow();
    }


    @Test(expected = IllegalArgumentException.class)
    public void testProcessConnectionNullConnection() {
        incomingClientsThread.processConnection(null);
    }

    @Test(expected = IllegalStateException.class)
    public void testProcessConnectionClosedConnection() {
        ServerSocket serverSocket = mock(ServerSocket.class);
        when(serverSocket.isClosed()).thenReturn(true);
        incomingClientsThread.processConnection(serverSocket);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIsDoneNullServerSocket() {
        incomingClientsThread.isDone(null);
    }

    @Test
    public void testIsDone() {
        ServerSocket serverSocket = mock(ServerSocket.class);
        when(serverSocket.isClosed()).thenReturn(true);
        assertTrue(incomingClientsThread.isDone(serverSocket));
    }

    @Test
    public void testIsNotDone() {
        ServerSocket serverSocket = mock(ServerSocket.class);
        when(serverSocket.isClosed()).thenReturn(false);
        assertFalse(incomingClientsThread.isDone(serverSocket));
    }

    @Test
    public void testHandleRequestRace() {
        NodeConnection connection = mock(NodeConnection.class);
        NodeRequest request = mock(NodeRequest.class);
        when(request.getRequestStatus()).thenReturn(RequestStatus.RACE);
        Client client = mock(Client.class);
        doReturn(client).when(incomingClientsThread).createClientExecutor(connection, request);
        incomingClientsThread.handleRequest(connection, request);
        verify(executorService).submit(any(Client.class));
    }

    @Test
    public void testHandleRequestNormal() {
        NodeConnection connection = mock(NodeConnection.class);
        NodeRequest request = mock(NodeRequest.class);
        when(request.getRequestStatus()).thenReturn(RequestStatus.NORMAL);
        Client client = mock(Client.class);
        doReturn(client).when(incomingClientsThread).createClientExecutor(connection, request);
        incomingClientsThread.handleRequest(connection, request);
        verify(executorService).submit(any(Client.class));
    }

    @Test
    public void testHandleRequestHeartBeat() {
        NodeConnection connection = mock(NodeConnection.class);
        NodeRequest request = mock(NodeRequest.class);
        when(request.getRequestStatus()).thenReturn(RequestStatus.HEART_BEAT);
        Client client = mock(Client.class);
        doReturn(client).when(incomingClientsThread).createClientExecutor(connection, request);
        incomingClientsThread.handleRequest(connection, request);
        verify(executorService, never()).submit(any(Client.class));
    }


    @Test
    public void testHandleRequestStop() {
        NodeConnection connection = mock(NodeConnection.class);
        NodeRequest request = mock(NodeRequest.class);
        when(request.getRequestStatus()).thenReturn(RequestStatus.STOP);
        Client client = mock(Client.class);
        doReturn(client).when(incomingClientsThread).createClientExecutor(connection, request);
        incomingClientsThread.handleRequest(connection, request);
        verify(executorService, never()).submit(any(Client.class));
    }


    @Test
    public void testProcessConnection() throws IOException {
        NodeConnection connection = mock(NodeConnection.class);
        doReturn(connection).when(incomingClientsThread).createNodeConnection(serverSocket);
        NodeRequest request = mock(NodeRequest.class);
        when(connection.receive()).thenReturn(request);
        doNothing().when(incomingClientsThread).handleRequest(connection, request);
        incomingClientsThread.processConnection(serverSocket);
        verify(incomingClientsThread).handleRequest(connection, request);
    }

    @Test
    public void testRunFinally() {
        doReturn(true).when(incomingClientsThread).isDone(serverSocket);
        incomingClientsThread.run();
        verify(incomingClientsThread).onExit();
    }

}