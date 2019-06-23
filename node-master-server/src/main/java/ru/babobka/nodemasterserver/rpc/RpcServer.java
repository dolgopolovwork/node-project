package ru.babobka.nodemasterserver.rpc;

import com.rabbitmq.client.*;
import com.rabbitmq.client.impl.DefaultExceptionHandler;
import org.apache.log4j.Logger;
import ru.babobka.nodeconfigs.master.MasterServerConfig;
import ru.babobka.nodeconfigs.master.RmqConfig;
import ru.babobka.nodemasterserver.mapper.NodeResponseErrorMapper;
import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodeserials.NodeResponse;
import ru.babobka.nodeserials.enumerations.RequestStatus;
import ru.babobka.nodeserials.exception.NodeSerializationException;
import ru.babobka.nodeserials.serializer.NodeSerializer;
import ru.babobka.nodetask.service.TaskService;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.thread.PrettyNamedThreadPoolFactory;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

public class RpcServer implements Closeable {
    private static final String RPC_QUEUE_NAME = "rpc_queue";
    private static final DefaultExceptionHandler rmqExceptionHandler = new RmqErrorHandler();
    private static final Logger logger = Logger.getLogger(RpcServer.class);
    private final MasterServerConfig masterServerConfig = Container.getInstance().get(MasterServerConfig.class);
    private final TaskService taskService = Container.getInstance().get(TaskService.class);
    private final NodeResponseErrorMapper nodeResponseErrorMapper = Container.getInstance().get(NodeResponseErrorMapper.class);
    private final ExecutorService rpcConsumerThreadPool = PrettyNamedThreadPoolFactory.singleThreadPool("rmq-consumer");
    private final ExecutorService rpcExecutorThreadPool = PrettyNamedThreadPoolFactory.fixedThreadPool("rpc-executor");
    private final Connection rmqConnection;
    private final Channel channel;
    private final AtomicBoolean started = new AtomicBoolean();

    public RpcServer() throws IOException {
        try {
            rmqConnection = createConnection(masterServerConfig.getRmq());
        } catch (TimeoutException ex) {
            throw new IOException("Cannot create connection", ex);
        }
        channel = rmqConnection.createChannel();
        channel.queueDeclare(RPC_QUEUE_NAME, true, false, false, null);
    }

    public void start() throws IOException {
        if (!started.compareAndSet(false, true)) {
            throw new IllegalStateException("rpc server has been started already");
        }
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            AMQP.BasicProperties replyProps = new AMQP.BasicProperties
                    .Builder()
                    .correlationId(delivery.getProperties().getCorrelationId())
                    .build();
            try {
                NodeRequest request = NodeSerializer.deserializeRequest(delivery.getBody());
                logger.info("got rpc request " + request);
                if (request.getRequestStatus() == RequestStatus.STOP) {
                    taskService.cancelTask(request.getTaskId(),
                            result -> logger.info("task " + request.getTaskId() + " has been successfully stopped"),
                            error -> logger.error("cannot stop task " + request.getTaskId(), error));
                } else {
                    rpcExecutorThreadPool.submit(() -> executeRequest(request, delivery, replyProps));
                }
            } catch (NodeSerializationException e) {
                logger.error("cannot deserialize request. request will be ignored.", e);
            }
        };
        channel.basicConsume(RPC_QUEUE_NAME, false, deliverCallback, (consumerTag -> {
        }));
        logger.info("rpc server has been started");
    }

    private void executeRequest(NodeRequest request,
                                Delivery delivery,
                                AMQP.BasicProperties replyProps) {

        taskService.executeTask(request, result -> {
            if (result.wasStopped()) {
                sendResponse(NodeResponse.stopped(request), delivery, replyProps);
            } else {
                sendResponse(
                        NodeResponse.normal(result.getData(), request, result.getTimeTakes()), delivery, replyProps);
            }
        }, error -> sendResponse(nodeResponseErrorMapper.createErrorResponse(request, error), delivery, replyProps));
    }

    private void sendResponse(NodeResponse response,
                              Delivery delivery,
                              AMQP.BasicProperties replyProps) {
        try {
            channel.basicPublish("",
                    delivery.getProperties().getReplyTo(),
                    replyProps,
                    NodeSerializer.serializeResponse(response));
            channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
        } catch (Exception e) {
            logger.error("cannot send response " + response, e);
        }
    }

    private Connection createConnection(RmqConfig rmqConfig) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(rmqConfig.getHost());
        factory.setPort(rmqConfig.getPort());
        factory.setExceptionHandler(rmqExceptionHandler);
        return factory.newConnection(rpcConsumerThreadPool);
    }

    @Override
    public void close() {
        try {
            channel.close();
        } catch (Exception ex) {
            logger.error("Cannot close channel", ex);
        }
        try {
            rmqConnection.close();
        } catch (Exception ex) {
            logger.error("Cannot close rmq connection", ex);

        }
        rpcConsumerThreadPool.shutdownNow();
        rpcExecutorThreadPool.shutdownNow();
    }
}
