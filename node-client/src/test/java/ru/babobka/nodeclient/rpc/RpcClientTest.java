package ru.babobka.nodeclient.rpc;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodeserials.NodeResponse;
import ru.babobka.nodeserials.data.Data;
import ru.babobka.nodeserials.enumerations.RequestStatus;
import ru.babobka.nodeserials.serializer.NodeSerializer;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class RpcClientTest {

    private Connection connection;
    private Channel channel;
    private ConnectionFactory connectionFactory;

    @Before
    public void setUp() throws IOException, TimeoutException {
        connection = mock(Connection.class);
        channel = mock(Channel.class);
        connectionFactory = mock(ConnectionFactory.class);
        when(connectionFactory.newConnection()).thenReturn(connection);
        when(connection.createChannel()).thenReturn(channel);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidPort() throws IOException, TimeoutException {
        new RpcClient("localhost", -1, "queue");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEmptyHost() throws IOException, TimeoutException {
        new RpcClient("", 123, "queue");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEmptyReplyQueue() throws IOException, TimeoutException {
        new RpcClient("localhost", 123, "");
    }

    @Test
    public void testClose() throws IOException, TimeoutException {
        String replyQueue = "test";
        RpcClient rpcClient = new RpcClient("localhost", 123, replyQueue, connectionFactory);
        rpcClient.close();
        verify(connection).close();
    }

    @Test
    public void testCancel() throws IOException, TimeoutException {
        String replyQueue = "test";
        UUID taskID = UUID.randomUUID();
        RpcClient rpcClient = spy(new RpcClient("localhost", 123, replyQueue, connectionFactory));
        rpcClient.cancelCall(taskID);
        ArgumentCaptor<byte[]> request = ArgumentCaptor.forClass(byte[].class);
        verify(channel).basicPublish(eq(""), eq(RpcClient.RPC_QUEUE_NAME), eq(null), request.capture());
        NodeRequest actualRequest = NodeSerializer.deserializeRequest(request.getValue());
        assertEquals(RequestStatus.STOP, actualRequest.getRequestStatus());
        assertEquals(taskID, actualRequest.getTaskId());
        verify(rpcClient, never()).reserveResponse(anyString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCallBadRequestStatusStop() throws IOException, TimeoutException, InterruptedException {
        String replyQueue = "test";
        RpcClient rpcClient = new RpcClient("localhost", 123, replyQueue, connectionFactory);
        rpcClient.call(NodeRequest.stop(UUID.randomUUID()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCallBadRequestStatusTime() throws IOException, TimeoutException, InterruptedException {
        String replyQueue = "test";
        RpcClient rpcClient = new RpcClient("localhost", 123, replyQueue, connectionFactory);
        rpcClient.call(NodeRequest.time());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCallBadRequestStatusHeartBeat() throws IOException, TimeoutException, InterruptedException {
        String replyQueue = "test";
        RpcClient rpcClient = new RpcClient("localhost", 123, replyQueue, connectionFactory);
        rpcClient.call(NodeRequest.heartBeat());
    }

    @Test
    public void testCall() throws IOException, TimeoutException, InterruptedException {
        String replyQueue = "test";
        RpcClient rpcClient = spy(new RpcClient("localhost", 123, replyQueue, connectionFactory));
        doNothing().when(rpcClient).reserveResponse(anyString());
        NodeResponse response = mock(NodeResponse.class);
        doReturn(response).when(rpcClient).getResponse(anyString());
        NodeRequest request = NodeRequest.regular(UUID.randomUUID(), "test task", new Data());
        assertEquals(response, rpcClient.call(request));
        verify(rpcClient).reserveResponse(anyString());
        ArgumentCaptor<byte[]> serializedRequest = ArgumentCaptor.forClass(byte[].class);
        ArgumentCaptor<AMQP.BasicProperties> properties = ArgumentCaptor.forClass(AMQP.BasicProperties.class);
        verify(channel).basicPublish(eq(""), eq(RpcClient.RPC_QUEUE_NAME), properties.capture(), serializedRequest.capture());
        assertEquals(replyQueue, properties.getValue().getReplyTo());
        NodeRequest deserializedRequest = NodeSerializer.deserializeRequest(serializedRequest.getValue());
        assertEquals(request, deserializedRequest);
    }
}
