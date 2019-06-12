package ru.babobka.nodeclient.rpc;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import lombok.NonNull;
import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodeserials.NodeResponse;
import ru.babobka.nodeserials.enumerations.RequestStatus;
import ru.babobka.nodeserials.serializer.NodeSerializer;
import ru.babobka.nodeutils.util.TextUtil;

import java.io.Closeable;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

public class RpcClient implements Closeable {

    private static final RpcResponsePool rpcResponsePool = new RpcResponsePool();
    private final Connection connection;
    private final Channel channel;
    private final String replyQueue;
    private static final String RPC_QUEUE = "rpc_queue";

    // TODO добавь слушатель пропущенных сообщений
    public RpcClient(@NonNull String host, int port, @NonNull String replyQueue) throws IOException, TimeoutException {
        if (TextUtil.isEmpty(host)) {
            throw new IllegalArgumentException("host was not set");
        } else if (!TextUtil.isValidPort(port)) {
            throw new IllegalArgumentException("not valid port " + port);
        } else if (TextUtil.isEmpty(replyQueue)) {
            throw new IllegalArgumentException("replyQueue was not set");
        }
        this.replyQueue = replyQueue;
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(host);
        factory.setPort(port);
        connection = factory.newConnection();
        channel = connection.createChannel();
        initChannel();
    }

    public NodeResponse call(@NonNull NodeRequest request) throws IOException, InterruptedException {
        if (!(request.getRequestStatus() == RequestStatus.RACE || request.getRequestStatus() == RequestStatus.NORMAL)) {
            throw new IllegalArgumentException("invalid request status " + request.getRequestStatus().name());
        }
        final String corrId = UUID.randomUUID().toString();
        AMQP.BasicProperties props = new AMQP.BasicProperties
                .Builder()
                .correlationId(corrId)
                .replyTo(replyQueue)
                .build();

        rpcResponsePool.reserveResponse(corrId);
        channel.basicPublish("", RPC_QUEUE, props, NodeSerializer.serializeRequest(request));
        return rpcResponsePool.getResponse(corrId);
    }

    public void cancelCall(@NonNull UUID taskId) throws IOException {
        channel.basicPublish("", RPC_QUEUE, null, NodeSerializer.serializeRequest(NodeRequest.stop(taskId)));
    }

    private void initChannel() throws IOException {
        channel.queueDeclare(replyQueue, true, false, false, null);
        channel.basicConsume(replyQueue, true, (consumerTag, delivery) -> {
            rpcResponsePool.putResponse(
                    delivery.getProperties().getCorrelationId(),
                    NodeSerializer.deserializeResponse(delivery.getBody()));

        }, consumerTag -> {
        });
    }

    @Override
    public void close() throws IOException {
        connection.close();
    }
}
