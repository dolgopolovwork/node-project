package ru.babobka.nodemasterserver.applyer;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.babobka.nodemasterserver.model.ResponseStorage;
import ru.babobka.nodemasterserver.slave.AbstractNetworkSlave;
import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodeutils.container.ApplicationContainer;
import ru.babobka.nodeutils.container.Container;

import java.util.UUID;

import static org.mockito.Mockito.*;

/**
 * Created by 123 on 11.09.2017.
 */
public class StopTaskApplyerTest {
    private ResponseStorage responseStorage;

    @Before
    public void setUp() {
        responseStorage = mock(ResponseStorage.class);
        new ApplicationContainer() {
            @Override
            public void contain(Container container) {
                container.put(responseStorage);
            }
        }.contain(Container.getInstance());
    }

    @After
    public void after() {
        Container.getInstance().clear();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testStopTaskApplyerNullTaskId() {
        new StopTaskApplyer(null, mock(AbstractNetworkSlave.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testStopTaskApplyerNullSlave() {
        new StopTaskApplyer(UUID.randomUUID(), null);
    }

    @Test
    public void testApply() {
        AbstractNetworkSlave slave = mock(AbstractNetworkSlave.class);
        UUID taskId = UUID.randomUUID();
        StopTaskApplyer stopTaskApplyer = new StopTaskApplyer(taskId, slave);
        NodeRequest request = NodeRequest.regular(taskId, "test task", null);
        stopTaskApplyer.apply(request);
        verify(responseStorage).addStopResponse(taskId);
        verify(slave).removeTask(request);
    }

    @Test
    public void testApplyDifferentTaskId() {
        AbstractNetworkSlave slave = mock(AbstractNetworkSlave.class);
        UUID taskId = UUID.randomUUID();
        StopTaskApplyer stopTaskApplyer = new StopTaskApplyer(taskId, slave);
        NodeRequest request = NodeRequest.regular(UUID.randomUUID(), "test task", null);
        stopTaskApplyer.apply(request);
        verify(responseStorage, never()).addStopResponse(taskId);
        verify(slave, never()).removeTask(request);

    }
}
