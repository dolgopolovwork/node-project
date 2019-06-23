package ru.babobka.nodeclient.rpc;

import org.junit.Test;
import ru.babobka.nodeserials.NodeResponse;

import java.util.UUID;
import java.util.concurrent.TimeoutException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class RpcResponsePoolTest {

    private final RpcResponsePool rpcResponsePool = new RpcResponsePool();

    @Test(expected = IllegalStateException.class)
    public void testGetResponseNotReserved() throws InterruptedException, TimeoutException {
        rpcResponsePool.getResponse(UUID.randomUUID().toString());
    }

    @Test
    public void testGetResponse() throws InterruptedException, TimeoutException {
        UUID correlationID = UUID.randomUUID();
        NodeResponse response = mock(NodeResponse.class);
        rpcResponsePool.reserveResponse(correlationID.toString());
        rpcResponsePool.putResponse(correlationID.toString(), response);
        assertEquals(response, rpcResponsePool.getResponse(correlationID.toString()));
    }

    @Test(expected = IllegalStateException.class)
    public void testGetResponseDoubleCall() throws InterruptedException, TimeoutException {
        UUID correlationID = UUID.randomUUID();
        NodeResponse response = mock(NodeResponse.class);
        rpcResponsePool.reserveResponse(correlationID.toString());
        rpcResponsePool.putResponse(correlationID.toString(), response);
        assertEquals(response, rpcResponsePool.getResponse(correlationID.toString()));
        rpcResponsePool.getResponse(correlationID.toString());
    }

    @Test(expected = TimeoutException.class)
    public void testGetResponseNoPut() throws InterruptedException, TimeoutException {
        UUID correlationID = UUID.randomUUID();
        rpcResponsePool.reserveResponse(correlationID.toString());
        rpcResponsePool.getResponse(correlationID.toString(), 2_000);
    }
}
