package ru.babobka.nodemasterserver.client;

import org.apache.log4j.Logger;
import ru.babobka.nodeconfigs.master.MasterServerConfig;
import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodeserials.NodeResponse;
import ru.babobka.nodeserials.enumerations.ResponseStatus;
import ru.babobka.nodetask.exception.TaskExecutionException;
import ru.babobka.nodetask.service.TaskExecutionResult;
import ru.babobka.nodetask.service.TaskService;
import ru.babobka.nodeutils.container.Container;
import ru.babobka.nodeutils.network.NodeConnection;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static ru.babobka.nodeutils.util.StreamUtil.isClosedConnectionException;

/**
 * Created by 123 on 28.10.2017.
 */
public class Client extends AbstractClient {

    private final ClientStorage clientStorage = Container.getInstance().get(ClientStorage.class);
    private final MasterServerConfig config = Container.getInstance().get(MasterServerConfig.class);
    private static final Logger logger = Logger.getLogger(Client.class);
    private final TaskService taskService = Container.getInstance().get(TaskService.class);
    private final AtomicInteger processedRequests = new AtomicInteger();
    private volatile boolean done;

    Client(NodeConnection connection, List<NodeRequest> requests) {
        super(connection, requests);
    }

    @Override
    public void run() {
        clientStorage.add(this);
        try {
            runExecution();
            processConnection();
        } finally {
            clientStorage.remove(this);
            close();
        }
    }

    void runExecution() {
        for (NodeRequest request : requests) {
            new Thread(new ExecutionRunnable(request)).start();
        }
    }

    void processConnection() {
        try {
            while (!isDone()) {
                connection.receive();
                connection.setReadTimeOut(config.getTime().getRequestReadTimeOutMillis());
            }
        } catch (IOException e) {
            if (!isDone()) {
                cancelTask();
                if (!connection.isClosed() && !isClosedConnectionException(e)) {
                    logger.error("exception thrown", e);
                }
            }
        }
    }

    void close() {
        connection.close();
    }

    void cancelTask() {
        for (NodeRequest request : requests) {
            taskService.cancelTask(request.getTaskId(), canceled -> {
                if (canceled) {
                    logger.info("task " + request.getTaskId() + " has been stopped");
                } else {
                    logger.info("task " + request.getTaskId() + " has NOT been stopped");
                }
            }, error -> {
                logger.error("exception thrown", error);
            });
        }
        setDone();
    }

    void sendFailed(TaskExecutionException taskExecutionException) {
        for (NodeRequest request : requests) {
            try {
                connection.send(createErrorResponse(request, taskExecutionException));
            } catch (IOException e) {
                logger.error("cannot send failed response for task " + request.getTaskId(), e);
            }
        }
    }

    void sendNormal(TaskExecutionResult result, NodeRequest request) {
        try {
            connection.send(NodeResponse.normal(result.getData(), request, result.getTimeTakes()));
        } catch (IOException e) {
            logger.error("cannot send response for task " + request.getTaskId(), e);
        }
    }

    void sendStopped() {
        for (NodeRequest request : requests) {
            try {
                connection.send(NodeResponse.stopped(request));
            } catch (IOException e) {
                logger.error("cannot send stop for task " + request.getTaskId(), e);
            }
        }
    }

    boolean isDone() {
        return done;
    }

    void setDone() {
        this.done = true;
    }

    void executeTask(NodeRequest request) throws IOException {
        taskService.executeTask(request, result -> {
            if (result.wasStopped()) {
                sendStopped();
            } else {
                sendNormal(result, request);
            }
            if (processedRequests.incrementAndGet() == requests.size()) {
                setDone();
            }
        }, error -> {
            logger.error("exception thrown", error);
            sendFailed(error);
        });
    }

    //This code is duplicated
    private NodeResponse createErrorResponse(NodeRequest request, TaskExecutionException taskExecutionException) {
        String message = taskExecutionException.getMessage();
        if (taskExecutionException.getExecutionStatus() == ResponseStatus.SYSTEM_ERROR) {
            return NodeResponse.systemError(request, message);
        } else if (taskExecutionException.getExecutionStatus() == ResponseStatus.VALIDATION_ERROR) {
            return NodeResponse.validationError(request, message);
        } else if (taskExecutionException.getExecutionStatus() == ResponseStatus.NO_NODES) {
            return NodeResponse.noNodesError(request, message);
        }
        throw new NotImplementedException();
    }

    private class ExecutionRunnable implements Runnable {

        private final NodeRequest request;

        ExecutionRunnable(NodeRequest request) {
            this.request = request;
        }

        @Override
        public void run() {
            try {
                executeTask(request);
            } catch (IOException e) {
                logger.error("exception thrown", e);
            }
        }
    }
}
