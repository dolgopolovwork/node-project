package ru.babobka.nodemasterserver.service;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.babobka.nodemasterserver.exception.DistributionException;
import ru.babobka.nodemasterserver.slave.AbstractNetworkSlave;
import ru.babobka.nodemasterserver.slave.Slave;
import ru.babobka.nodemasterserver.slave.SlavesStorage;
import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodeutils.container.ApplicationContainer;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.logger.SimpleLogger;

import java.io.IOException;
import java.util.*;

import static org.mockito.Mockito.*;

/**
 * Created by 123 on 05.09.2017.
 */
public class DistributionServiceTest {
    private SimpleLogger simpleLogger;
    private SlavesStorage slavesStorage;
    private DistributionService distributionService;

    @Before
    public void setUp() {
        simpleLogger = mock(SimpleLogger.class);
        slavesStorage = mock(SlavesStorage.class);
        new ApplicationContainer() {
            @Override
            public void contain(Container container) {
                container.put(simpleLogger);
                container.put(slavesStorage);
            }
        }.contain(Container.getInstance());
        distributionService = spy(DistributionService.class);
    }

    @After
    public void tearDown() {
        Container.getInstance().clear();
    }

    @Test(expected = DistributionException.class)
    public void testBroadcastStopRequestsNullSlaves() throws DistributionException {
        distributionService.broadcastStopRequests(null, UUID.randomUUID());
    }

    @Test(expected = DistributionException.class)
    public void testBroadcastStopRequestsEmptySlaves() throws DistributionException {
        distributionService.broadcastStopRequests(new ArrayList<>(), UUID.randomUUID());
    }

    @Test
    public void testBroadcastStopRequests() throws DistributionException, IOException {
        UUID taskID = UUID.randomUUID();
        Slave slave = mock(Slave.class);
        List<Slave> slaveList = Arrays.asList(slave, slave, slave);
        distributionService.broadcastStopRequests(slaveList, taskID);
        verify(slave, times(slaveList.size())).stopTask(taskID);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRedistributeNullSlaves() throws DistributionException {
        distributionService.redistribute(null);
    }

    @Test
    public void testRedistributeNoTasks() throws DistributionException {
        AbstractNetworkSlave abstractNetworkSlave = mock(AbstractNetworkSlave.class);
        when(abstractNetworkSlave.isNoTasks()).thenReturn(true);
        distributionService.redistribute(abstractNetworkSlave);
        verify(distributionService, never()).broadcastRequests(anyString(), anyList());
    }

    @Test(expected = DistributionException.class)
    public void testRedistributeException() throws DistributionException {
        AbstractNetworkSlave abstractNetworkSlave = mock(AbstractNetworkSlave.class);
        Map<String, List<NodeRequest>> groupedTasks = new HashMap<>();
        List<NodeRequest> requests = Arrays.asList(NodeRequest.heartBeatRequest(), NodeRequest.heartBeatRequest());
        groupedTasks.put("testTask", requests);
        when(abstractNetworkSlave.getRequestsGroupedByTasks()).thenReturn(groupedTasks);
        doThrow(new DistributionException()).when(distributionService).broadcastRequests(anyString(), anyList());
        distributionService.redistribute(abstractNetworkSlave);
    }

    @Test(expected = DistributionException.class)
    public void testRedistribute() throws DistributionException {
        AbstractNetworkSlave abstractNetworkSlave = mock(AbstractNetworkSlave.class);
        Map<String, List<NodeRequest>> groupedTasks = new HashMap<>();
        List<NodeRequest> requests = Arrays.asList(NodeRequest.heartBeatRequest(), NodeRequest.heartBeatRequest());
        groupedTasks.put("testTask", requests);
        when(abstractNetworkSlave.getRequestsGroupedByTasks()).thenReturn(groupedTasks);
        distributionService.redistribute(abstractNetworkSlave);
        verify(distributionService).broadcastRequests("testTask", requests);
    }

    @Test
    public void testBroadcastStopRequestsIOException() throws IOException, DistributionException {
        Slave slave = mock(Slave.class);
        doThrow(new IOException()).when(slave).stopTask(any(UUID.class));
        List<Slave> slaves = Arrays.asList(slave, slave, slave);
        distributionService.broadcastStopRequests(slaves, UUID.randomUUID());
        verify(simpleLogger, times(slaves.size())).error(any(Exception.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBroadcastRequestsNegativeMaxRetry() throws DistributionException {
        List<NodeRequest> nodeRequests = Arrays.asList(NodeRequest.heartBeatRequest(), NodeRequest.heartBeatRequest());
        distributionService.broadcastRequests("taskName", nodeRequests, 0, -1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBroadcastRequestsNullRequests() throws DistributionException {
        distributionService.broadcastRequests("taskName", null, 0, 5);
    }

    @Test(expected = DistributionException.class)
    public void testBroadcastRequestsNoSlavesToDistribute() throws DistributionException {
        when(slavesStorage.getList(anyString())).thenReturn(new ArrayList<>());
        List<NodeRequest> nodeRequests = Arrays.asList(NodeRequest.heartBeatRequest(), NodeRequest.heartBeatRequest());
        distributionService.broadcastRequests("taskName", nodeRequests, 0, 5);
    }

    @Test
    public void testBroadcastRequestsMoreSlaves() throws DistributionException, IOException {
        Slave slave = mock(Slave.class);
        List<Slave> slaves = Arrays.asList(slave, slave, slave);
        when(slavesStorage.getList(anyString())).thenReturn(slaves);
        List<NodeRequest> nodeRequests = Arrays.asList(NodeRequest.heartBeatRequest(), NodeRequest.heartBeatRequest());
        distributionService.broadcastRequests("taskName", nodeRequests, 0, 5);
        verify(slave, times(nodeRequests.size())).executeTask(any(NodeRequest.class));
    }

    @Test
    public void testBroadcastRequestsMoreRequests() throws DistributionException, IOException {
        Slave slave = mock(Slave.class);
        List<Slave> slaves = Arrays.asList(slave, slave);
        when(slavesStorage.getList(anyString())).thenReturn(slaves);
        List<NodeRequest> nodeRequests = Arrays.asList(NodeRequest.heartBeatRequest(), NodeRequest.heartBeatRequest(), NodeRequest.heartBeatRequest());
        distributionService.broadcastRequests("taskName", nodeRequests, 0, 5);
        verify(slave, times(nodeRequests.size())).executeTask(any(NodeRequest.class));
    }

}
