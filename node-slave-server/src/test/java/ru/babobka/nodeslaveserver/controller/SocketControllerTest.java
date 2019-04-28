package ru.babobka.nodeslaveserver.controller;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.babobka.nodeconfigs.slave.SlaveServerConfig;
import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodeserials.NodeResponse;
import ru.babobka.nodeslaveserver.key.SlaveServerKey;
import ru.babobka.nodeslaveserver.task.TaskRunnerService;
import ru.babobka.nodeslaveserver.thread.SlaveBackedNodeRequestHandler;
import ru.babobka.nodetask.TaskPool;
import ru.babobka.nodetask.TasksStorage;
import ru.babobka.nodetask.model.SubTask;
import ru.babobka.nodeutils.container.Container;
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
    private TasksStorage tasksStorage;
    private ExecutorService executorService;
    private TaskRunnerService taskRunnerService;
    private NodeConnection connection;

    @Before
    public void setUp() {
        connection = mock(NodeConnection.class);
        taskPool = mock(TaskPool.class);
        slaveServerConfig = mock(SlaveServerConfig.class);
        tasksStorage = mock(TasksStorage.class);
        executorService = mock(ExecutorService.class);
        taskRunnerService = mock(TaskRunnerService.class);
        Container.getInstance().put(container -> {
            container.put(SlaveServerKey.SLAVE_SERVER_TASK_POOL, taskPool);
            container.put(slaveServerConfig);
            container.put(taskRunnerService);
        });
    }

    @After
    public void tearDown() throws IOException {
        Container.getInstance().clear();
    }

    @Test
    public void testControlHeartBeat() throws IOException {
        NodeRequest request = NodeRequest.heartBeat();
        when(connection.receive()).thenReturn(request);
        try (AbstractSocketController socketController = new SlaveBackedSocketController(connection, tasksStorage, executorService)) {
            socketController.control();
        }
        verify(connection).send(any(NodeResponse.class));
        verify(executorService, never()).submit(any(Runnable.class));
    }

    @Test
    public void testControlStop() throws IOException {
        UUID uuid = UUID.randomUUID();
        NodeRequest request = NodeRequest.stop(uuid);
        NodeConnection connection = mock(NodeConnection.class);
        when(connection.receive()).thenReturn(request);
        try (AbstractSocketController socketController = new SlaveBackedSocketController(connection, tasksStorage, executorService)) {
            socketController.control();
        }
        verify(tasksStorage).stopTask(request);
        verify(connection, never()).send(any(NodeResponse.class));
        verify(executorService, never()).submit(any(SlaveBackedNodeRequestHandler.class));
    }

    @Test
    public void testControlRaceTaskExists() throws IOException {
        UUID uuid = UUID.randomUUID();
        String taskName = "test";
        NodeRequest request = NodeRequest.race(uuid, taskName, null);
        when(tasksStorage.exists(uuid)).thenReturn(true);
        NodeConnection connection = mock(NodeConnection.class);
        when(connection.receive()).thenReturn(request);
        try (AbstractSocketController socketController = new SlaveBackedSocketController(connection, tasksStorage, executorService)) {
            socketController.control();
        }
        verify(connection, never()).send(any(NodeResponse.class));
        verify(executorService, never()).submit(any(SlaveBackedNodeRequestHandler.class));
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
        try (AbstractSocketController socketController = new SlaveBackedSocketController(connection, tasksStorage, executorService)) {
            socketController.control();
        }
        verify(tasksStorage).put(request, subTask);
        verify(connection, never()).send(any(NodeResponse.class));
        verify(executorService).submit(any(SlaveBackedNodeRequestHandler.class));
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
        try (AbstractSocketController socketController = new SlaveBackedSocketController(connection, tasksStorage, executorService)) {
            socketController.control();
        }
        verify(tasksStorage).put(request, subTask);
        verify(connection, never()).send(any(NodeResponse.class));
        verify(executorService).submit(any(SlaveBackedNodeRequestHandler.class));
    }

    @Test
    public void testControlNormalStopped() throws IOException {
        UUID uuid = UUID.randomUUID();
        String taskName = "test";
        NodeRequest request = NodeRequest.regular(uuid, taskName, null);
        NodeConnection connection = mock(NodeConnection.class);
        when(connection.receive()).thenReturn(request);
        when(tasksStorage.wasStopped(request)).thenReturn(true);
        try (AbstractSocketController socketController = new SlaveBackedSocketController(connection, tasksStorage, executorService)) {
            socketController.control();
        }
        verify(connection, never()).send(any(NodeResponse.class));
        verify(executorService, never()).submit(any(SlaveBackedNodeRequestHandler.class));
    }
}
