package ru.babobka.dlp.model.regular;

import org.junit.Test;
import ru.babobka.dlp.model.regular.PollardDlpDistributor;
import ru.babobka.dlp.task.Params;
import ru.babobka.nodeserials.NodeRequest;

import java.math.BigInteger;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by 123 on 28.01.2018.
 */
public class PollardDlpDistributorTest {
    private PollardDlpDistributor dlpDistributor = new PollardDlpDistributor();

    @Test
    public void testDistributeImpl() {
        int nodes = 10;
        NodeRequest mainRequest = mock(NodeRequest.class);
        UUID taskId = UUID.randomUUID();
        when(mainRequest.getTaskId()).thenReturn(taskId);
        when(mainRequest.getTaskName()).thenReturn("abc");
        when(mainRequest.getDataValue(Params.X.getValue())).thenReturn(BigInteger.ONE);
        when(mainRequest.getDataValue(Params.Y.getValue())).thenReturn(BigInteger.ONE);
        when(mainRequest.getDataValue(Params.MOD.getValue())).thenReturn(BigInteger.ONE);
        List<NodeRequest> requestList = dlpDistributor.distributeImpl(mainRequest, nodes);
        assertEquals(requestList.size(), nodes);
        for (NodeRequest request : requestList) {
            assertEquals(request.getTaskId(), taskId);
            assertEquals(request.getTaskName(), request.getTaskName());
        }
    }
}
