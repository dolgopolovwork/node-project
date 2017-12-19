package ru.babobka.primecounter.model;

import org.junit.Test;
import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodeserials.enumerations.RequestStatus;
import ru.babobka.primecounter.task.Params;

import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by 123 on 23.10.2017.
 */
public class PrimeCounterDistributorTest {

    private final PrimeCounterDistributor primeCounterDistributor = new PrimeCounterDistributor();

    @Test
    public void testDistribute() {
        UUID taskId = UUID.randomUUID();
        long begin = 0L;
        long end = 1_000L;
        NodeRequest request = mock(NodeRequest.class);
        when(request.getTaskId()).thenReturn(taskId);
        when(request.getDataValue(Params.BEGIN.getValue())).thenReturn(begin);
        when(request.getDataValue(Params.END.getValue())).thenReturn(end);
        for (int nodes = 1; nodes < 10; nodes++) {
            List<NodeRequest> requests = primeCounterDistributor.distribute(request, nodes);
            assertEquals(requests.size(), nodes);
            for (NodeRequest distributedRequest : requests) {
                assertEquals(distributedRequest.getRequestStatus(), RequestStatus.NORMAL);
                assertEquals(distributedRequest.getTaskId(), taskId);
            }
            assertEquals((long) requests.get(0).getDataValue(Params.BEGIN.getValue()), begin);
            assertEquals((long) requests.get(nodes - 1).getDataValue(Params.END.getValue()), end);
        }
    }
}