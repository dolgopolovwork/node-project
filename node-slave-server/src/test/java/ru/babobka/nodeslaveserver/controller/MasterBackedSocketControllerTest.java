package ru.babobka.nodeslaveserver.controller;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.babobka.nodeconfigs.slave.SlaveServerConfig;
import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodeslaveserver.task.RaceStyleTaskStorage;
import ru.babobka.nodetask.model.StoppedTasks;
import ru.babobka.nodetask.service.TaskService;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.network.NodeConnection;
import ru.babobka.nodeutils.react.PubSub;

import java.util.concurrent.ExecutorService;

import static org.mockito.Mockito.*;
import static ru.babobka.nodeslaveserver.key.SlaveServerKey.SLAVE_SERVER_REQUEST_STREAM;

public class MasterBackedSocketControllerTest {

    private SlaveServerConfig slaveServerConfig;
    private NodeConnection connection;
    private TaskService taskService;
    private PubSub<NodeRequest> requestsStream;
    private ExecutorService executorService;
    private RaceStyleTaskStorage raceStyleTaskStorage;
    private StoppedTasks stoppedTasks;
    private MasterBackedSocketController masterBackedSocketController;

    @Before
    public void setUp() {
        slaveServerConfig = mock(SlaveServerConfig.class);
        when(slaveServerConfig.getRequestTimeoutMillis()).thenReturn(31);
        raceStyleTaskStorage = mock(RaceStyleTaskStorage.class);
        stoppedTasks = mock(StoppedTasks.class);
        connection = mock(NodeConnection.class);
        taskService = mock(TaskService.class);
        requestsStream = mock(PubSub.class);
        executorService = mock(ExecutorService.class);
        Container.getInstance().put(container -> {
            container.put(taskService);
            container.put(SLAVE_SERVER_REQUEST_STREAM, requestsStream);
            container.put(slaveServerConfig);
        });
        masterBackedSocketController = new MasterBackedSocketController(
                connection, executorService, raceStyleTaskStorage, stoppedTasks);
    }

    @After
    public void tearDown() {
        Container.getInstance().clear();
    }

    @Test
    public void testOnStop() {
        NodeRequest request = mock(NodeRequest.class);
        masterBackedSocketController.onStop(request);
        verify(requestsStream).publish(request);
        verify(stoppedTasks).add(request);
    }

    @Test
    public void testClose() {
        masterBackedSocketController.close();
        verify(stoppedTasks).clear();
        verify(raceStyleTaskStorage).clear();
    }

    @Test
    public void testOnStoppedRequest() {
        NodeRequest request = mock(NodeRequest.class);
        when(stoppedTasks.wasStopped(request)).thenReturn(true);
        masterBackedSocketController.onExecute(request);
        verify(requestsStream, never()).publish(any());
    }

    @Test
    public void testOnRequest() {
        NodeRequest request = mock(NodeRequest.class);
        when(stoppedTasks.wasStopped(request)).thenReturn(false);
        masterBackedSocketController.onExecute(request);
        verify(requestsStream).publish(request);
    }
}
