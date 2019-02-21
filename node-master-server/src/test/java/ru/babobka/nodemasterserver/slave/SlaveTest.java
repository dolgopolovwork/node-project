package ru.babobka.nodemasterserver.slave;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.babobka.nodeconfigs.master.MasterServerConfig;
import ru.babobka.nodeconfigs.master.TimeConfig;
import ru.babobka.nodemasterserver.exception.DistributionException;
import ru.babobka.nodemasterserver.listener.OnSlaveExitListener;
import ru.babobka.nodemasterserver.model.ResponseStorage;
import ru.babobka.nodemasterserver.model.Responses;
import ru.babobka.nodemasterserver.service.DistributionService;
import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodeserials.NodeResponse;
import ru.babobka.nodeserials.enumerations.ResponseStatus;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.func.Applyer;
import ru.babobka.nodeutils.log.LoggerInit;
import ru.babobka.nodeutils.network.NodeConnection;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

/**
 * Created by 123 on 07.09.2017.
 */
public class SlaveTest {
    static {
        LoggerInit.initConsoleLogger();
    }

    private MasterServerConfig masterServerConfig;
    private ResponseStorage responseStorage;
    private SlavesStorage slavesStorage;
    private DistributionService distributionService;

    @Before
    public void setUp() throws IOException {
        masterServerConfig = mock(MasterServerConfig.class);
        responseStorage = mock(ResponseStorage.class);
        slavesStorage = mock(SlavesStorage.class);
        distributionService = mock(DistributionService.class);
        Container.getInstance().put(container -> {
            container.put(masterServerConfig);
            container.put(responseStorage);
            container.put(distributionService);
            container.put(slavesStorage);
        });
    }

    @After
    public void tearDown() {
        Container.getInstance().clear();
    }

    @Test(expected = NullPointerException.class)
    public void testConstructorNullAvailableTasks() {
        new Slave(null, mock(NodeConnection.class));
    }

    @Test(expected = NullPointerException.class)
    public void testConstructorNullNodeConnection() {
        new Slave(new HashSet<>(), null);
    }

    @Test
    public void testInterrupt() {
        NodeConnection connection = mock(NodeConnection.class);
        Slave slave = new Slave(new HashSet<>(), connection);
        slave.interrupt();
        verify(connection).close();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testClosedConnection() {
        NodeConnection connection = mock(NodeConnection.class);
        when(connection.isClosed()).thenReturn(true);
        new Slave(new HashSet<>(), connection);
    }

    @Test
    public void testSendHeartBeating() throws IOException {
        NodeConnection connection = mock(NodeConnection.class);
        Slave slave = new Slave(new HashSet<>(), connection);
        slave.sendHeartBeating();
        verify(connection).send(any(NodeRequest.class));
    }

    @Test
    public void testProcessConnectionDeath() throws IOException {
        NodeConnection connection = mock(NodeConnection.class);
        Slave slave = spy(new Slave(new HashSet<>(), connection));
        TimeConfig timeConfig = new TimeConfig();
        timeConfig.setDataOutDateMillis(10_000);
        when(masterServerConfig.getTime()).thenReturn(timeConfig);
        NodeResponse response = mock(NodeResponse.class);
        when(response.getTimeStamp()).thenReturn(System.currentTimeMillis());
        when(response.getStatus()).thenReturn(ResponseStatus.DEATH);
        when(connection.receive()).thenReturn(response);
        assertFalse(slave.processConnection());
        verify(connection).setReadTimeOut(timeConfig.getRequestReadTimeOutMillis());
        verify(slave, never()).onReceive(response);
    }

    @Test
    public void testProcessConnectionHeartBeating() throws IOException {
        NodeConnection connection = mock(NodeConnection.class);
        Slave slave = spy(new Slave(new HashSet<>(), connection));
        TimeConfig timeConfig = new TimeConfig();
        when(masterServerConfig.getTime()).thenReturn(timeConfig);
        NodeResponse response = mock(NodeResponse.class);
        when(response.getStatus()).thenReturn(ResponseStatus.HEART_BEAT);
        when(connection.receive()).thenReturn(response);
        assertTrue(slave.processConnection());
        verify(connection).setReadTimeOut(timeConfig.getRequestReadTimeOutMillis());
        verify(slave, never()).onReceive(response);
    }

    @Test
    public void testProcessConnectionOutDated() throws IOException {
        NodeConnection connection = mock(NodeConnection.class);
        Slave slave = spy(new Slave(new HashSet<>(), connection));
        TimeConfig timeConfig = new TimeConfig();
        timeConfig.setDataOutDateMillis(0);
        when(masterServerConfig.getTime()).thenReturn(timeConfig);
        NodeResponse response = mock(NodeResponse.class);
        when(response.getStatus()).thenReturn(ResponseStatus.NORMAL);
        when(response.getTimeStamp()).thenReturn(0L);
        when(connection.receive()).thenReturn(response);
        doNothing().when(slave).onReceive(response);
        assertTrue(slave.processConnection());
        verify(connection).setReadTimeOut(timeConfig.getRequestReadTimeOutMillis());
        verify(slave, never()).onReceive(response);
    }

    @Test
    public void testProcessConnection() throws IOException {
        NodeConnection connection = mock(NodeConnection.class);
        Slave slave = spy(new Slave(new HashSet<>(), connection));
        TimeConfig timeConfig = new TimeConfig();
        timeConfig.setDataOutDateMillis(10_000);
        when(masterServerConfig.getTime()).thenReturn(timeConfig);
        NodeResponse response = mock(NodeResponse.class);
        when(response.getStatus()).thenReturn(ResponseStatus.NORMAL);
        when(response.getTimeStamp()).thenReturn(System.currentTimeMillis());
        when(connection.receive()).thenReturn(response);
        doNothing().when(slave).onReceive(response);
        assertTrue(slave.processConnection());
        verify(connection).setReadTimeOut(timeConfig.getRequestReadTimeOutMillis());
        verify(slave).onReceive(response);
    }

    @Test
    public void testExecuteTask() throws IOException {
        NodeConnection connection = mock(NodeConnection.class);
        UUID taskId = UUID.randomUUID();
        String taskName = "task name";
        NodeRequest request = NodeRequest.regular(taskId, taskName, null);
        Slave slave = new Slave(new HashSet<>(), connection);
        slave.executeTask(request);
        verify(connection).send(request);
    }

    @Test
    public void testExecuteTaskRace() throws IOException {
        NodeConnection connection = mock(NodeConnection.class);
        UUID taskId = UUID.randomUUID();
        String taskName = "task name";
        NodeRequest request = NodeRequest.race(taskId, taskName, null);
        Slave slave = new Slave(new HashSet<>(), connection);
        slave.executeTask(request);
        verify(connection).send(request);
    }

    @Test
    public void testExecuteTaskRaceAlreadyHasRequest() throws IOException {
        Responses responses = mock(Responses.class);
        NodeConnection connection = mock(NodeConnection.class);
        UUID taskId = UUID.randomUUID();
        String taskName = "task name";
        NodeRequest request = NodeRequest.race(taskId, taskName, null);
        when(responseStorage.get(taskId)).thenReturn(responses);
        Slave slave = new Slave(new HashSet<>(), connection);
        slave.addTask(request);
        slave.executeTask(request);
        verify(connection, never()).send(any(NodeRequest.class));
        verify(responses, never()).add(any(NodeResponse.class));
    }

    @Test
    public void testOnReceiveTaskExists() {
        UUID taskId = UUID.randomUUID();
        NodeResponse response = NodeResponse.dummy(taskId);
        NodeConnection connection = mock(NodeConnection.class);
        when(responseStorage.exists(taskId)).thenReturn(true);
        Responses responses = mock(Responses.class);
        when(responseStorage.get(taskId)).thenReturn(responses);
        Slave slave = spy(new Slave(new HashSet<>(), connection));
        slave.onReceive(response);
        verify(slave).removeTask(response);
        verify(responses).add(response);
    }

    @Test
    public void testOnExitNoTasks() {
        NodeConnection connection = mock(NodeConnection.class);
        OnSlaveExitListener onSlaveExitListener = mock(OnSlaveExitListener.class);
        Slave slave = spy(new Slave(new HashSet<>(), connection, onSlaveExitListener));
        when(slave.isNoTasks()).thenReturn(true);
        slave.onExit();
        verify(slavesStorage).remove(slave);
        verify(onSlaveExitListener).onExit();
        verify(connection).close();
    }

    @Test
    public void testOnExitHaveTasks() throws IOException {
        NodeConnection connection = mock(NodeConnection.class);
        Slave slave = spy(new Slave(new HashSet<>(), connection));
        when(slave.isNoTasks()).thenReturn(false);
        slave.onExit();
        verify(slavesStorage).remove(slave);
        verify(slave).redistributeTasks();
        verify(connection).close();
    }

    @Test
    public void testOnExitHaveTasksRedistributionIOException() throws IOException {
        NodeConnection connection = mock(NodeConnection.class);
        Slave slave = spy(new Slave(new HashSet<>(), connection));
        when(slave.isNoTasks()).thenReturn(false);
        doThrow(new IOException()).when(slave).redistributeTasks();
        slave.onExit();
        verify(slavesStorage).remove(slave);
        verify(slave).setBadStatusForAllTasks();
        verify(slave).clearTasks();
        verify(connection).close();
    }

    @Test
    public void testOnExitHaveTasksRedistributionDistributionException() throws IOException {
        NodeConnection connection = mock(NodeConnection.class);
        Slave slave = spy(new Slave(new HashSet<>(), connection));
        when(slave.isNoTasks()).thenReturn(false);
        doThrow(new IOException(new DistributionException())).when(slave).redistributeTasks();
        slave.onExit();
        verify(slavesStorage).remove(slave);
        verify(slave).setBadStatusForAllTasks();
        verify(slave).clearTasks();
        verify(slave).cancelAllTasks();
        verify(connection).close();
    }


    @Test
    public void testRedistributeTasks() throws IOException, DistributionException {
        NodeConnection connection = mock(NodeConnection.class);
        Slave slave = spy(new Slave(new HashSet<>(), connection));
        slave.redistributeTasks();
        verify(distributionService).redistribute(slave);
    }

    @Test(expected = IOException.class)
    public void testRedistributeTasksException() throws IOException, DistributionException {
        NodeConnection connection = mock(NodeConnection.class);
        Slave slave = spy(new Slave(new HashSet<>(), connection));
        doThrow(new DistributionException()).when(distributionService).redistribute(slave);
        slave.redistributeTasks();
    }

    @Test
    public void testApplyToTasks() {

        List<NodeRequest> requestList = Arrays.asList(
                NodeRequest.stop(UUID.randomUUID()),
                NodeRequest.stop(UUID.randomUUID()),
                NodeRequest.stop(UUID.randomUUID()));
        NodeConnection connection = mock(NodeConnection.class);
        Slave slave = spy(new Slave(new HashSet<>(), connection));
        slave.addTasks(requestList);
        Applyer<NodeRequest> applyer = mock(Applyer.class);
        slave.applyToTasks(applyer);
        verify(applyer, times(requestList.size())).apply(any(NodeRequest.class));
    }
}
