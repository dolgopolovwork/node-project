package ru.babobka.nodemasterserver.listener;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.babobka.nodemasterserver.exception.DistributionException;
import ru.babobka.nodemasterserver.service.DistributionService;
import ru.babobka.nodemasterserver.slave.Slave;
import ru.babobka.nodemasterserver.slave.SlavesStorage;
import ru.babobka.nodeserials.NodeResponse;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.logger.SimpleLogger;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;

/**
 * Created by 123 on 10.09.2017.
 */
public class OnRaceStyleTaskIsReadyTest {
    private DistributionService distributionService;
    private SimpleLogger logger;
    private SlavesStorage slavesStorage;
    private OnRaceStyleTaskIsReady onRaceStyleTaskIsReady;

    @Before
    public void setUp() {
        distributionService = mock(DistributionService.class);
        logger = mock(SimpleLogger.class);
        slavesStorage = mock(SlavesStorage.class);
        Container.getInstance().put(distributionService);
        Container.getInstance().put(logger);
        Container.getInstance().put(slavesStorage);
        onRaceStyleTaskIsReady = new OnRaceStyleTaskIsReady();
    }

    @After
    public void tearDown() {
        Container.getInstance().clear();
    }

    @Test
    public void testOnResponse() throws DistributionException {
        Slave slave = mock(Slave.class);
        List<Slave> slaves = Arrays.asList(slave, slave, slave);
        UUID taskId = UUID.randomUUID();
        NodeResponse response = NodeResponse.dummy(taskId);
        when(slavesStorage.getListByTaskId(response)).thenReturn(slaves);
        onRaceStyleTaskIsReady.onResponse(response);
        verify(logger).info(anyString());
        verify(distributionService).broadcastStopRequests(slaves, response.getTaskId());
    }

}
