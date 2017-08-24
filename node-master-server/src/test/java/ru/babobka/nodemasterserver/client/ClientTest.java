package ru.babobka.nodemasterserver.client;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.babobka.nodemasterserver.exception.TaskExecutionException;
import ru.babobka.nodemasterserver.server.MasterServerConfig;
import ru.babobka.nodemasterserver.service.TaskService;
import ru.babobka.nodemasterserver.task.TaskExecutionResult;
import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodeserials.NodeResponse;
import ru.babobka.nodetask.model.StoppedTasks;
import ru.babobka.nodeutils.container.ApplicationContainer;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.logger.SimpleLogger;
import ru.babobka.nodeutils.network.NodeConnection;

import java.io.IOException;
import java.util.UUID;

import static org.mockito.Mockito.*;

/**
 * Created by 123 on 29.10.2017.
 */
public class ClientTest {

    private MasterServerConfig config;
    private SimpleLogger logger;
    private TaskService taskService;
    private ClientStorage clientStorage;

    @Before
    public void setUp() {
        config = mock(MasterServerConfig.class);
        logger = mock(SimpleLogger.class);
        taskService = mock(TaskService.class);
        clientStorage = mock(ClientStorage.class);
        new ApplicationContainer() {
            @Override
            public void contain(Container container) {
                container.put(config);
                container.put(logger);
                container.put(taskService);
                container.put(clientStorage);
            }
        }.contain(Container.getInstance());
    }

    @After
    public void tearDown() {
        Container.getInstance().clear();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullConnection() {
        new Client(null, mock(NodeRequest.class), mock(StoppedTasks.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testClosedConnection() {
        NodeConnection connection = mock(NodeConnection.class);
        when(connection.isClosed()).thenReturn(true);
        new Client(connection, mock(NodeRequest.class), mock(StoppedTasks.class));
    }


    @Test(expected = IllegalArgumentException.class)
    public void testNullRequest() {
        new Client(mock(NodeConnection.class), null, mock(StoppedTasks.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullStoppedTasks() {
        new Client(mock(NodeConnection.class), mock(NodeRequest.class), null);
    }

    @Test
    public void testCancelTaskWasStopped() throws TaskExecutionException {
        StoppedTasks stoppedTasks = mock(StoppedTasks.class);
        when(stoppedTasks.wasStopped(any(NodeRequest.class))).thenReturn(true);
        Client client = new Client(mock(NodeConnection.class), mock(NodeRequest.class), stoppedTasks);
        client.cancelTask();
        verify(taskService, never()).cancelTask(any(UUID.class));
    }

    @Test
    public void testCancelTaskWasNotStopped() throws TaskExecutionException {
        StoppedTasks stoppedTasks = mock(StoppedTasks.class);
        when(stoppedTasks.wasStopped(any(NodeRequest.class))).thenReturn(false);
        Client client = new Client(mock(NodeConnection.class), mock(NodeRequest.class), stoppedTasks);
        client.cancelTask();
        verify(taskService).cancelTask(any(UUID.class));
    }

    @Test
    public void testCancelTaskWasNotStoppedException() throws TaskExecutionException {
        StoppedTasks stoppedTasks = mock(StoppedTasks.class);
        when(stoppedTasks.wasStopped(any(NodeRequest.class))).thenReturn(false);
        doThrow(new TaskExecutionException()).when(taskService).cancelTask(any(UUID.class));
        Client client = new Client(mock(NodeConnection.class), mock(NodeRequest.class), stoppedTasks);
        client.cancelTask();
        verify(logger).error(any(Exception.class));
    }

    @Test
    public void testExecuteTask() throws TaskExecutionException, IOException {
        TaskExecutionResult result = mock(TaskExecutionResult.class);
        when(taskService.executeTask(any(NodeRequest.class))).thenReturn(result);
        StoppedTasks stoppedTasks = mock(StoppedTasks.class);
        NodeConnection connection = mock(NodeConnection.class);
        Client client = spy(new Client(connection, mock(NodeRequest.class), stoppedTasks));
        client.executeTask();
        verify(connection).send(any(NodeResponse.class));
        verify(client).setDone();
    }

    @Test
    public void testExecuteTaskException() throws TaskExecutionException, IOException {
        doThrow(new TaskExecutionException()).when(taskService).executeTask(any(NodeRequest.class));
        StoppedTasks stoppedTasks = mock(StoppedTasks.class);
        NodeConnection connection = mock(NodeConnection.class);
        Client client = spy(new Client(connection, mock(NodeRequest.class), stoppedTasks));
        client.executeTask();
        verify(logger).error(any(Exception.class));
        verify(connection).send(any(NodeResponse.class));
        verify(client).setDone();
    }

    @Test
    public void testHeartBeatingTwoTicks() throws IOException {
        NodeConnection connection = mock(NodeConnection.class);
        when(config.getRequestTimeOutMillis()).thenReturn(2000);
        StoppedTasks stoppedTasks = mock(StoppedTasks.class);
        Client client = spy(new Client(connection, mock(NodeRequest.class), stoppedTasks));
        when(client.isDone()).thenReturn(false, false, true);
        client.processHeartBeating();
        verify(connection, times(2)).receive();
        verify(connection, times(2)).setReadTimeOut(config.getRequestTimeOutMillis());
    }

    @Test
    public void testHeartBeatingExceptionNotDone() throws IOException {
        NodeConnection connection = mock(NodeConnection.class);
        when(config.getRequestTimeOutMillis()).thenReturn(2000);
        StoppedTasks stoppedTasks = mock(StoppedTasks.class);
        doThrow(new IOException()).when(connection).receive();
        Client client = spy(new Client(connection, mock(NodeRequest.class), stoppedTasks));
        when(client.isDone()).thenReturn(false);
        client.processHeartBeating();
        verify(client).cancelTask();
        verify(logger).error(any(Exception.class));
    }
}
