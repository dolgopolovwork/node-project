package ru.babobka.nodemasterserver.client;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.babobka.nodeconfigs.master.MasterServerConfig;
import ru.babobka.nodeconfigs.master.TimeConfig;
import ru.babobka.nodemasterserver.exception.TaskExecutionException;
import ru.babobka.nodemasterserver.service.TaskService;
import ru.babobka.nodemasterserver.task.TaskExecutionResult;
import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.logger.NodeLogger;
import ru.babobka.nodeutils.network.NodeConnection;

import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;

import static org.mockito.Mockito.*;

/**
 * Created by 123 on 15.12.2017.
 */
public class ClientTest {

    private ClientStorage clientStorage;
    private MasterServerConfig config;
    private NodeLogger nodeLogger;
    private TaskService taskService;

    @Before
    public void setUp() {
        clientStorage = mock(ClientStorage.class);
        config = mock(MasterServerConfig.class);
        nodeLogger = mock(NodeLogger.class);
        taskService = mock(TaskService.class);
        Container.getInstance().put(clientStorage);
        Container.getInstance().put(config);
        Container.getInstance().put(nodeLogger);
        Container.getInstance().put(taskService);
    }

    @After
    public void tearDown() {
        Container.getInstance().clear();
    }

    @Test(expected = NullPointerException.class)
    public void testNullConnection() {
        new Client(null, Arrays.asList(mock(NodeRequest.class)));
    }

    @Test(expected = NullPointerException.class)
    public void testNullRequest() {
        new Client(mock(NodeConnection.class), null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testClosedConnection() {
        NodeConnection connection = mock(NodeConnection.class);
        when(connection.isClosed()).thenReturn(true);
        new Client(connection, Arrays.asList(mock(NodeRequest.class)));
    }

    @Test
    public void testSendHeartBeating() throws IOException {
        NodeConnection connection = mock(NodeConnection.class);
        NodeRequest request = mock(NodeRequest.class);
        Client client = new Client(connection, Arrays.asList(request));
        client.sendHeartBeating();
        verify(connection).send(any(NodeRequest.class));
    }

    @Test
    public void testCancelTask() throws TaskExecutionException {
        UUID id = UUID.randomUUID();
        NodeRequest request = mock(NodeRequest.class);
        when(request.getTaskId()).thenReturn(id);
        NodeConnection connection = mock(NodeConnection.class);
        Client client = spy(new Client(connection, Arrays.asList(request)));
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
        Client client = spy(new Client(connection, Arrays.asList(request)));
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
        Client client = spy(new Client(connection, Arrays.asList(request)));
        client.executeTask(request);
        verify(client).sendNormal(taskExecutionResult, request);
        verify(client).setDone();
    }

    @Test
    public void testExecuteTaskStopped() throws TaskExecutionException, IOException {
        NodeRequest request = mock(NodeRequest.class);
        NodeConnection connection = mock(NodeConnection.class);
        TaskExecutionResult taskExecutionResult = mock(TaskExecutionResult.class);
        when(taskExecutionResult.wasStopped()).thenReturn(true);
        when(taskService.executeTask(request)).thenReturn(taskExecutionResult);
        Client client = spy(new Client(connection, Arrays.asList(request)));
        client.executeTask(request);
        verify(client).sendStopped();
        verify(client).setDone();
    }

    @Test
    public void testExecuteTaskException() throws TaskExecutionException, IOException {
        NodeRequest request = mock(NodeRequest.class);
        NodeConnection connection = mock(NodeConnection.class);
        when(taskService.executeTask(request)).thenThrow(new TaskExecutionException());
        Client client = spy(new Client(connection, Arrays.asList(request)));
        client.executeTask(request);
        verify(client).sendFailed();
        verify(client, never()).setDone();
    }

    @Test
    public void testProcessConnection() throws IOException {
        NodeRequest request = mock(NodeRequest.class);
        NodeConnection connection = mock(NodeConnection.class);
        TimeConfig timeConfig = new TimeConfig();
        timeConfig.setRequestReadTimeOutMillis(1000);
        when(config.getTime()).thenReturn(timeConfig);
        Client client = spy(new Client(connection, Arrays.asList(request)));
        doReturn(false).doReturn(true).when(client).isDone();
        client.processConnection();
        verify(connection).receive();
        verify(connection).setReadTimeOut(timeConfig.getRequestReadTimeOutMillis());
    }

    @Test
    public void testProcessConnectionException() throws IOException {
        NodeRequest request = mock(NodeRequest.class);
        NodeConnection connection = mock(NodeConnection.class);
        Client client = spy(new Client(connection, Arrays.asList(request)));
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
        Client client = spy(new Client(connection, Arrays.asList(request)));
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
        TimeConfig timeConfig = new TimeConfig();
        timeConfig.setRequestReadTimeOutMillis(1000);
        when(config.getTime()).thenReturn(timeConfig);
        Client client = spy(new Client(connection, Arrays.asList(request)));
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
