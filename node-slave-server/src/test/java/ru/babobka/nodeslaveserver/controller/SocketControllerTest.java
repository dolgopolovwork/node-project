package ru.babobka.nodeslaveserver.controller;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodeserials.NodeResponse;
import ru.babobka.nodeslaveserver.key.SlaveServerKey;
import ru.babobka.nodeslaveserver.runnable.RequestHandlerRunnable;
import ru.babobka.nodeslaveserver.server.SlaveServerConfig;
import ru.babobka.nodeslaveserver.task.TaskRunnerService;
import ru.babobka.nodetask.TaskPool;
import ru.babobka.nodetask.TasksStorage;
import ru.babobka.nodetask.model.SubTask;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.logger.NodeLogger;
import ru.babobka.nodeutils.network.NodeConnection;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

import static org.mockito.Mockito.*;

/**
 * Created by 123 on 03.09.2017.
 */
public class SocketControllerTest {
    private TaskPool taskPool;
    private SlaveServerConfig slaveServerConfig;
    private NodeLogger nodeLogger;
    private TasksStorage tasksStorage;
    private SocketController socketController;
    private ExecutorService executorService;
    private TaskRunnerService taskRunnerService;

    @Before
    public void setUp() {
        taskPool = mock(TaskPool.class);
        slaveServerConfig = mock(SlaveServerConfig.class);
        nodeLogger = mock(NodeLogger.class);
        tasksStorage = mock(TasksStorage.class);
        executorService = mock(ExecutorService.class);
        taskRunnerService = mock(TaskRunnerService.class);
        Container.getInstance().put(SlaveServerKey.SLAVE_SERVER_TASK_POOL, taskPool);
        Container.getInstance().put(slaveServerConfig);
        Container.getInstance().put(nodeLogger);
        Container.getInstance().put(taskRunnerService);
        socketController = new SocketController(executorService, tasksStorage);
    }

    @After
    public void tearDown() throws IOException {
        Container.getInstance().clear();
        socketController.close();
    }

    @Test
    public void testControlHeartBeat() throws IOException {
        NodeRequest request = NodeRequest.heartBeat();
        NodeConnection connection = mock(NodeConnection.class);
        when(connection.receive()).thenReturn(request);
        socketController.control(connection);
        verify(connection).send(any(NodeResponse.class));
        verify(executorService, never()).submit(any(Runnable.class));
    }

    @Test
    public void testControlStop() throws IOException {
        UUID uuid = UUID.randomUUID();
        NodeRequest request = NodeRequest.stop(uuid);
        NodeConnection connection = mock(NodeConnection.class);
        when(connection.receive()).thenReturn(request);
        socketController.control(connection);
        verify(tasksStorage).stopTask(request);
        verify(connection, never()).send(any(NodeResponse.class));
        verify(executorService, never()).submit(any(RequestHandlerRunnable.class));
    }

    @Test
    public void testControlRaceTaskExists() throws IOException {
        UUID uuid = UUID.randomUUID();
        String taskName = "test";
        NodeRequest request = NodeRequest.race(uuid, taskName, null);
        when(tasksStorage.exists(uuid)).thenReturn(true);
        NodeConnection connection = mock(NodeConnection.class);
        when(connection.receive()).thenReturn(request);
        socketController.control(connection);
        verify(nodeLogger).warning(anyString());
        verify(connection, never()).send(any(NodeResponse.class));
        verify(executorService, never()).submit(any(RequestHandlerRunnable.class));
    }

    @Test
    public void testControlRaceNotStopped() throws IOException {
        UUID uuid = UUID.randomUUID();
        String taskName = "test";
        NodeRequest request = NodeRequest.race(uuid, taskName, null);
        when(tasksStorage.exists(uuid)).thenReturn(false);
        NodeConnection connection = mock(NodeConnection.class);
        when(connection.receive()).thenReturn(request);
        SubTask subTask = mock(SubTask.class);
        when(taskPool.get(taskName)).thenReturn(subTask);
        socketController.control(connection);
        verify(tasksStorage).put(request, subTask);
        verify(connection, never()).send(any(NodeResponse.class));
        verify(executorService).submit(any(RequestHandlerRunnable.class));
    }

    @Test
    public void testControlNormalNotStopped() throws IOException {
        UUID uuid = UUID.randomUUID();
        String taskName = "test";
        NodeRequest request = NodeRequest.regular(uuid, taskName, null);
        NodeConnection connection = mock(NodeConnection.class);
        when(connection.receive()).thenReturn(request);
        SubTask subTask = mock(SubTask.class);
        when(taskPool.get(taskName)).thenReturn(subTask);
        socketController.control(connection);
        verify(tasksStorage).put(request, subTask);
        verify(connection, never()).send(any(NodeResponse.class));
        verify(executorService).submit(any(RequestHandlerRunnable.class));
    }

    @Test
    public void testControlNormalStopped() throws IOException {
        UUID uuid = UUID.randomUUID();
        String taskName = "test";
        NodeRequest request = NodeRequest.regular(uuid, taskName, null);
        NodeConnection connection = mock(NodeConnection.class);
        when(connection.receive()).thenReturn(request);
        when(tasksStorage.wasStopped(request)).thenReturn(true);
        socketController.control(connection);
        verify(connection, never()).send(any(NodeResponse.class));
        verify(executorService, never()).submit(any(RequestHandlerRunnable.class));
    }
}
