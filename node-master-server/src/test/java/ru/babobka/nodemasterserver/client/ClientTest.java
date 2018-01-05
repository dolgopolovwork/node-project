package ru.babobka.nodemasterserver.client;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.babobka.nodemasterserver.exception.TaskExecutionException;
import ru.babobka.nodemasterserver.server.MasterServerConfig;
import ru.babobka.nodemasterserver.service.TaskService;
import ru.babobka.nodemasterserver.task.TaskExecutionResult;
import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodeutils.container.ApplicationContainer;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.logger.SimpleLogger;
import ru.babobka.nodeutils.network.NodeConnection;

import java.io.IOException;
import java.util.UUID;

import static org.mockito.Mockito.*;

/**
 * Created by 123 on 15.12.2017.
 */
public class ClientTest {

    private ClientStorage clientStorage;
    private MasterServerConfig config;
    private SimpleLogger logger;
    private TaskService taskService;

    @Before
    public void setUp() {
        clientStorage = mock(ClientStorage.class);
        config = mock(MasterServerConfig.class);
        logger = mock(SimpleLogger.class);
        taskService = mock(TaskService.class);
        new ApplicationContainer() {
            @Override
            public void contain(Container container) {
                container.put(clientStorage);
                container.put(config);
                container.put(logger);
                container.put(taskService);
            }
        }.contain(Container.getInstance());
    }

    @After
    public void tearDown() {
        Container.getInstance().clear();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullConnection() {
        new Client(null, mock(NodeRequest.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullRequest() {
        new Client(mock(NodeConnection.class), null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testClosedConnection() {
        NodeConnection connection = mock(NodeConnection.class);
        when(connection.isClosed()).thenReturn(true);
        new Client(connection, mock(NodeRequest.class));
    }

    @Test
    public void testSendHeartBeating() throws IOException {
        NodeConnection connection = mock(NodeConnection.class);
        NodeRequest request = mock(NodeRequest.class);
        Client client = new Client(connection, request);
        client.sendHeartBeating();
        verify(connection).send(any(NodeRequest.class));
    }

    @Test
    public void testCancelTask() throws TaskExecutionException {
        UUID id = UUID.randomUUID();
        NodeRequest request = mock(NodeRequest.class);
        when(request.getTaskId()).thenReturn(id);
        NodeConnection connection = mock(NodeConnection.class);
        Client client = spy(new Client(connection, request));
        client.cancelTask();
        verify(taskService).cancelTask(id);
        verify(client).setDone();
    }

    @Test
    public void testCancelTaskException() throws TaskExecutionException {
        UUID id = UUID.randomUUID();
        NodeRequest request = mock(NodeRequest.class);
        when(request.getTaskId()).thenReturn(id);
        NodeConnection connection = mock(NodeConnection.class);
        Client client = spy(new Client(connection, request));
        doThrow(new TaskExecutionException()).when(taskService).cancelTask(id);
        client.cancelTask();
        verify(client).setDone();
    }

    @Test
    public void testExecuteTask() throws TaskExecutionException, IOException {
        NodeRequest request = mock(NodeRequest.class);
        NodeConnection connection = mock(NodeConnection.class);
        TaskExecutionResult taskExecutionResult = mock(TaskExecutionResult.class);
        when(taskService.executeTask(request)).thenReturn(taskExecutionResult);
        Client client = spy(new Client(connection, request));
        client.executeTask();
        verify(client).sendNormal(taskExecutionResult);
        verify(client).setDone();
    }

    @Test
    public void testExecuteTaskStopped() throws TaskExecutionException, IOException {
        NodeRequest request = mock(NodeRequest.class);
        NodeConnection connection = mock(NodeConnection.class);
        TaskExecutionResult taskExecutionResult = mock(TaskExecutionResult.class);
        when(taskExecutionResult.isWasStopped()).thenReturn(true);
        when(taskService.executeTask(request)).thenReturn(taskExecutionResult);
        Client client = spy(new Client(connection, request));
        client.executeTask();
        verify(client).sendStopped();
        verify(client).setDone();
    }

    @Test
    public void testExecuteTaskException() throws TaskExecutionException, IOException {
        NodeRequest request = mock(NodeRequest.class);
        NodeConnection connection = mock(NodeConnection.class);
        when(taskService.executeTask(request)).thenThrow(new TaskExecutionException());
        Client client = spy(new Client(connection, request));
        client.executeTask();
        verify(client).sendFailed();
        verify(client, never()).setDone();
    }

    @Test
    public void testProcessConnection() throws IOException {
        NodeRequest request = mock(NodeRequest.class);
        NodeConnection connection = mock(NodeConnection.class);
        when(config.getRequestTimeOutMillis()).thenReturn(1000);
        Client client = spy(new Client(connection, request));
        doReturn(false).doReturn(true).when(client).isDone();
        client.processConnection();
        verify(connection).receive();
        verify(connection).setReadTimeOut(config.getRequestTimeOutMillis());
    }

    @Test
    public void testProcessConnectionException() throws IOException {
        NodeRequest request = mock(NodeRequest.class);
        NodeConnection connection = mock(NodeConnection.class);
        Client client = spy(new Client(connection, request));
        doReturn(false).when(client).isDone();
        doThrow(new IOException()).when(connection).receive();
        doNothing().when(client).cancelTask();
        client.processConnection();
        verify(client).cancelTask();
    }

    @Test
    public void testProcessConnectionExceptionIsDone() throws IOException {
        NodeRequest request = mock(NodeRequest.class);
        NodeConnection connection = mock(NodeConnection.class);
        Client client = spy(new Client(connection, request));
        doReturn(false).doReturn(true).when(client).isDone();
        doThrow(new IOException()).when(connection).receive();
        doNothing().when(client).cancelTask();
        client.processConnection();
        verify(client, never()).cancelTask();
    }

    @Test
    public void testRun() {
        NodeRequest request = mock(NodeRequest.class);
        NodeConnection connection = mock(NodeConnection.class);
        when(config.getRequestTimeOutMillis()).thenReturn(1000);
        Client client = spy(new Client(connection, request));
        doNothing().when(client).runExecution();
        doNothing().when(client).processConnection();
        doNothing().when(client).close();
        client.run();
        verify(clientStorage).add(client);
        verify(client).runExecution();
        verify(client).processConnection();
        verify(clientStorage).remove(client);
        verify(client).close();
    }

}
