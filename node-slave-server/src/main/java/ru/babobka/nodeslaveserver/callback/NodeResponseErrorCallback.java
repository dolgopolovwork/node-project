package ru.babobka.nodeslaveserver.callback;

import lombok.NonNull;
import org.apache.commons.lang.NotImplementedException;
import org.apache.log4j.Logger;
import ru.babobka.nodeserials.NodeRequest;
import ru.babobka.nodeserials.NodeResponse;
import ru.babobka.nodeserials.enumerations.ResponseStatus;
import ru.babobka.nodetask.exception.TaskExecutionException;
import ru.babobka.nodeutils.func.Callback;
import ru.babobka.nodeutils.network.NodeConnection;

import java.io.IOException;

/**
 * Created by 123 on 30.03.2019.
 */
public class NodeResponseErrorCallback implements Callback<Exception> {

    private static final Logger logger = Logger.getLogger(NodeResponseErrorCallback.class);
    private final NodeRequest request;
    private final NodeConnection nodeConnection;

    public NodeResponseErrorCallback(@NonNull NodeRequest request,
                                     @NonNull NodeConnection nodeConnection) {
        this.request = request;
        this.nodeConnection = nodeConnection;
    }

    @Override
    public void callback(Exception exception) {
        if (exception instanceof IOException) {
            logger.error("response for request id " + request.getId() + " wasn't sent ", exception);
            return;
        }
        logger.error("exception thrown", exception);
        try {
            if (exception instanceof TaskExecutionException) {
                sendTaskError((TaskExecutionException) exception);
            } else {
                sendSystemError();
            }
        } catch (IOException ioException) {
            logger.error(ioException);
        }
    }

    void sendTaskError(TaskExecutionException exception) throws IOException {
        NodeResponse errorResponse = createErrorResponse(request, exception);
        nodeConnection.send(errorResponse);
        logger.info("error response " + errorResponse + " has been sent");
    }

    void sendSystemError() throws IOException {
        nodeConnection.send(NodeResponse.systemError(request));
    }

    //This code is duplicated
    static NodeResponse createErrorResponse(NodeRequest request, TaskExecutionException taskExecutionException) {
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
}
