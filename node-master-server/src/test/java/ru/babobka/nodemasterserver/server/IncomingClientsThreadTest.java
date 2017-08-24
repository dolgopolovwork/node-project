package ru.babobka.nodemasterserver.server;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.babobka.nodemasterserver.exception.TaskExecutionException;
import ru.babobka.nodemasterserver.service.TaskService;
import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodeserials.enumerations.RequestStatus;
import ru.babobka.nodetask.model.StoppedTasks;
import ru.babobka.nodeutils.container.ApplicationContainer;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.logger.SimpleLogger;
import ru.babobka.nodeutils.network.NodeConnection;

import java.net.ServerSocket;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

/**
 * Created by 123 on 04.11.2017.
 */
public class IncomingClientsThreadTest {

    private MasterServerConfig config;
    private SimpleLogger logger;
    private ExecutorService executorService;
    private StoppedTasks stoppedTasks;
    private TaskService taskService;
    private IncomingClientsThread incomingClientsThread;

    @Before
    public void setUp() {
        config = mock(MasterServerConfig.class);
        logger = mock(SimpleLogger.class);
        executorService = mock(ExecutorService.class);
        stoppedTasks = mock(StoppedTasks.class);
        taskService = mock(TaskService.class);
        new ApplicationContainer() {
            @Override
            public void contain(Container container) {
                container.put("clientsThreadPool", executorService);
                container.put(logger);
                container.put(config);
                container.put(stoppedTasks);
                container.put(taskService);
            }
        }.contain(Container.getInstance());
        incomingClientsThread = spy(new IncomingClientsThread());
    }

    @After
    public void tearDown() {
        Container.getInstance().clear();
    }

    @Test
    public void testInterrupt() {
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
    public void testHandleRequestStop() throws TaskExecutionException {
        NodeRequest request = mock(NodeRequest.class);
        UUID taskId = UUID.randomUUID();
        when(request.getTaskId()).thenReturn(taskId);
        when(request.getRequestStatus()).thenReturn(RequestStatus.STOP);
        NodeConnection connection = mock(NodeConnection.class);
        incomingClientsThread.handleRequest(connection, request);
        verify(stoppedTasks).add(request);
        verify(taskService).cancelTask(taskId);
    }

    @Test
    public void testHandleRequestStopException() throws TaskExecutionException {
        NodeRequest request = mock(NodeRequest.class);
        UUID taskId = UUID.randomUUID();
        when(request.getTaskId()).thenReturn(taskId);
        when(request.getRequestStatus()).thenReturn(RequestStatus.STOP);
        NodeConnection connection = mock(NodeConnection.class);
        doThrow(new TaskExecutionException()).when(taskService).cancelTask(taskId);
        incomingClientsThread.handleRequest(connection, request);
        verify(logger).error(any(Exception.class));
    }

}
