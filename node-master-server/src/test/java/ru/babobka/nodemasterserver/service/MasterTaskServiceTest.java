package ru.babobka.nodemasterserver.service;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import ru.babobka.nodebusiness.monitoring.TaskMonitoringService;
import ru.babobka.nodemasterserver.exception.DistributionException;
import ru.babobka.nodemasterserver.key.MasterServerKey;
import ru.babobka.nodemasterserver.listener.OnRaceStyleTaskIsReady;
import ru.babobka.nodemasterserver.listener.OnTaskIsReady;
import ru.babobka.nodemasterserver.mapper.ResponsesMapper;
import ru.babobka.nodemasterserver.model.ResponseStorage;
import ru.babobka.nodemasterserver.model.Responses;
import ru.babobka.nodemasterserver.slave.Slave;
import ru.babobka.nodemasterserver.slave.SlavesStorage;
import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodetask.TaskPool;
import ru.babobka.nodetask.exception.TaskExecutionException;
import ru.babobka.nodetask.model.DataValidators;
import ru.babobka.nodetask.model.RequestDistributor;
import ru.babobka.nodetask.model.SubTask;
import ru.babobka.nodetask.service.TaskStartResult;
import ru.babobka.nodeutils.container.Container;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Created by 123 on 10.09.2017.
 */
public class MasterTaskServiceTest {
    private TaskPool taskPool;
    private SlavesStorage slavesStorage;
    private ResponseStorage responseStorage;
    private DistributionService distributionService;
    private MasterTaskService taskService;
    private TaskMonitoringService taskMonitoringService;

    @Before
    public void setUp() {
        taskMonitoringService = mock(TaskMonitoringService.class);
        taskPool = mock(TaskPool.class);
        slavesStorage = mock(SlavesStorage.class);
        responseStorage = mock(ResponseStorage.class);
        distributionService = mock(DistributionService.class);
        Container.getInstance().put(container -> {
            container.put(MasterServerKey.MASTER_SERVER_TASK_POOL, taskPool);
            container.put(slavesStorage);
            container.put(taskMonitoringService);
            container.put(responseStorage);
            container.put(distributionService);
            container.put(mock(OnTaskIsReady.class));
            container.put(mock(OnRaceStyleTaskIsReady.class));
            container.put(new ResponsesMapper());
        });

        taskService = spy(new MasterTaskService());
    }

    @After
    public void tearDown() {
        Container.getInstance().clear();
    }

    @Test(expected = NullPointerException.class)
    public void testCancelTaskNullTaskId() {
        taskService.cancelTask(null, item -> {
        }, error -> {
        });
    }

    @Test
    public void testCancelTaskNullResponses() {
        taskService.cancelTask(UUID.randomUUID(), Assert::assertFalse, error -> fail());
    }

    @Test
    public void testCancelTask() {
        UUID taskId = UUID.randomUUID();
        Slave slave = mock(Slave.class);
        List<Slave> slaves = Arrays.asList(slave, slave, slave);
        when(slavesStorage.getListByTaskId(taskId)).thenReturn(slaves);
        Responses responses = mock(Responses.class);
        when(distributionService.broadcastStopRequests(slaves, taskId)).thenReturn(true);
        when(responseStorage.get(taskId)).thenReturn(responses);
        taskService.cancelTask(taskId, Assert::assertTrue, item -> fail());
        verify(responseStorage).setStopAllResponses(taskId);
        verify(distributionService).broadcastStopRequests(slaves, taskId);
    }

    @Test
    public void testCancelTaskEmptyCluster() {
        UUID taskId = UUID.randomUUID();
        Slave slave = mock(Slave.class);
        List<Slave> slaves = Arrays.asList(slave, slave, slave);
        when(slavesStorage.getListByTaskId(taskId)).thenReturn(slaves);
        Responses responses = mock(Responses.class);
        when(responseStorage.get(taskId)).thenReturn(responses);
        doReturn(false).when(distributionService).broadcastStopRequests(slaves, taskId);
        taskService.cancelTask(taskId, Assert::assertFalse, error -> fail());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBroadcastTaskBadNodes() throws DistributionException, TaskExecutionException {
        taskService.broadcastTask(mock(NodeRequest.class), mock(SubTask.class), -1);
    }

    @Test
    public void testBroadcastTaskLittleRequest() throws DistributionException, TaskExecutionException {
        int actualClusterSize = 10;
        when(slavesStorage.getClusterSize(anyString())).thenReturn(actualClusterSize);
        RequestDistributor requestDistributor = mock(RequestDistributor.class);
        UUID taskId = UUID.randomUUID();
        SubTask task = mock(SubTask.class);
        NodeRequest request = mock(NodeRequest.class);
        when(request.getTaskName()).thenReturn("test task");
        when(requestDistributor.distribute(eq(request), anyInt())).thenReturn(new LinkedList<>());
        when(task.getDistributor()).thenReturn(requestDistributor);
        when(request.getTaskId()).thenReturn(taskId);
        when(task.isSingleNodeTask(request)).thenReturn(true);
        taskService.broadcastTask(request, task, 2);
        verify(responseStorage).create(eq(taskId), any(Responses.class));
        verify(requestDistributor).distribute(request, 1);
        verify(distributionService).broadcastRequests(eq(request.getTaskName()), anyList());
    }

    @Test
    public void testBroadcastTaskLittleRequestAllNodes() throws DistributionException, TaskExecutionException {
        int actualClusterSize = 10;
        when(slavesStorage.getClusterSize(anyString())).thenReturn(actualClusterSize);
        RequestDistributor requestDistributor = mock(RequestDistributor.class);
        UUID taskId = UUID.randomUUID();
        SubTask task = mock(SubTask.class);
        NodeRequest request = mock(NodeRequest.class);
        when(request.getTaskName()).thenReturn("test task");
        when(requestDistributor.distribute(eq(request), anyInt())).thenReturn(new LinkedList<>());
        when(task.getDistributor()).thenReturn(requestDistributor);
        when(request.getTaskId()).thenReturn(taskId);
        when(task.isSingleNodeTask(request)).thenReturn(false);
        taskService.broadcastTask(request, task, 0);
        verify(responseStorage).create(eq(taskId), any(Responses.class));
        verify(requestDistributor).distribute(request, actualClusterSize);
        verify(distributionService).broadcastRequests(eq(request.getTaskName()), anyList());
    }

    @Test
    public void testBroadcastTaskLittleRequestMaxNodesWasSet() throws DistributionException, TaskExecutionException {
        int maxNodes = 5;
        int actualClusterSize = 10;
        when(slavesStorage.getClusterSize(anyString())).thenReturn(actualClusterSize);
        RequestDistributor requestDistributor = mock(RequestDistributor.class);
        UUID taskId = UUID.randomUUID();
        SubTask task = mock(SubTask.class);
        NodeRequest request = mock(NodeRequest.class);
        when(request.getTaskName()).thenReturn("test task");
        when(requestDistributor.distribute(eq(request), anyInt())).thenReturn(new LinkedList<>());
        when(task.getDistributor()).thenReturn(requestDistributor);
        when(request.getTaskId()).thenReturn(taskId);
        when(task.isSingleNodeTask(request)).thenReturn(false);
        taskService.broadcastTask(request, task, maxNodes);
        verify(responseStorage).create(eq(taskId), any(Responses.class));
        verify(requestDistributor).distribute(request, maxNodes);
        verify(distributionService).broadcastRequests(eq(request.getTaskName()), anyList());
    }

    @Test
    public void testBroadcastTaskLittleRequestMaxNodesIsBiggerThanActualClusterSize() throws DistributionException, TaskExecutionException {
        int maxNodes = 12;
        int actualClusterSize = 10;
        when(slavesStorage.getClusterSize(anyString())).thenReturn(actualClusterSize);
        RequestDistributor requestDistributor = mock(RequestDistributor.class);
        UUID taskId = UUID.randomUUID();
        SubTask task = mock(SubTask.class);
        NodeRequest request = mock(NodeRequest.class);
        when(request.getTaskName()).thenReturn("test task");
        when(requestDistributor.distribute(eq(request), anyInt())).thenReturn(new LinkedList<>());
        when(task.getDistributor()).thenReturn(requestDistributor);
        when(request.getTaskId()).thenReturn(taskId);
        when(task.isSingleNodeTask(request)).thenReturn(false);
        taskService.broadcastTask(request, task, maxNodes);
        verify(responseStorage).create(eq(taskId), any(Responses.class));
        verify(requestDistributor).distribute(request, actualClusterSize);
        verify(distributionService).broadcastRequests(eq(request.getTaskName()), anyList());
    }

    @Test
    public void testStartTaskNotValid() throws TaskExecutionException {
        UUID taskId = UUID.randomUUID();
        NodeRequest request = mock(NodeRequest.class);
        when(request.getTaskId()).thenReturn(taskId);
        DataValidators dataValidators = mock(DataValidators.class);
        when(dataValidators.isValidRequest(request)).thenReturn(false);
        SubTask task = mock(SubTask.class);
        when(task.getDataValidators()).thenReturn(dataValidators);
        TaskStartResult taskStartResult = taskService.startTask(request, task, 1);
        assertTrue(taskStartResult.isValidationError());
        assertEquals(taskStartResult.getTaskId(), taskId);
    }

    @Test
    public void testStartTaskTooBig() throws TaskExecutionException {
        UUID taskId = UUID.randomUUID();
        NodeRequest request = mock(NodeRequest.class);
        when(request.getTaskId()).thenReturn(taskId);
        DataValidators dataValidators = mock(DataValidators.class);
        when(dataValidators.isValidRequest(request)).thenReturn(true);
        SubTask task = mock(SubTask.class);
        when(task.isRequestDataTooBig(request)).thenReturn(true);
        when(task.getDataValidators()).thenReturn(dataValidators);
        TaskStartResult taskStartResult = taskService.startTask(request, task, 1);
        assertTrue(taskStartResult.isValidationError());
        assertEquals(taskStartResult.getTaskId(), taskId);
    }


    @Test
    public void testStartTask() throws DistributionException, TaskExecutionException {
        int maxNodes = 1;
        UUID taskId = UUID.randomUUID();
        NodeRequest request = mock(NodeRequest.class);
        when(request.getTaskId()).thenReturn(taskId);
        DataValidators dataValidators = mock(DataValidators.class);
        when(dataValidators.isValidRequest(request)).thenReturn(true);
        SubTask task = mock(SubTask.class);
        when(task.isRequestDataTooBig(request)).thenReturn(false);
        doNothing().when(taskService).broadcastTask(request, task, maxNodes);
        when(task.getDataValidators()).thenReturn(dataValidators);
        TaskStartResult taskStartResult = taskService.startTask(request, task, maxNodes);
        assertFalse(taskStartResult.isValidationError());
        assertFalse(taskStartResult.isSystemError());
        assertEquals(taskStartResult.getTaskId(), taskId);
        verify(taskService).broadcastTask(request, task, maxNodes);
    }

    @Test
    public void testStartTaskDistributionException() throws DistributionException, TaskExecutionException {
        int maxNodes = 1;
        UUID taskId = UUID.randomUUID();
        NodeRequest request = mock(NodeRequest.class);
        when(request.getTaskId()).thenReturn(taskId);
        DataValidators dataValidators = mock(DataValidators.class);
        when(dataValidators.isValidRequest(request)).thenReturn(true);
        SubTask task = mock(SubTask.class);
        when(task.isRequestDataTooBig(request)).thenReturn(false);
        doThrow(new DistributionException()).when(taskService).broadcastTask(request, task, maxNodes);
        when(task.getDataValidators()).thenReturn(dataValidators);
        TaskStartResult taskStartResult = taskService.startTask(request, task, maxNodes);
        assertTrue(taskStartResult.isSystemError());
        assertEquals(taskStartResult.getTaskId(), taskId);
        verify(taskService).broadcastTask(request, task, maxNodes);
    }
}
