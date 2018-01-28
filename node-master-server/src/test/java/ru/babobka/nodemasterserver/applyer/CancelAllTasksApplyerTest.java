package ru.babobka.nodemasterserver.applyer;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.babobka.nodemasterserver.exception.DistributionException;
import ru.babobka.nodemasterserver.service.DistributionService;
import ru.babobka.nodemasterserver.slave.SlavesStorage;
import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.logger.SimpleLogger;

import static org.mockito.Mockito.*;

/**
 * Created by 123 on 11.09.2017.
 */
public class CancelAllTasksApplyerTest {

    private SimpleLogger logger;
    private DistributionService distributionService;
    private SlavesStorage slavesStorage;
    private CancelAllTasksApplyer cancelAllTasksApplyer;

    @Before
    public void setUp() {
        logger = mock(SimpleLogger.class);
        distributionService = mock(DistributionService.class);
        slavesStorage = mock(SlavesStorage.class);
        Container.getInstance().put(logger);
        Container.getInstance().put(distributionService);
        Container.getInstance().put(slavesStorage);
        cancelAllTasksApplyer = new CancelAllTasksApplyer();
    }

    @After
    public void tearDown() {
        Container.getInstance().clear();
    }

    @Test
    public void testApply() throws DistributionException {
        NodeRequest request = NodeRequest.heartBeatRequest();
        cancelAllTasksApplyer.apply(request);
        verify(distributionService).broadcastStopRequests(anyList(), eq(request.getTaskId()));
    }
}
