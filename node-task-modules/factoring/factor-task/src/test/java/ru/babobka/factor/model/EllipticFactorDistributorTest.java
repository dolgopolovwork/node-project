package ru.babobka.factor.model;

import org.junit.Test;
import ru.babobka.factor.task.Params;
import ru.babobka.nodeserials.NodeRequest;

import java.math.BigInteger;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by 123 on 20.06.2017.
 */
public class EllipticFactorDistributorTest {

    private final EllipticFactorDistributor ellipticFactorDistributor = new EllipticFactorDistributor();

    @Test
    public void testDistribute() {
        NodeRequest request = mock(NodeRequest.class);
        when(request.getDataValue(Params.NUMBER.getValue())).thenReturn(BigInteger.valueOf(123));
        UUID id = UUID.randomUUID();
        when(request.getTaskId()).thenReturn(id);
        int nodes = 5;
        List<NodeRequest> nodeRequests = ellipticFactorDistributor.distribute(request, nodes);
        assertEquals(nodeRequests.size(), nodes);
        for (NodeRequest distributedRequest : nodeRequests) {
            assertEquals(distributedRequest.getTaskId(), id);
        }
    }
}